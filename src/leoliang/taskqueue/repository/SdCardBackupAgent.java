package leoliang.taskqueue.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class SdCardBackupAgent {

	public class Error extends Exception {
		public Error(String detailMessage) {
			super(detailMessage);
		}
	}

	private final File dbFile;
	private final File backupDir;
	private final File backupFile;
	private static final String BACKUP_DB_PATH = "//TaskQueue//";
	private static final String BACKUP_DB_FILE = "backup." + DatabaseOpenHelper.DATABASE_VERSION
			+ ".db";

	public SdCardBackupAgent(Context context) {
		dbFile = context.getApplicationContext().getDatabasePath(DatabaseOpenHelper.DATABASE_NAME);
		backupDir = new File(Environment.getExternalStorageDirectory(), BACKUP_DB_PATH);
		backupFile = new File(backupDir, BACKUP_DB_FILE);
	}

	private void copyFile(File from, File to) throws Error {
		if (from.exists()) {
			try {
				FileChannel src = new FileInputStream(from).getChannel();
				FileChannel dst = new FileOutputStream(to).getChannel();
				dst.transferFrom(src, 0, src.size());
				src.close();
				dst.close();
			} catch (IOException e) {
				Log.e("SdCardBackupAgent", "Error occuried when copying database file.", e);
				throw new Error("Error occuried when copying database file.");
			}
		} else {
			throw new Error("Source file doesn't exist.");
		}
	}

	public void restore() throws Error {
		copyFile(backupFile, dbFile);
	}

	public void backup() throws Error {
		File sd = Environment.getExternalStorageDirectory();
		if (sd.canWrite()) {
			backupDir.mkdirs();
			copyFile(dbFile, backupFile);
		} else {
			throw new Error("SD card isn't writable.");
		}
	}
}
