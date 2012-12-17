package leoliang.taskqueue;

import java.util.List;

import leoliang.taskqueue.repository.CheckoutTaskList;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;


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



	class CheckoutTaskListAdapter extends TaskListAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			// TODO: use convertView to avoid inflate every time
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.checkout_item, null);

			// TODO: use ViewHolder to avoid findViewById every time
			final TextView titleView = (TextView) view.findViewById(R.id.task_title);
			final EditText titleViewEdit = (EditText) view.findViewById(R.id.task_title_edit);
			final CheckBox doneBox = (CheckBox) view.findViewById(R.id.task_is_done);
			final ImageButton uncheckoutButton = (ImageButton) view.findViewById(R.id.task_uncheckout);
			final ImageButton planButton = (ImageButton) view.findViewById(R.id.task_plan);

			final Task task = (Task) getItem(position);

			titleView.setText(task.getTitle());
			titleView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					titleView.setVisibility(View.GONE);
					doneBox.setVisibility(View.GONE);
					uncheckoutButton.setVisibility(View.GONE);
					planButton.setVisibility(View.GONE);
					titleViewEdit.setVisibility(View.VISIBLE);
					titleViewEdit.requestFocus();
				}
			});

			titleViewEdit.setText(task.getTitle());
			titleViewEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						String newTitle = titleViewEdit.getText().toString();
						titleView.setText(newTitle);
						titleViewEdit.setVisibility(View.GONE);
						titleView.setVisibility(View.VISIBLE);
						doneBox.setVisibility(View.VISIBLE);
						uncheckoutButton.setVisibility(View.VISIBLE);
						planButton.setVisibility(View.VISIBLE);
						if (!newTitle.equals(task.getTitle())) {
							getTaskList().updateTitle(task.getId(), newTitle);
						}
					}
				}
			});


			doneBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton button, boolean isChecked) {
					if (isChecked) {
						getTaskList().doneTask(task.getId());
					}
				}
			});

			uncheckoutButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					getTaskList().uncheckoutTask(task.getId());
				}
			});

			return view;
		}

	}
}
