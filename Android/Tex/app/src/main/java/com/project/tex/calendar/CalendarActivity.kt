package com.project.tex.calendar

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.WindowCompat
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.DaySize
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.project.tex.R
import com.project.tex.base.activity.BaseActivity
import com.project.tex.databinding.ActivityCalendarBinding
import com.project.tex.databinding.NumberPickerDialogBinding
import com.project.tex.utils.RangeExts.toStringArray
import java.time.*
import java.time.format.TextStyle
import java.util.*
import kotlin.math.absoluteValue


class CalendarActivity : BaseActivity<ActivityCalendarBinding, CalendarViewModel>() {
    override fun getViewBinding() = ActivityCalendarBinding.inflate(layoutInflater)

    override fun getViewModelInstance() = ViewModelProvider(this).get(CalendarViewModel::class.java)
    private val c1 = Calendar.getInstance()

    private val today = YearMonth.now()
    private val MIN_YEAR = today.year - 105
    private val MAX_YEAR = today.year - 5
    private var currentMonth = today.minusYears(5)
        .withMonth(1)
    private val years = (MAX_YEAR..MIN_YEAR).toStringArray().toTypedArray()
    private var selectedDate: LocalDate = LocalDate.now()
        .minusYears(5)
        .withMonth(1).withDayOfMonth(1)

    private val maxDate: LocalDate = LocalDate.now()
        .minusYears(4)

    private val minDate: LocalDate = LocalDate.now()
        .minusYears(105)

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        _binding.close.setOnClickListener {
            onBackPressed()
        }

        _binding.currentMonth.setOnClickListener { view ->
            val list = resources.getStringArray(R.array.months)
            numberPickerCustom(getString(R.string.select_month), {
                currentMonth = YearMonth.of(
                    currentMonth.year, Month.valueOf(it.uppercase(Locale.getDefault()))
                )
                selectedDate = selectedDate.withDayOfMonth(1)
                updateMonth(currentMonth)
                _binding.calendarView.notifyDayChanged(CalendarDay(selectedDate, DayPosition.MonthDate))
                updateCardDate(LocalDate.of(currentMonth.year, currentMonth.month, 1))
            }, 11, 0, list, list.indexOf(_binding.currentMonth.text.toString()))
        }

        _binding.currentYear.setOnClickListener { view ->
            numberPickerCustom(getString(R.string.select_year), {
                currentMonth = YearMonth.of(it.toInt(), currentMonth.month)
                selectedDate = selectedDate.withDayOfMonth(1)
                updateMonth(currentMonth)
                _binding.calendarView.notifyDayChanged(CalendarDay(selectedDate, DayPosition.MonthDate))
                updateCardDate(LocalDate.of(currentMonth.year, currentMonth.month, 1))
            }, MIN_YEAR, MAX_YEAR, years, years.indexOf(currentMonth.year.toString()))
        }

//        _binding.calendarView.monthScrollListener = object : MonthScrollListener {
//            override fun invoke(p1: CalendarMonth) {
//                currentMonth = YearMonth.of(currentMonth.year, p1.yearMonth.month)
//                updateMonth(currentMonth)
//            }
//        }

        _binding.calendarView.daySize = DaySize.SeventhWidth
        _binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // Called only when a new container is needed.
            override fun create(view: View) = DayViewContainer(view)

            // Called every time we need to reuse a container.
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                // Set the calendar day for this container.
                container.day = data
                val isDateInMonth = data.position == DayPosition.MonthDate
                // Set the date text
                container.textView.text =
                    if (isDateInMonth) data.date.dayOfMonth.toString() else "-"

                if (isDateInMonth && data.date == selectedDate) {
                    // If this is the selected date, show a round background and change the text color.
                    container.textView.apply {
                        isChecked = true
                        typeface =
                            ResourcesCompat.getFont(this@CalendarActivity, R.font.poppins_bold)
                        setTextColor(
                            ContextCompat.getColor(this@CalendarActivity, R.color.colorPrimary)
                        )
                        alpha = 1f
                        isEnabled = true
                    }
                } else {
                    container.textView.apply {
                        isChecked = false
                        typeface =
                            ResourcesCompat.getFont(this@CalendarActivity, R.font.poppins_regular)
                        setTextColor(
                            ContextCompat.getColor(this@CalendarActivity, R.color.text_main)
                        )
                        if (isValidDate(data.date)) {
                            alpha = 1f
                            isEnabled = true
                        } else {
                            isEnabled = false
                            alpha = 0.3f
                        }
                    }
                }

                container.textView.setOnClickListener {
                    if (data.position != DayPosition.MonthDate) return@setOnClickListener

                    c1.set(Calendar.DAY_OF_MONTH, data.date.dayOfMonth)
                    c1.set(Calendar.YEAR, data.date.year)
                    c1.set(Calendar.MONTH, data.date.monthValue)

                    updateCardDate(data.date)

                    val currentSelection = selectedDate
                    if (currentSelection != data.date) {
                        selectedDate = data.date
                        // Reload the newly selected date so the dayBinder is
                        // called and we can ADD the selection background.
                        _binding.calendarView.notifyDateChanged(data.date)
                        if (currentSelection != null) {
                            // We need to also reload the previously selected
                            // date so we can REMOVE the selection background.
                            _binding.calendarView.notifyDateChanged(currentSelection)
                        }
                    }
                    _binding.calendarView.notifyCalendarChanged()
                }
            }

        }

        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
        val titlesContainer = _binding.titlesContainer.root
        titlesContainer.children.map { it as TextView }.forEachIndexed { index, textView ->
            val dayOfWeek = daysOfWeek[index]
            val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(2)
            textView.text = title
        }
        _binding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
                override fun create(view: View) = MonthViewContainer(view)
                override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                    // Remember that the header is reused so this will be called for each month.
                    // However, the first day of the week will not change so no need to bind
                    // the same view every time it is reused.
                    if (container.titlesContianer.tag == null) {
                        container.titlesContianer.tag = data.yearMonth
                        container.titlesContianer.children.map { it as TextView }
                            .forEachIndexed { index, textView ->
                                val dayOfWeek = daysOfWeek[index]
                                val title =
                                    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                textView.text = title
                                // In the code above, we use the same `daysOfWeek` list
                                // that was created when we set up the calendar.
                                // However, we can also get the `daysOfWeek` list from the month data:
                                // val daysOfWeek = data.weekDays.first().map { it.date.dayOfWeek }
                                // Alternatively, you can get the value for this specific index:
                                // val dayOfWeek = data.weekDays.first()[index].date.dayOfWeek
                            }
                    }
                }
            }

        _binding.btnNext.setOnClickListener { view ->
            c1.set(
                currentMonth.year, currentMonth.month.value - 1, c1[Calendar.DAY_OF_MONTH]
            )
            if (!isValidDate(LocalDateTime.ofInstant(c1.toInstant(), c1.getTimeZone().toZoneId()).toLocalDate())) {
                msg.showLongMsg("Please select a valid date!")
                return@setOnClickListener
            }
            setResult(RESULT_OK, Intent().apply {

                putExtra("date", c1.timeInMillis)
            })
            finish()
        }
        _binding.calendarView.setup(
            currentMonth.minusYears(106),
            currentMonth.plusYears(1),
            DayOfWeek.SUNDAY
        )

        _binding.calendarView.scrollToMonth(currentMonth)

        c1.set(Calendar.YEAR, MAX_YEAR)
        c1.set(Calendar.MONTH, currentMonth.monthValue)
        c1.set(Calendar.DAY_OF_MONTH, 1)

        _binding.tvMonth.text = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        _binding.tvDate.text = "${c1.get(Calendar.DAY_OF_MONTH)}"
        _binding.tvYear.text = "${MAX_YEAR}"

        _binding.currentMonth.text = _binding.tvMonth.text.toString()
        _binding.currentYear.text = _binding.tvYear.text.toString()
    }

    private fun isValidDate(date: LocalDate): Boolean {
        return date < maxDate && date > minDate
    }

    private fun updateCardDate(localDate: LocalDate) {
        _binding.tvDate.text = localDate.dayOfMonth.toString()
        _binding.tvMonth.text = _binding.currentMonth.text.toString()
        _binding.tvYear.text = _binding.currentYear.text.toString()
    }

    private fun updateMonth(currentMonth: YearMonth) {
        _binding.calendarView.scrollToMonth(currentMonth)
        val month = currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        val year = currentMonth.year.toString()

        _binding.currentMonth.text = month
        _binding.currentYear.text = year

        c1[Calendar.MONTH] = currentMonth.month.value
        c1[Calendar.YEAR] = currentMonth.year
        c1[Calendar.DAY_OF_MONTH] = 1
    }

    private fun numberPickerCustom(
        title: String,
        listener: NumberPickerSelected,
        max: Int,
        min: Int,
        list: Array<String>?,
        sel: Int
    ) {
        val d = AlertDialog.Builder(this)
        val dialogView = NumberPickerDialogBinding.inflate(layoutInflater)
        d.setView(dialogView.root)
        dialogView.title.text = title
        val alertDialog = d.create()

        val numberPicker = dialogView.dialogNumberPicker
        numberPicker.maxValue = (max - min).absoluteValue
        numberPicker.minValue = 0
        numberPicker.value = sel
        list?.let {
            // for month we have defined values in strings.xml file
            numberPicker.displayedValues = list
        }

        dialogView.btnDone.setOnClickListener {
            listener.numberSelected(numberPicker.displayedValues.get(numberPicker.value))
            alertDialog.dismiss()
        }
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
    }

    fun interface NumberPickerSelected {
        fun numberSelected(number: String)
    }
}