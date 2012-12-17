package leoliang.taskqueue;

import java.util.ArrayList;
import java.util.List;

import leoliang.taskqueue.repository.Task;
import android.widget.BaseAdapter;

public abstract class TaskListAdapter extends BaseAdapter {

	private List<Task> tasks;

	@Override
	public int getCount() {
		return (tasks == null) ? 0 : tasks.size();
	}

	@Override
	public Object getItem(int location) {
		return tasks.get(location);
	}

	@Override
	public long getItemId(int location) {
		return tasks.get(location).getId();
	}

	public void changeList(List<Task> list) {
		tasks = (list == null) ? new ArrayList<Task>() : list;
		notifyDataSetChanged();
	}

}
