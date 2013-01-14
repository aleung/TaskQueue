package leoliang.taskqueue;

import java.util.TimeZone;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
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

		long currentTimeUtc = System.currentTimeMillis();
		long currentTimeLocal = currentTimeUtc - TimeZone.getDefault().getOffset(currentTimeUtc);
		calendarView.setMinDate(currentTimeLocal);
		// FIXME: limit to 40 days because CalendarView scrolling to last bug
		calendarView.setMaxDate(currentTimeLocal + 3600000L * 24 * 40);

		long timeLocal = getArguments().getLong(ARGUMENT_DATE);
		if (timeLocal == 0) {
			// by default set to tomorrow
			timeLocal = currentTimeLocal + 3600000L * 24;
		} else if (timeLocal < currentTimeLocal) {
			// not allow to set to past
			timeLocal = currentTimeLocal;
		}
		// TODO: after max date?
		calendarView.setDate(timeLocal);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.title_schedule);
		builder.setView(pickerView);
		builder.setPositiveButton(R.string.button_set, new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EventBus.getDefault().post(
						new DatePickedEvent(getArguments().getLong(ARGUMENT_CONTEXT_ID), calendarView.getDate()));
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
