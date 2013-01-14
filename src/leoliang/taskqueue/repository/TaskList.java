package leoliang.taskqueue.repository;

import java.util.ArrayList;
import java.util.List;

import leoliang.taskqueue.repository.Task.Status;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.AsyncTaskLoader;

public abstract class TaskList extends AsyncTaskLoader<List<Task>> {

	protected List<Task> tasks;

	public class TaskNotFoundException extends Exception {
	}

	public TaskList(Context context) {
		super(context);
	}

	protected abstract Cursor queryDatabase(SQLiteDatabase database);

	@Override
	public void onStartLoading() {
		// TODO: cache to avoid reload
		forceLoad();
	}

	@Override
	public List<Task> loadInBackground() {
		SQLiteDatabase database = new DatabaseOpenHelper(getContext()).getReadableDatabase();
		Cursor cursor = queryDatabase(database);
		tasks = new ArrayList<Task>();
		if (cursor.moveToFirst()) {
			while (true) {
				Task task = new Task();
				task.setId(cursor.getLong(cursor.getColumnIndexOrThrow("_ID")));
				task.setModified(cursor.getLong(cursor.getColumnIndexOrThrow("modified")));
				task.setOrder(cursor.getInt(cursor.getColumnIndexOrThrow("sort_order")));
				task.setPlanned(cursor.getLong(cursor.getColumnIndexOrThrow("planned")));
				task.setStatus(Task.Status.valueOf(cursor.getString(cursor.getColumnIndexOrThrow("status"))));
				task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow("title")));
				tasks.add(task);
				if (cursor.moveToNext() == false) {
					break;
				}
			}
		}
		cursor.close();
		database.close();
		return tasks;
	}

	protected void updateTaskStatus(long taskId, Task.Status status) {
		SQLiteDatabase database = new DatabaseOpenHelper(getContext()).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("status", status.name());
		values.put("modified", System.currentTimeMillis());
		database.update(DatabaseOpenHelper.TABLE_NAME, values, "_id=?", new String[] { String.valueOf(taskId) });
		database.close();

		// reload from database (TODO: optimize, update list without query database)
		forceLoad();
	}


	protected void addTask(String title, Status status) {
		addTask(title, status, true);
	}

	protected void addTask(String title, Status status, boolean putAtFirst) {
		int order;
		if (tasks.isEmpty()) {
			order = 0;
		} else if (putAtFirst) {
			order = tasks.get(0).getOrder() - 100;
		} else {
			order = tasks.get(tasks.size() - 1).getOrder() + 100;
		}
		SQLiteDatabase database = new DatabaseOpenHelper(getContext()).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("status", status.name());
		values.put("title", title);
		values.put("modified", System.currentTimeMillis());
		values.put("sort_order", order);
		database.insert(DatabaseOpenHelper.TABLE_NAME, null, values);
		database.close();

		// reload from database (TODO: optimize, update list without query database)
		forceLoad();
	}

	protected void updateTitle(long taskId, String title) {
		SQLiteDatabase database = new DatabaseOpenHelper(getContext()).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("title", title);
		values.put("modified", System.currentTimeMillis());
		database.update(DatabaseOpenHelper.TABLE_NAME, values, "_id=?", new String[] { String.valueOf(taskId) });
		database.close();

		// reload from database (TODO: optimize, update list without query database)
		forceLoad();
	}

	protected void scheduleTask(long taskId, long plannedDate) {
		SQLiteDatabase database = new DatabaseOpenHelper(getContext()).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("planned", plannedDate);
		values.put("status", Task.Status.BACKLOG.name());
		values.put("modified", System.currentTimeMillis());
		database.update(DatabaseOpenHelper.TABLE_NAME, values, "_id=?", new String[] { String.valueOf(taskId) });
		database.close();

		// reload from database (TODO: optimize, update list without query database)
		forceLoad();
	}

	protected void moveTaskPosition(long taskId, int positionOffset) throws TaskNotFoundException {
		int order;
		int originalPosition = getTaskPosition(taskId);
		int newPosition = originalPosition + positionOffset;
		if (newPosition <= 0) {
			order = tasks.get(0).getOrder() - 100;
		} else if (newPosition >= tasks.size() - 1) {
			order = tasks.get(tasks.size() - 1).getOrder() + 100;
		} else {
			order = (tasks.get(newPosition - 1).getOrder() + tasks.get(newPosition).getOrder()) / 2;
		}

		SQLiteDatabase database = new DatabaseOpenHelper(getContext()).getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("sort_order", order);
		values.put("modified", System.currentTimeMillis());
		database.update(DatabaseOpenHelper.TABLE_NAME, values, "_id=?", new String[] { String.valueOf(taskId) });
		database.close();

		// reload from database (TODO: optimize, update list without query database)
		forceLoad();
	}

	private int getTaskPosition(long taskId) throws TaskNotFoundException {
		for (int position = 0; position < tasks.size(); position++) {
			if (tasks.get(position).getId() == taskId) {
				return position;
			}
		}
		throw new TaskNotFoundException();
	}
}
