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
import android.widget.ImageView;
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

		private class ViewHolder {
			ImageView statusView;
			TextView titleView;
			TextView ageView;
			ImageButton restoreButton;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.history_item, null);
				holder = new ViewHolder();
				holder.statusView = (ImageView) view.findViewById(R.id.task_status);
				holder.titleView = (TextView) view.findViewById(R.id.task_title);
				holder.ageView = (TextView) view.findViewById(R.id.task_age);
				holder.restoreButton = (ImageButton) view.findViewById(R.id.task_restore);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			final Task task = (Task) getItem(position);

			if (task.getStatus() == Task.Status.DONE) {
				holder.statusView.setImageResource(R.drawable.round_checkmark_icon);
			} else if (task.getStatus() == Task.Status.DELETED) {
				holder.statusView.setImageResource(R.drawable.round_delete_icon);
			} else {
				throw new AssertionError("Illegal task status in history task list. Status: "
						+ task.getStatus().toString());
			}
			holder.titleView.setText(task.getTitle());
			// FIXME: calculate age by date; i18n
			holder.ageView.setText((System.currentTimeMillis() - task.getModified()) / 3600000 / 24 + " days ago");

			holder.restoreButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					taskList.restoreTask(task.getId());
				}
			});

			return view;
		}

	}


}
