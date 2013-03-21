package net.openfiresecurity.ofsdoser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class threadinject extends Thread {
	String urlString;
	String post;
	int state;

	threadinject(String url, String post) {
		urlString = url;
		this.post = post;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(new Random().nextInt(1000));
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		setState(0);
		try {
			URL url = new URL(urlString);
			setState(1);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			setState(2);
			OutputStreamWriter osw = new OutputStreamWriter(
					conn.getOutputStream());
			setState(3);
			osw.write(post);
			osw.flush();
			osw.close();
			setState(4);
			InputStream in = conn.getInputStream();
			setState(5);
			threadinject.copy(in, System.out, 4096);
			setState(6);
			in.close();
		} catch (Throwable e) {
			System.out.println("threadinject.run():" + e);
		}
		setState(7);
	}

	public int getLocalState() {
		return state;
	}

	void setState(int s) {
		state = s;
	}

	public static void copy(InputStream in, OutputStream out, int bufferSize)
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