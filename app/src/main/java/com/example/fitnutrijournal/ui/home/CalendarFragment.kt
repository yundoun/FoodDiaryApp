package com.example.fitnutrijournal.ui.home

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.databinding.CalendarDayLayoutBinding
import com.example.fitnutrijournal.databinding.FragmentCalendarBinding
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()

    private var selectedDate: LocalDate? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        setupCalendarView()


        binding.selectDate.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupCalendarView() {
        val calendarView = binding.calendarView


        // 요일 타이틀 생성 메소드
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)
        configureBinders(daysOfWeek)

        binding.calendarView.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }




        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                if (container.titlesContainer.tag == null) {
                    container.titlesContainer.tag = data.yearMonth
                    container.titlesContainer.children.map { it as TextView }
                        .forEachIndexed { index, textView ->
                            val dayOfWeek = daysOfWeek[index]
                            val title =
                                dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            textView.text = title
                        }
                }
            }
        }
    }

    class MonthViewContainer(view: View) : ViewContainer(view) {
        val titlesContainer = view as ViewGroup
    }


    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            // MonthDayBinder에서 해당 변수에 data(CalendarDay)를 넣어줄 예정
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText

            init {
                view.setOnClickListener {
                    // 날짜를 클릭했을 때 이벤트
                    Log.d("CollapisbleActivity", day.toString())

                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date)
                    }

                }
            }

        }




        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            // 새 컨테이너가 필요할 때만 호출
            override fun create(view: View) = DayViewContainer(view)
            // create() 메소드의 반환 값으로 방금 생성한 DayViewContainer(view) 클래스를 지정한다.
            // 그럼 캘린더의 각 셀에 뷰가 내가 지정한 레이아웃으로 만들어지게 된다.

            // 컨테이너가 재사용될 때마다 호출
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                // DayViewContainer의 day 변수에 data 할당
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()
            }
            // bind() 메소드에선 셀 내의 뷰들에 대한 값을 세팅해준다.
            // 달력 밑에 점을 통해서 일정을 표시하는 경우가 많은데,
            // 그런 경우에도 이 bind() 메소드에서 작업해주면 될 것 같다.
        }


    }



    private fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
            //updateAdapterForDate(date)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
