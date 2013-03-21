/*
 * Old Installer, got useless after i fixed all.
 * Maybe its usefull for you though.
 */
//package net.openfiresecurity.ofsdoser.utils;
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//
//import net.openfiresecurity.ofsdoser.R;
//import android.content.Context;
//import android.content.res.Resources;
//
//public class Install extends Thread {
//
//	private static final int BUFFER_SIZE = 8192;
//
//	private class InstallerBinary {
//		public transient String filename;
//		public transient int files[];
//		public transient boolean executable;
//
//		public InstallerBinary(final String filename, final int files[],
//				final boolean executable) {
//			this.filename = filename;
//			this.files = files.clone();
//			this.executable = executable;
//		}
//	}
//
//	private final transient InstallerBinary installerBinaries[] = { new InstallerBinary(
//			"resolver", new int[] { R.raw.resolver }, true) };
//
//	private final transient String binaryDirectory;
//	private final transient Resources appResources;
//	private final transient boolean hasRoot;
//
//	/**
//	 * 
//	 * @param context
//	 *            Context of the activity launching this installer.
//	 * @param binaryDirectory
//	 *            Location to save binaries.
//	 * @param hasRoot
//	 *            Does user have root access or not.
//	 */
//	public Install(final Context context, final String binaryDirectory,
//			final boolean hasRoot) {
//		super();
//		appResources = context.getResources();
//		this.binaryDirectory = binaryDirectory;
//		this.hasRoot = hasRoot;
//	}
//
//	private void deleteExistingFile(final File myFile) {
//		if (myFile.exists()) {
//			// Error.log(myFile.getAbsolutePath() + " exists. Deleting...");
//			if (myFile.delete()) {
//				// Error.log("...deleted.");
//			} else {
//				// Error.log("...unable to delete.");
//			}
//		}
//	}
//
//	private void writeNewFile(final File myFile, final int fileResources[]) {
//		final byte[] buf = new byte[Install.BUFFER_SIZE];
//
//		OutputStream out;
//		try {
//			out = new FileOutputStream(myFile);
//			for (int resource : fileResources) {
//				final InputStream inputStream = appResources
//						.openRawResource(resource);
//				while (inputStream.read(buf) > 0) {
//					out.write(buf);
//				}
//				inputStream.close();
//			}
//			out.close();
//			// Error.log("Wrote " + myFile.getName());
//		} catch (FileNotFoundException e) {
//			// Error.log(e.toString());
//		} catch (IOException e) {
//			// Error.log(e.toString());
//		}
//	}
//
//	private void setExecutable(final File myFile) {
//		final String shell = hasRoot ? "su" : "sh";
//		try {
//			final Process process = Runtime.getRuntime().exec(shell);
//			final DataOutputStream outputStream = new DataOutputStream(
//					process.getOutputStream());
//			final BufferedReader inputStream = new BufferedReader(
//					new InputStreamReader(process.getInputStream()),
//					Install.BUFFER_SIZE);
//			final BufferedReader errorStream = new BufferedReader(
//					new InputStreamReader(process.getErrorStream()),
//					Install.BUFFER_SIZE);
//
//			outputStream.writeBytes("cd " + binaryDirectory + "\n");
//
//			if (hasRoot) {
//				outputStream.writeBytes("chown root.root * \n");
//				// Error.log("chown root.root *");
//			}
//
//			outputStream.writeBytes("chmod 555 " + myFile.getAbsolutePath()
//					+ " \n");
//			// Error.log("chmod 555 " + myFile.getAbsolutePath());
//
//			outputStream.writeBytes("chmod 777 " + binaryDirectory + " \n");
//			// Error.log("chmod 777 " + binaryDirectory + " \n");
//
//			outputStream.writeBytes("exit\n");
//
//			final StringBuilder feedback = new StringBuilder();
//			String input, error;
//			while ((input = inputStream.readLine()) != null) {
//				feedback.append(input);
//			}
//			while ((error = errorStream.readLine()) != null) {
//				feedback.append(error);
//			}
//
//			final String chmodResult = feedback.toString();
//			// Error.log(chmodResult);
//
//			outputStream.close();
//			inputStream.close();
//			errorStream.close();
//			process.waitFor();
//			process.destroy();
//
//			if (chmodResult.length() > 0) {
//			}
//		} catch (IOException e) {
//		} catch (InterruptedException e) {
//		}
//	}
//
//	@Override
//	public void run() {
//		File myFile;
//
//		for (InstallerBinary install : installerBinaries) {
//			final String filename = binaryDirectory + install.filename;
//
//			myFile = new File(filename);
//
//			deleteExistingFile(myFile);
//
//			writeNewFile(myFile, install.files);
//
//			if (install.executable) {
//				setExecutable(myFile);
//			}
//		}
//	}
// }
