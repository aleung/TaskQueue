package leoliang.taskqueue.repository;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HistoryTaskList extends TaskList {

	public HistoryTaskList(Context context) {
		super(context);
	}

	public void restoreTask(long taskId) {
		updateTaskStatus(taskId, Task.Status.CHECKOUT);
	}

	@Override
	protected Cursor queryDatabase(SQLiteDatabase database) {
		return database.query(DatabaseOpenHelper.TABLE_NAME, null, "status=? OR status=?", 
				new String[] {Task.Status.DELETED.name(), Task.Status.DONE.name() }, null, null, "modified DESC");
	}

	public void purgeTasks(int days) {
		SQLiteDatabase database = new DatabaseOpenHelper(getContext()).getWritableDatabase();
		database.delete(
				DatabaseOpenHelper.TABLE_NAME,
				"(status=? OR status=?) AND modified<?",
				new String[] { Task.Status.DELETED.name(), Task.Status.DONE.name(),
						String.valueOf(System.currentTimeMillis() - 1000 * 3600 * 24 * days) });
	}
}
