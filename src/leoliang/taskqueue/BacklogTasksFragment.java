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

		private class ViewHolder {
			TextView titleView;
			EditText titleViewEdit;
			TextView dateView;
			ImageButton checkoutButton;
			ImageButton downButton;
			ImageButton scheduleButton;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.backlog_item, null);
				holder = new ViewHolder();
				holder.titleView = (TextView) view.findViewById(R.id.task_title);
				holder.titleViewEdit = (EditText) view.findViewById(R.id.task_title_edit);
				holder.dateView = (TextView) view.findViewById(R.id.task_date);
				holder.checkoutButton = (ImageButton) view.findViewById(R.id.task_checkout);
				holder.downButton = (ImageButton) view.findViewById(R.id.task_down_priority);
				holder.scheduleButton = (ImageButton) view.findViewById(R.id.task_schedule);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			final Task task = (Task) getItem(position);

			holder.titleView.setText(task.getTitle());
			holder.titleView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					holder.titleView.setVisibility(View.GONE);
					holder.titleViewEdit.setVisibility(View.VISIBLE);
					holder.titleViewEdit.requestFocus();
				}
			});

			holder.titleViewEdit.setText(task.getTitle());
			holder.titleViewEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						holder.titleViewEdit.setVisibility(View.GONE);
						holder.titleView.setVisibility(View.VISIBLE);
						if (!holder.titleViewEdit.getText().toString().equals(task.getTitle())) {
							getTaskList().updateTitle(task.getId(), holder.titleViewEdit.getText().toString());
						}
					}
				}
			});

			if (task.getPlanned() > 0) {
				holder.dateView.setText(formatDate(task.getPlanned()));
				holder.dateView.setVisibility(View.VISIBLE);
			} else {
				holder.dateView.setVisibility(View.GONE);
			}

			holder.checkoutButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getTaskList().checkoutTask(task.getId());
				}
			});

			holder.downButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getTaskList().downPriority(task.getId());
				}
			});

			holder.scheduleButton.setOnClickListener(new View.OnClickListener() {
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
