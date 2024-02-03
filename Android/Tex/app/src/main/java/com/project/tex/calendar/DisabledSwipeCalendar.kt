package com.project.tex.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import com.kizitonwose.calendar.view.CalendarView

class DisabledSwipeCalendar @JvmOverloads constructor(context: Context, attrs: AttributeSet) :
    CalendarView(context, attrs) {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        return if (ev.action == MotionEvent.ACTION_MOVE) true else super.dispatchTouchEvent(ev)
    }

}