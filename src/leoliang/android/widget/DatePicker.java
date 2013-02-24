package leoliang.android.widget;

import java.util.Calendar;
import java.util.Locale;

import leoliang.taskqueue.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class DatePicker extends LinearLayout {

	protected Context context;

	private CalendarView calendarView;
	private Spinner yearSpinner;
	private Spinner monthSpinner;
	private YearSpinnerAdapter yearSpinnerAdapter;
	private MonthSpinnerAdapter monthSpinnerAdapter;

	private Calendar minDate;
	private Calendar maxDate;

	public DatePicker(Context context) {
		this(context, null);
	}

	public DatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	private void initialize(Context context) {
		this.context = context;

		// set default date range: today ~ 5 years since today
		minDate = Calendar.getInstance();
		maxDate = Calendar.getInstance();
		maxDate.add(Calendar.YEAR, 5);

		setOrientation(VERTICAL);

		View view = LayoutInflater.from(context).inflate(R.layout.widget_datepicker, this, true);
		calendarView = (CalendarView) view.findViewById(R.id.calendarView);
		yearSpinner = (Spinner) view.findViewById(R.id.yearSpinner);
		monthSpinner = (Spinner) view.findViewById(R.id.monthSpinner);

		OnItemSelectedListener spinnerListener = new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				updateCalendarView();
			}

			@Override
			public void onNothingSelected(AdapterView<?> paramAdapterView) {
				// ignore
			}
		};

		yearSpinnerAdapter = new YearSpinnerAdapter(context);
		yearSpinner.setAdapter(yearSpinnerAdapter);
		yearSpinner.setOnItemSelectedListener(spinnerListener);

		monthSpinnerAdapter = new MonthSpinnerAdapter(context);
		monthSpinner.setAdapter(monthSpinnerAdapter);
		monthSpinner.setOnItemSelectedListener(spinnerListener);

		updateDateRange();

		updateDateToToday();
	}

	private void updateCalendarView() {
		int year = yearSpinnerAdapter.getYearByPossition(yearSpinner.getSelectedItemPosition());
		int month = monthSpinner.getSelectedItemPosition();
		calendarView.showMonth(year, month);
	}

	private void updateDateToToday() {
		updateDate(Calendar.getInstance());
	}

	public void updateDate(int year, int month, int dayOfMonth) {
		Calendar date = Calendar.getInstance();
		date.set(year, month, dayOfMonth);
		updateDate(date);
	}

	public void updateDate(Calendar date) {
		Calendar selectedDate = date;
		if (date.before(minDate)) {
			selectedDate = minDate;
		}
		calendarView.selectDate(selectedDate);
		yearSpinner.setSelection(selectedDate.get(Calendar.YEAR) - minDate.get(Calendar.YEAR));
		monthSpinner.setSelection(selectedDate.get(Calendar.MONTH));
	}

	public Calendar getSelectedDate() {
		return calendarView.getSelectedDate();
	}

	public void setMinDate(Calendar minDate) {
		this.minDate = minDate;
		updateDateRange();
	}

	public void setMaxDate(Calendar maxDate) {
		this.maxDate = maxDate;
		updateDateRange();
	}

	private void updateDateRange() {
		yearSpinnerAdapter.setRange(minDate.get(Calendar.YEAR), maxDate.get(Calendar.YEAR));
		// TODO: how to control CalendarView?
	}

	private class YearSpinnerAdapter extends ArrayAdapter<String> {
		private int from;
		public YearSpinnerAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1);
		}

		public void setRange(int from, int to) {
			this.from = from;
			clear();
			for (int year = from; year <= to; year++) {
				add(String.valueOf(year));
			}
		}

		public int getYearByPossition(int pos) {
			return from + pos;
		}
	}

	private class MonthSpinnerAdapter extends ArrayAdapter<String> {

		public MonthSpinnerAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1);

			Calendar date = Calendar.getInstance();
			for (int month = 0; month < 12; month++) {
				date.set(Calendar.MONTH, month);
				add(date.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault()));
			}
		}
	}

}
