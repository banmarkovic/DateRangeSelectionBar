# DateRangeSelectionBar
Android library for easy selection of date range on selection bar. Date range could be selected by scrolling or clicking on the date range shown next to focused one.

![date_range_selection_bar_example_11](https://user-images.githubusercontent.com/17555202/83332287-9b286300-a29a-11ea-9aea-24a3355858ca.gif)

###XML attributes
  app:drsTextSize="16sp" <!-- set text size in selection bar -->
  app:drsTextColor="@android:color/white" <!-- set text color in selection bar -->
  app:drsUnderlineColor="@android:color/white" <!-- set color of selected date range undreline view inside selection bar -->
  app:drsVerticalPadding="14dp" <!-- set vertical padding in selection bar -->

For receiving the dates of selected date range in selection bar, you need to pass instance of  DateRangeSelectionBar.DRSOnTimePeriodSelectListener interface in DateRangeSelectionBar view.

##Example

    private val onTimePeriodSelectListener = object: DateRangeSelectionBar.DRSOnTimePeriodSelectListener {
        override fun onDatePeriodSelected(datePeriod: DRSDatePeriod) {
            Toast.makeText(this@MainActivity, "${datePeriod.startDate} - ${datePeriod.endDate}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dateRangeSelectionBar.setCurrentShownTimePeriodType(DRSDatePeriodType.DRSMonthlyType)
        dateRangeSelectionBar.onTimePeriodSelectListener = onTimePeriodSelectListener
        dateRangeSelectionBar.allowFutureDates = true
    }

###Configuration changes
Change the date period type shown in selection bar (default is DRSDatePeriodType.DRSMonthlyType).
Set DateRangeSelectionBar.DRSOnTimePeriodSelectListener listener.
Enable showing of future dates (default is true).

###Four types of date range could be shown in selection bar:
  Days -> DRSDatePeriodType.DRSDailyType,
  Weeks -> DRSDatePeriodType.DRSWeeklyType,
  Months -> DRSDatePeriodType.DRSMonthlyType,
  Years -> DRSDatePeriodType.DRSYearlyType.
