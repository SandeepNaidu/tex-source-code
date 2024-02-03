package com.project.tex.calendar

import android.view.View
import android.widget.CheckedTextView
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer
import com.project.tex.R

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<CheckedTextView>(R.id.calendarDayText)

    // With ViewBinding
    // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    lateinit var day: CalendarDay

    init {
        view.setOnClickListener {
            // Use the CalendarDay associated with this container.

        }
    }
}