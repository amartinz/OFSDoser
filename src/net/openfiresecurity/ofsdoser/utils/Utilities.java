/*
 * Old Class for my Installer, got useless for this Project, maybe usefull for you.
 */
//package net.openfiresecurity.ofsdoser.utils;
//
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.IOException;
//
//import android.content.Context;
//import android.content.pm.PackageManager.NameNotFoundException;
//
//public final class Utilities {
//
//	@SuppressWarnings("unused")
//	private static final Utilities SINGLETON = new Utilities();
//
//	private Utilities() {
//		// singleton - does nothing.
//	}
//
//	public static String getApplicationFolder(final Context context,
//			final String subfolder) {
//		File appDir = null;
//
//		try {
//			appDir = new File(Utilities.getDataDirectory(context) + "/"
//					+ subfolder);
//
//			if (appDir.exists()) {
//			} else {
//				if (appDir.mkdirs()) {
//				} else {
//					throw new Utilities.CannotCreateDirectoryException(
//							"Failed to create " + appDir.getAbsolutePath());
//				}
//			}
//		} catch (CannotCreateDirectoryException e) {
//			appDir = new File("/tmp/");
//		}
//
//		return appDir.getAbsolutePath() + "/";
//	}
//
//	private static String getDataDirectory(final Context context) {
//		String dataDirectory = "";
//		try {
//			dataDirectory = context.getPackageManager().getApplicationInfo(
//					"net.openfiresecurity.ofsdoser", 0).dataDir;
//		} catch (NameNotFoundException e) {
//		}
//		return dataDirectory;
//	}
//
//	// XXX Added
//	public static boolean canGetRoot() {
//		Process process = null;
//		DataOutputStream os = null;
//		boolean rooted = true;
//		try {
//			process = Runtime.getRuntime().exec("su");
//			os = new DataOutputStream(process.getOutputStream());
//			os.writeBytes("exit\n");
//			os.flush();
//			process.waitFor();
//			if (process.exitValue() != 0) {
//				rooted = false;
//			}
//		} catch (Exception e) {
//			rooted = false;
//		} finally {
//			if (os != null) {
//				try {
//					os.close();
//					process.destroy();
//				} catch (Exception e) {
//				}
//			}
//		}
//		return rooted;
//	}
//
//	public static final class NullProcessException extends Exception {
//
//		private static final long serialVersionUID = -6606740982523502676L;
//
//		public NullProcessException() {
//			super();
//		}
//	}
//
//	public static final class CannotCreateDirectoryException extends
//			IOException {
//
//		private static final long serialVersionUID = -7566027000405830050L;
//
//		public CannotCreateDirectoryException(final String detailMessage) {
//			super(detailMessage);
//		}
//	}
//
//	public static final class StandardErrorNotEmptyException extends Exception {
//
//		private static final long serialVersionUID = 866136689299038573L;
//
//		public StandardErrorNotEmptyException(final String detailMessage) {
//			super(detailMessage);
//		}
//	}
// }
