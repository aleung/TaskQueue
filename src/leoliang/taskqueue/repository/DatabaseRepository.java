package leoliang.taskqueue.repository;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseRepository implements TaskRepository {

	private final SQLiteOpenHelper mOpenHelper;

	public DatabaseRepository(Context context) {
		mOpenHelper = new DatabaseOpenHelper(context);
	}

	@Override
	public void addTask(Task task) {
		ContentValues values = new ContentValues();
		values.put("title", task.getTitle());
		values.put("status", task.getStatus().name());
		values.put("planned", task.getPlanned());
		values.put("modified", System.currentTimeMillis());
		
		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		db.insertOrThrow("task", null, values);
	}

	@Override
	public void updateTask(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Task> queryHistoryTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Task> queryCheckoutTasks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Task> queryBacklogTasks() {
		// TODO Auto-generated method stub
		return null;
	}

}
