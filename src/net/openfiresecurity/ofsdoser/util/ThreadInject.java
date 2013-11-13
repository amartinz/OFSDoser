/*
 * Copyright (c) 2013. Alexander Martinz.
 */

package net.openfiresecurity.ofsdoser.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class ThreadInject extends Thread {
    private final String mUrlString;
    private final String mPost;
    private int mState;

    public ThreadInject(String url, String post) {
        mUrlString = url;
        mPost = post;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(new Random().nextInt(1000));
        } catch (InterruptedException ignored) {
        }
        setState(0);
        try {
            URL url = new URL(mUrlString);
            setState(1);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            setState(2);
            OutputStreamWriter osw = new OutputStreamWriter(
                    conn.getOutputStream());
            setState(3);
            osw.write(mPost);
            osw.flush();
            osw.close();
            setState(4);
            InputStream in = conn.getInputStream();
            setState(5);
            ThreadInject.copy(in);
            setState(6);
            in.close();
        } catch (Throwable ignored) {
        }
        setState(7);
    }

    public int getLocalState() {
        return mState;
    }

    void setState(int s) {
        mState = s;
    }

    private static void copy(InputStream in)
            throws IOException {
        byte[] buffer = new byte[4096];
        while (true) {
            int count = in.read(buffer);
            if (count == -1) {
                break;
            }
            System.out.write(buffer, 0, count);
        }
    }

}