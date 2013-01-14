package leoliang.taskqueue;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import leoliang.taskqueue.DatePickerFragment.DatePickedEvent;
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
import de.greenrobot.event.EventBus;

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


	@Override
	protected void scheduleTask(long id, long timeLocal) {
		getTaskList().scheduleTask(id, timeLocal);
	}

	class BacklogTaskListAdapter extends TaskListAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// TODO: use convertView to avoid inflate every time
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.backlog_item, null);

			// TODO: use ViewHolder to avoid findViewById every time
			final TextView titleView = (TextView) view.findViewById(R.id.task_title);
			final EditText titleViewEdit = (EditText) view.findViewById(R.id.task_title_edit);
			TextView dateView = (TextView) view.findViewById(R.id.task_date);
			ImageButton checkoutButton = (ImageButton) view.findViewById(R.id.task_checkout);
			ImageButton downButton = (ImageButton) view.findViewById(R.id.task_down_priority);
			ImageButton scheduleButton = (ImageButton) view.findViewById(R.id.task_schedule);

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

			if (task.getPlanned() > 0) {
				dateView.setText(formatDate(task.getPlanned()));
				dateView.setVisibility(View.VISIBLE);
			} else {
				dateView.setVisibility(View.GONE);
			}

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

			scheduleButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DatePickerFragment datePicker = DatePickerFragment.newInstance(task.getId(), task.getPlanned());
					datePicker.show(BacklogTasksFragment.this.getFragmentManager(), "");
					EventBus.getDefault().register(BacklogTasksFragment.this, DatePickedEvent.class);
				}
			});

			return view;
		}

		private String formatDate(long timeMs) {
			DateFormat format = DateFormat.getDateInstance();
			return format.format(new Date(timeMs));
		}

	}

}
