package leoliang.taskqueue;

import java.util.List;

import leoliang.taskqueue.repository.Task;
import android.content.Context;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public abstract class TaskListFragment extends ListFragment implements LoaderCallbacks<List<Task>> {
	protected TaskListAdapter mAdapter;

	@Override
	public void onLoadFinished(Loader<List<Task>> loader, List<Task> data) {
		mAdapter.changeList(data);

		// The list should now be shown.
		if (isResumed()) {
			setListShown(true);
		} else {
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<List<Task>> loader) {
		mAdapter.changeList(null);
	}

	protected void createOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fragment_tasklist, menu);
		final MenuItem menuItem = menu.findItem(R.id.menu_add);
		View view = menuItem.getActionView();
		final EditText titleEdit = (EditText) view.findViewById(R.id.task_title_edit);

		titleEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					addTask(textView.getText().toString());
					menuItem.collapseActionView();
					textView.setText("");
					return true;
				}
				return false;
			}
		});

		titleEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				// Hide soft keyboard whenever focus moves out
				if (!hasFocus) {
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
							Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(titleEdit.getWindowToken(), 0);
				}
			}
		});

		menuItem.setOnActionExpandListener(new OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionCollapse(MenuItem item) {
				return true;
			}

			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				// Focus on edit area and pop up soft keyboard
				// Thanks to the answer on StackOverflow. http://stackoverflow.com/a/8532417/94148
				titleEdit.requestFocus();
				titleEdit.postDelayed(new Runnable() {
					@Override
					public void run() {
						InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
								Context.INPUT_METHOD_SERVICE);
						imm.showSoftInput(titleEdit, InputMethodManager.SHOW_IMPLICIT);
					}
				}, 200);
				return true;
			}
		});
	}

	abstract protected void addTask(String title);

	/**
	 * Temp solution. Should reload once data is changed, not wait till the fragment is shown.
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser == true) {
			getLoaderManager().restartLoader(0, null, this);
		}
	}
}
