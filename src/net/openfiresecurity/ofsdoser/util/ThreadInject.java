/*
 * Copyright (c) 2013. Alexander Martinz.
 */

package net.openfiresecurity.ofsdoser.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.SecureRandom;

public class ThreadInject extends Thread {
    private final String mUrlString;
    private final String mPost;
    private int mState;
    private OutputStreamWriter osw;

    public ThreadInject(String url, String post) {
        mUrlString = url;
        mPost = post;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(new SecureRandom().nextInt(1000));
        } catch (InterruptedException ignored) {
        }
        setState(0);
        try {
            URL url = new URL(mUrlString);
            setState(1);
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(PreferenceStorage.DOS_TIMEOUT);
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            setState(2);
            osw = new OutputStreamWriter(conn.getOutputStream());
            setState(3);
            osw.write(mPost);
            osw.flush();
            setState(4);
            InputStream in = conn.getInputStream();
            setState(5);
            ThreadInject.copy(in, System.out, 4096);
            setState(6);
            in.close();
        } catch (Throwable ignored) {
        } finally {
            try {
                osw.close();
            } catch (Exception ignored) {
            }
        }
        setState(7);
    }

    public int getLocalState() {
        return mState;
    }

    void setState(int s) {
        mState = s;
    }

    private static void copy(InputStream in, OutputStream out, int bufferSize)
            throws IOException {
        byte[] buffer = new byte[bufferSize];
        while (true) {
            int count = in.read(buffer);
            if (count == -1) {
                break;
            }
            out.write(buffer, 0, count);
        }
    }

}
