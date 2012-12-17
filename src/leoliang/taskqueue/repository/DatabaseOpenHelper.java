package leoliang.taskqueue.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

	public static final String TABLE_NAME = "task";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "taskqueue";

	DatabaseOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

    @Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " ("
				+ "_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "title TEXT NOT NULL,"
				+ "status TEXT NOT NULL,"
				+ "modified INTEGER NOT NULL,"
				+ "planned INTEGER DEFAULT 0,"
				+ "sort_order INTEGER NOT NULL)");

		ContentValues values = new ContentValues();
		values.put("status", Task.Status.CHECKOUT.name());
		values.put("title", "Welcome to TaskQueue!");
		values.put("modified", System.currentTimeMillis());
		values.put("sort_order", 0);
		db.insert(TABLE_NAME, null, values);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// no upgrade yet
	}

}
