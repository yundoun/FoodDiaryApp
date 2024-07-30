package com.example.fitnutrijournal.ui.home

import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.CalendarDayLayoutBinding
import com.example.fitnutrijournal.databinding.FragmentCalendarBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
@RequiresApi(Build.VERSION_CODES.O)
class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    private var selectedDate: LocalDate? = null

    private val monthYearFormatter = DateTimeFormatter.ofPattern("yyyy.MM")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).showBottomNavigation(false)

        setupCalendarView()

        binding.writeDiary.setOnClickListener {
            findNavController().navigate(R.id.action_calendarFragment_to_diaryFragment)
        }


        binding.selectDate.setOnClickListener {
            selectedDate?.let {
                homeViewModel.updateCurrentDate(it)
                homeViewModel.updateSelectedDate(it)
                Log.d("CalendarFragment", "Selected date updated to: $it")
                findNavController().popBackStack()
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        homeViewModel.currentDate.observe(viewLifecycleOwner, Observer { currentDate ->
            selectedDate = LocalDate.parse(currentDate)
            binding.calendarView.notifyDateChanged(selectedDate!!)
            binding.calendarView.scrollToDate(selectedDate!!) // 선택된 날짜에 맞게 달력 바인딩
        })
    }

    // 캘린더 뷰와 스크롤 리스너 설정
    private fun setupCalendarView() {
        val daysOfWeek = daysOfWeek(firstDayOfWeek = DayOfWeek.SUNDAY)
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(100)
        val endMonth = currentMonth.plusMonths(100)

        configureBinders(daysOfWeek)

        binding.calendarView.apply {
            setup(startMonth, endMonth, daysOfWeek.first())
            scrollToMonth(currentMonth)
        }

        binding.calendarView.monthScrollListener = { month ->
            updateMonthTitle(month.yearMonth)
        }

        binding.calendarView.monthHeaderBinder =
            object : MonthHeaderFooterBinder<MonthViewContainer> {
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

    // 월 제목 업데이트
    private fun updateMonthTitle(yearMonth: YearMonth) {
        val formattedMonth = yearMonth.format(monthYearFormatter)
        binding.Month.text = formattedMonth
    }

    // 날짜를 선택하고 UI를 업데이트
    private fun selectDate(date: LocalDate, dayPosition: DayPosition) {
        if (dayPosition == DayPosition.MonthDate && selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
        }
    }

    // 일(day) 및 월(month) 헤더 바인더 구성
    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date, day.position)
                    }
                }
            } // 날짜를 클릭했을 때 이벤트
        }

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()

                // 날짜의 위치에 따라 색상 설정
                // 날짜의 위치에 따라 색상 설정
                when {
                    data.position == DayPosition.MonthDate -> {
                        val drawable = container.textView.background as GradientDrawable
                        when {
                            data.date == selectedDate -> {
                                drawable.setColor(
                                    ContextCompat.getColor(requireContext(), R.color.calendar_today)
                                )
                                container.textView.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.white)
                                )
                            }
                            data.date == LocalDate.now() -> {
                                drawable.setColor(
                                    ContextCompat.getColor(requireContext(), R.color.calendar_date_select)
                                )
                                container.textView.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.white)
                                )
                            }
                            else -> {
                                drawable.setColor(
                                    ContextCompat.getColor(requireContext(), android.R.color.transparent)
                                )
                                container.textView.setTextColor(
                                    ContextCompat.getColor(requireContext(), R.color.black)
                                )
                            }
                        }
                    }
                    else -> {
                        val drawable = container.textView.background as GradientDrawable
                        drawable.setColor(
                            ContextCompat.getColor(requireContext(), android.R.color.transparent)
                        )
                        container.textView.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.out_of_month_color)
                        )
                    }
                }

            }
        }
    }

    // 월 헤더의 ViewContainer
    class MonthViewContainer(view: View) : ViewContainer(view) {
        val titlesContainer = view as ViewGroup
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

