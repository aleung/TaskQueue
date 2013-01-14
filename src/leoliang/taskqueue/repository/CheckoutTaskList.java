package leoliang.taskqueue.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CheckoutTaskList extends TaskList {

	public CheckoutTaskList(Context context) {
		super(context);
	}

	public void addTask(String title) {
		addTask(title, Task.Status.CHECKOUT);
	}

	public void uncheckoutTask(long taskId) {
		updateTaskStatus(taskId, Task.Status.BACKLOG);
	}

	public void deleteTask(long taskId) {
		updateTaskStatus(taskId, Task.Status.DELETED);
	}

	public void doneTask(long taskId) {
		updateTaskStatus(taskId, Task.Status.DONE);
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
				new String[] { Task.Status.CHECKOUT.name() },
				null, null, "sort_order");
	}

}
