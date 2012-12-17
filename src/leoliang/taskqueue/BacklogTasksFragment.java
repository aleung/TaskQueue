package leoliang.taskqueue;

import java.util.List;

import leoliang.taskqueue.repository.BacklogTaskList;
import leoliang.taskqueue.repository.Task;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class BacklogTasksFragment extends TaskListFragment {
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getString(R.string.list_backlog_empty));

		setHasOptionsMenu(true);

		mAdapter = new BacklogTaskListAdapter();
		setListAdapter(mAdapter);

		// Start out with a progress indicator.
		setListShown(false);

		getLoaderManager().initLoader(0, null, this);

	}

	@Override
	public Loader<List<Task>> onCreateLoader(int id, Bundle bundle) {
		return new BacklogTaskList(getActivity());
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		createOptionsMenu(menu, inflater);
	}

	private BacklogTaskList getTaskList() {
		Loader<List<Task>> loader = getLoaderManager().getLoader(0);
		return (BacklogTaskList) loader;
	}

	@Override
	protected void addTask(String title) {
		getTaskList().addTask(title);
	}


	class BacklogTaskListAdapter extends TaskListAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// TODO: use convertView to avoid inflate every time
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.backlog_item, null);

			// TODO: use ViewHolder to avoid findViewById every time
			final TextView titleView = (TextView) view.findViewById(R.id.task_title);
			final EditText titleViewEdit = (EditText) view.findViewById(R.id.task_title_edit);
			ImageButton checkoutButton = (ImageButton) view.findViewById(R.id.task_checkout);
			ImageButton downButton = (ImageButton) view.findViewById(R.id.task_down_priority);

			final Task task = (Task) getItem(position);

			titleView.setText(task.getTitle());
			titleView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					titleView.setVisibility(View.GONE);
					titleViewEdit.setVisibility(View.VISIBLE);
					titleViewEdit.requestFocus();
				}
			});

			titleViewEdit.setText(task.getTitle());
			titleViewEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						titleViewEdit.setVisibility(View.GONE);
						titleView.setVisibility(View.VISIBLE);
						if (!titleViewEdit.getText().toString().equals(task.getTitle())) {
							getTaskList().updateTitle(task.getId(), titleViewEdit.getText().toString());
						}
					}
				}
			});


			checkoutButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getTaskList().checkoutTask(task.getId());
				}
			});

			downButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getTaskList().downPriority(task.getId());
				}
			});

			return view;
		}

	}

}
