package leoliang.android.widget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.util.MonthDisplayHelper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * A calendar widget, similar to android.widget.CalendarView.
 * <p>
 * Origin from {@link https://github.com/meinside/andlib/blob/6879bf1ad56c478401a27444f3fbedd25e7c97ca
 * /src/org/andlib/ui/SimpleCalendarView.java}
 *
 */
public class CalendarView extends TableLayout {

	/**
	 * The callback used to indicate the user changes the date.
	 */
	public interface OnChangeListener {

		/**
		 * Called upon change of the selected day by user click.
		 *
		 * @param view The view associated with this listener.
		 * @param year The year that was set.
		 * @param month The month that was set [0-11].
		 * @param dayOfMonth The day of the month that was set.
		 */
		public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth);
	}

	public static final int NUM_ROWS = 6;
	public static final int NUM_COLUMNS = 7;

	private static final int WEEK_START_DAY = Calendar.SUNDAY;

	protected Context context;

	private ArrayList<View> cells = null;
	private OnChangeListener listener = null;
	private MonthDisplayHelper helper = null;
	private Calendar showingDate = null;
	private Calendar selectedDate = null;


	public CalendarView(Context context) {
		this(context, null);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initialize(context);
	}

	private void initialize(Context context) {
		if (isInEditMode()) {
			return;
		}

		this.context = context;
		showingDate = Calendar.getInstance();

		helper = new MonthDisplayHelper(showingDate.get(Calendar.YEAR), showingDate.get(Calendar.MONTH), WEEK_START_DAY);

		setBackgroundColor(0xFFDFDFDF);
		setPadding(0, 1, 1, 0);

		TableRow.LayoutParams rowParams = new TableRow.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		rowParams.weight = 1f;
		rowParams.gravity = Gravity.CENTER_VERTICAL;
		rowParams.setMargins(0, 0, 0, 0);

		// calendar header

		TableRow headerRow = new TableRow(context);
		for (int j = 0; j < NUM_COLUMNS; j++) {
			View cell = createAndRenderCalendarHeaderCell(j);
			headerRow.addView(cell);
		}
		addView(headerRow, rowParams);

		// calendar cells

		cells = new ArrayList<View>();

		for (int i = 0; i < NUM_ROWS; i++) {
			TableRow row = new TableRow(context);

			for (int j = 0; j < NUM_COLUMNS; j++) {
				View cell = createCalendarCell();
				cell.setTag(i * NUM_COLUMNS + j); // save row+column index as tag

				cell.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View cell) {
						int row = (Integer) cell.getTag() / NUM_COLUMNS;
						int column = (Integer) cell.getTag() % NUM_COLUMNS;

						Calendar date = getCellDate(row, column);
						selectedDate = date;
						refresh();
						if (listener != null) {
							listener.onSelectedDayChange(CalendarView.this, date.get(Calendar.YEAR),
									date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
						}
					}
				});
				row.addView(cell);
				cells.add(cell);
			}
			addView(row, rowParams);
		}

		refresh();
	}

	/**
	 * override this function to change design of view
	 *
	 * @param passOrFuture - 0: today, >0: in the future, <0: in the pass
	 */
	protected void renderCell(View cell, Calendar date, boolean isWithinShowingMonth, boolean isSelected,
			int passOrFuture) {
		int day = date.get(Calendar.DAY_OF_MONTH);
		TextView view = (TextView) cell;
		view.setText(String.format("%2d", day));

		int selectedDateColor           = 0xFFFFFFFF;
		int selectedDateBackgroundColor = 0xFF1DB6FF;
		int todayColor                  = 0xFF000000;
		int todayBackgroundColor        = 0xFFF2EDDB;
		int otherMonthColor             = 0xFFB1B1B1;
		int otherMonthBackgroundColor   = 0xFFEBEBEB;
		int passedDateColor             = 0xFFB1B1B1;
		int passedDateBackgroundColor   = 0xFFFFFFFF;
		int showingMonthColor           = 0xFF404040;
		int showingMonthBackgroundColor = 0xFFFFFFFF;

		// Order: selected date -> today -> other month -> passed date -> showing month

		if (isSelected) {
			view.setTextColor(selectedDateColor);
			view.setBackgroundColor(selectedDateBackgroundColor);
		} else if (passOrFuture == 0) {
			view.setTextColor(todayColor);
			view.setBackgroundColor(todayBackgroundColor);
		} else if (!isWithinShowingMonth) {
			view.setTextColor(otherMonthColor);
			view.setBackgroundColor(otherMonthBackgroundColor);
		} else if (passOrFuture < 0) {
			view.setTextColor(passedDateColor);
			view.setBackgroundColor(passedDateBackgroundColor);
		} else {
			view.setTextColor(showingMonthColor);
			view.setBackgroundColor(showingMonthBackgroundColor);
		}
	}

	/**
	 * override this function to change design of view
	 */
	protected View createCalendarCell() {
		TextView view = new TextView(context);
		view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		view.setGravity(Gravity.CENTER);
		view.setPadding(0, 16, 0, 16);

		TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		cellParams.weight = 1f;
		cellParams.gravity = Gravity.CENTER;
		cellParams.setMargins(1, 0, 0, 1);
		view.setLayoutParams(cellParams);

		return view;
	}

	/**
	 * override this function to change design of view
	 */
	protected View createAndRenderCalendarHeaderCell(int weekDay) {
		TextView view = new TextView(context);
		view.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
		view.setGravity(Gravity.CENTER);
		view.setPadding(0, 4, 0, 4);

		Calendar date = Calendar.getInstance();
		date.set(helper.getYear(), helper.getMonth(), helper.getDayAt(1, weekDay));
		view.setText(date.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault()));

		int dayOfWeek = date.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
			view.setTextColor(0xFFB1B1B1);
		} else {
			view.setTextColor(0xFF404040);
		}
		view.setBackgroundColor(0xFFFFFFFF);

		TableRow.LayoutParams cellParams = new TableRow.LayoutParams(0, LayoutParams.WRAP_CONTENT);
		cellParams.weight = 1f;
		cellParams.gravity = Gravity.CENTER;
		cellParams.setMargins(1, 0, 0, 3);
		view.setLayoutParams(cellParams);

		return view;
	}

	private Calendar getCellDate(int row, int column) {
		int dayOfMonth = helper.getDayAt(row, column);
		int month = helper.getMonth();
		if (!helper.isWithinCurrentMonth(row, column)) {
			month = (dayOfMonth < 15 ? month + 1 : month - 1);
		}

		Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, helper.getYear());
		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		return date;
	}

	/**
	 * refresh view
	 */
	public void refresh() {
		// showingDate might has been changed, create new helper
		helper = new MonthDisplayHelper(showingDate.get(Calendar.YEAR), showingDate.get(Calendar.MONTH), WEEK_START_DAY);

		Calendar today = Calendar.getInstance();
		for (View cell : cells) {
			int row = (Integer) cell.getTag() / NUM_COLUMNS;
			int column = (Integer) cell.getTag() % NUM_COLUMNS;

			Calendar date = getCellDate(row, column);

			boolean isWithinShowingMonth = helper.isWithinCurrentMonth(row, column);
			boolean isSelectedDate = (selectedDate == null) ? false : (compareCalendarByDay(date, selectedDate) == 0);
			int passOrFuture = compareCalendarByDay(date, today);

			renderCell(cell, date, isWithinShowingMonth, isSelectedDate, passOrFuture);
		}
	}

	public int getShowingYear() {
		return showingDate.get(Calendar.YEAR);
	}

	/**
	 * @return month [0-11]
	 */
	public int getShowingMonth() {
		return showingDate.get(Calendar.MONTH);
	}

	/**
	 * Example:
	 *
	 * <pre>
	 * showMonth(2013, 11) // Dec. 2013
	 * </pre>
	 */
	public void showMonth(int year, int month) {
		showingDate.set(year, month, 1);
		refresh();
	}

	public void showCurrentMonth() {
		showingDate = Calendar.getInstance();
		refresh();
	}

	public void showPreviousYear() {
		showingDate.add(Calendar.YEAR, -1);
		refresh();
	}

	public void showNextYear() {
		showingDate.add(Calendar.YEAR, 1);
		refresh();
	}

	public void showPreviousMonth() {
		showingDate.add(Calendar.MONTH, -1);
		refresh();
	}

	public void showNextMonth() {
		showingDate.add(Calendar.MONTH, 1);
		refresh();
	}

	/**
	 * By default the current date is selected.
	 */
	public Calendar getSelectedDate() {
		return selectedDate;
	}

	public void selectDate(Calendar date) {
		selectedDate = date;
		showMonth(date.get(Calendar.YEAR), date.get(Calendar.MONTH));
	}

	public void setOnChangeListener(OnChangeListener listener) {
		this.listener = listener;
	}

	/**
	 * Compare two calendar by date, ignore hour/minute/second, ignore time zone.
	 *
	 * <pre>
	 * c1 = 2013-2-20, c2 = 2013-2-19, return positive;
	 * c1 = 2013-2-20, c2 = 2013-2-20, return zero;
	 * </pre>
	 */
	private static int compareCalendarByDay(Calendar c1, Calendar c2) {
		int compareYear = c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
		if (compareYear != 0) {
			return compareYear;
		}
		int compareMonth = c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
		if (compareMonth != 0) {
			return compareMonth;
		}
		int compareDay = c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
		return compareDay;
	}

}