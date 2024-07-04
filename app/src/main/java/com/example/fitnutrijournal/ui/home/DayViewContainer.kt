package com.example.fitnutrijournal.ui.home

import android.view.View
import android.widget.TextView
import com.example.fitnutrijournal.R
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.view.ViewContainer

class DayViewContainer(view: View) : ViewContainer(view) {
    // MonthDayBinder에서 해당 변수에 data(CalendarDay)를 넣어줄 예정
    lateinit var day: CalendarDay
    val textView = view.findViewById<TextView>(R.id.calendarDayText)

    // With ViewBinding
    // val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
    init {
        view.setOnClickListener {
            // 날짜를 클릭했을 때 이벤트
            Log.d("CollapisbleActivity", day.toString())
        }
    }



}
class DayViewContiner(view: View) : ViewContainer(view){

    val textView = view.findViewById<TextView>(R.id.dayTextView)


}