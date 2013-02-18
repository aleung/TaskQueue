package leoliang.taskqueue;

import java.util.List;

import leoliang.taskqueue.DatePickerFragment.DatePickedEvent;
import leoliang.taskqueue.repository.CheckoutTaskList;
import leoliang.taskqueue.repository.Task;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.TextView;
import de.greenrobot.event.EventBus;


public class CheckoutTasksFragment extends TaskListFragment {

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		setEmptyText(getString(R.string.list_checkout_empty));

		setHasOptionsMenu(true);

		mAdapter = new CheckoutTaskListAdapter();
		setListAdapter(mAdapter);

		// Start out with a progress indicator.
		setListShown(false);

		getLoaderManager().initLoader(0, null, this);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		createOptionsMenu(menu, inflater);
	}

	@Override
	public Loader<List<Task>> onCreateLoader(int id, Bundle bundle) {
		return new CheckoutTaskList(getActivity());
	}

	private CheckoutTaskList getTaskList() {
		Loader<List<Task>> loader = getLoaderManager().getLoader(0);
		return (CheckoutTaskList) loader;
	}

	@Override
	protected void addTask(String title) {
		getTaskList().addTask(title);
	}

	@Override
	protected void scheduleTask(long id, long timeLocal) {
		getTaskList().scheduleTask(id, timeLocal);
	}

	class CheckoutTaskListAdapter extends TaskListAdapter {

		private class ViewHolder {
			TextView titleView;
			CheckBox doneBox;
			ImageButton uncheckoutButton;
			ImageButton scheduleButton;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			final ViewHolder holder;
			if (convertView == null) {
				view = LayoutInflater.from(getActivity()).inflate(R.layout.checkout_item, null);
				holder = new ViewHolder();
				holder.titleView = (TextView) view.findViewById(R.id.task_title);
				holder.doneBox = (CheckBox) view.findViewById(R.id.task_is_done);
				holder.uncheckoutButton = (ImageButton) view.findViewById(R.id.task_uncheckout);
				holder.scheduleButton = (ImageButton) view.findViewById(R.id.task_schedule);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			final Task task = (Task) getItem(position);

			holder.titleView.setText(task.getTitle());

			holder.doneBox.setChecked(false);
			holder.doneBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton button, boolean isChecked) {
					if (isChecked) {
						getTaskList().doneTask(task.getId());
					}
				}
			});

			holder.uncheckoutButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getTaskList().uncheckoutTask(task.getId());
				}
			});

			holder.scheduleButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DatePickerFragment datePicker = DatePickerFragment.newInstance(task.getId(), task.getPlanned());
					datePicker.show(CheckoutTasksFragment.this.getFragmentManager(), "");
					EventBus.getDefault().register(CheckoutTasksFragment.this, DatePickedEvent.class);
				}
			});

			return view;
		}

	}
}
