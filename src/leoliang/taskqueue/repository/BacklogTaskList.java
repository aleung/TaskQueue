package leoliang.taskqueue.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BacklogTaskList extends TaskList {

	public BacklogTaskList(Context context) {
		super(context);
	}

	public void addTask(String title) {
		addTask(title, Task.Status.BACKLOG);
	}

	public void checkoutTask(long taskId) {
		updateTaskStatus(taskId, Task.Status.CHECKOUT);
	}

	public void deleteTask(long taskId) {
		updateTaskStatus(taskId, Task.Status.DELETED);
	}

	public void doneTask(long taskId) {
		updateTaskStatus(taskId, Task.Status.DONE);
	}

	public void downPriority(long taskId) {
		try {
			moveTaskPosition(taskId, 10);
		} catch (TaskNotFoundException e) {
			// should not happen
		}
	}

	@Override
	public void updateTitle(long taskId, String title) {
		super.updateTitle(taskId, title);
	}

	@Override
	public void scheduleTask(long taskId, long plannedDate) {
		super.scheduleTask(taskId, plannedDate);
	}

	@Override
	protected Cursor queryDatabase(SQLiteDatabase database) {
		return database.query(DatabaseOpenHelper.TABLE_NAME, null, "status=?",
				new String[] { Task.Status.BACKLOG.name() }, null, null, "sort_order");
	}
}
