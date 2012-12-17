package leoliang.taskqueue;

import java.util.List;

import leoliang.taskqueue.repository.Task;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
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
		EditText titleEdit = (EditText) view.findViewById(R.id.task_title_edit);
		titleEdit.requestFocus();
//		final InputMethodManager imm = (InputMethodManager) getActivity()
//				.getSystemService(Context.INPUT_METHOD_SERVICE);
//		imm.showSoftInput(titleEdit, InputMethodManager.SHOW_IMPLICIT);
		titleEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					addTask(textView.getText().toString());
					menuItem.collapseActionView();
					textView.clearFocus();
					textView.setText("");
					return true;
				}
				return false;
			}
		});
		titleEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean hasFocus) {
				if (!hasFocus) {
					getActivity().getWindow().setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
				}
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
