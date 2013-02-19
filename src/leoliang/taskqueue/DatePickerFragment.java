package leoliang.taskqueue;

import java.util.Calendar;

import leoliang.android.widget.CalendarView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import de.greenrobot.event.EventBus;

public class DatePickerFragment extends DialogFragment {

	public class DatePickedEvent {

		private long mContextId;
		private long mTime;

		public DatePickedEvent(long contextId, long time) {
			mContextId = contextId;
			mTime = time;
		}

		public long getContextId() {
			return mContextId;
		}

		/**
		 * FIXME: use enum
		 *
		 * @return -1: cancelled (no change), 0: cleared.
		 */
		public long getTime() {
			return mTime;
		}
	}

	private static final String ARGUMENT_DATE = "date";
	private static final String ARGUMENT_CONTEXT_ID = "contextId";

	public static DatePickerFragment newInstance(long contextId, long timeMillisLocal) {
		DatePickerFragment datePicker = new DatePickerFragment();
		Bundle args = new Bundle();
		args.putLong(ARGUMENT_CONTEXT_ID, contextId);
		args.putLong(ARGUMENT_DATE, timeMillisLocal);
		datePicker.setArguments(args);
		return datePicker;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View pickerView = inflater.inflate(R.layout.dialog_datepicker, null);
		final CalendarView calendarView = (CalendarView) pickerView.findViewById(R.id.calendarView);

		Calendar date = Calendar.getInstance(); // now
		long millis = getArguments().getLong(ARGUMENT_DATE);
		if (millis == 0) {
			// by default set to tomorrow
			date.add(Calendar.DAY_OF_YEAR, 1);
		} else {
			long currentTimeUtc = System.currentTimeMillis();
			if (millis < currentTimeUtc) {
				// not allow to set to past
				millis = currentTimeUtc;
			}
			date.setTimeInMillis(millis);
		}
		calendarView.selectDate(date);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.title_schedule);
		builder.setView(pickerView);
		builder.setPositiveButton(R.string.button_set, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Calendar date = calendarView.getSelectedDate();
				EventBus.getDefault().post(
						new DatePickedEvent(getArguments().getLong(ARGUMENT_CONTEXT_ID), date.getTimeInMillis()));
			}
		});
		builder.setNeutralButton(R.string.button_remove, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EventBus.getDefault().post(new DatePickedEvent(getArguments().getLong(ARGUMENT_CONTEXT_ID), 0));
			}
		});
		return builder.create();
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		EventBus.getDefault().post(new DatePickedEvent(getArguments().getLong(ARGUMENT_CONTEXT_ID), -1));
	}

}
