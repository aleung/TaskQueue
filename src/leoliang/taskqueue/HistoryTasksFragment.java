package leoliang.taskqueue;

import java.util.List;

import leoliang.taskqueue.repository.HistoryTaskList;
import leoliang.taskqueue.repository.Task;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class HistoryTasksFragment extends TaskListFragment {

	private HistoryTaskList taskList;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getString(R.string.list_history_empty));

		setHasOptionsMenu(true);

		mAdapter = new HistoryTaskListAdapter();
		setListAdapter(mAdapter);

		// Start out with a progress indicator.
		setListShown(false);

		Loader<List<Task>> loader = getLoaderManager().initLoader(0, null, this);
		taskList = (HistoryTaskList) loader;

		taskList.purgeTasks(7);
	}

	@Override
	public Loader<List<Task>> onCreateLoader(int id, Bundle bundle) {
		return new HistoryTaskList(getActivity());
	}

	@Override
	protected void addTask(String title) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void scheduleTask(long id, long timeLocal) {
		throw new UnsupportedOperationException();
	}

	class HistoryTaskListAdapter extends TaskListAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// TODO: use convertView to avoid inflate every time
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.history_item, null);

			// TODO: use ViewHolder to avoid findViewById every time
			final TextView titleView = (TextView) view.findViewById(R.id.task_title);
			final ImageButton restoreButton = (ImageButton) view.findViewById(R.id.task_restore);
			final Task task = (Task) getItem(position);

			titleView.setText(task.getTitle());

			restoreButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					taskList.restoreTask(task.getId());
				}
			});

			return view;
		}

	}


}
