package com.example.fitnutrijournal.ui.home

import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.CalendarDayLayoutBinding
import com.example.fitnutrijournal.databinding.FragmentCalendarBinding
import com.example.fitnutrijournal.ui.Activity.MainActivity
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.example.fitnutrijournal.viewmodel.MemoViewModel
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
    private val memoViewModel: MemoViewModel by activityViewModels()
    private var selectedDate: LocalDate? = null
    private val monthYearFormatter = DateTimeFormatter.ofPattern("yyyy.MM")
    private var isFirstTime = true

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
        setupObservers()
        setupClickListeners()

        // 처음에는 현재 날짜로 설정
        if (isFirstTime) {
            setupCurrentDateObserver()
            isFirstTime = false
        }
    }

    private fun setupClickListeners() {
        binding.writeDiary.setOnClickListener {
            selectedDate?.let {
                memoViewModel.updateClickedDate(it)
                findNavController().navigate(R.id.action_calendarFragment_to_diaryFragment)
            }
        }

        binding.diary.setOnClickListener {
            selectedDate?.let {
                memoViewModel.updateClickedDate(it)
                findNavController().navigate(R.id.action_calendarFragment_to_diaryFragment)
            }
        }

        binding.selectDate.setOnClickListener {
            selectedDate?.let {
                homeViewModel.updateCurrentDate(it)
                homeViewModel.updateSelectedDate(it)
                memoViewModel.updateClickedDate(it)
                Log.d("CalendarFragment", "Selected date updated to: $it")
                Toast.makeText(requireContext(), "$it 날짜 변경됨", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupObservers() {
        memoViewModel.clickedDate.observe(viewLifecycleOwner, Observer { date ->
            selectedDate = LocalDate.parse(date)
            selectedDate?.let {
                binding.calendarView.notifyDateChanged(it)
                binding.calendarView.scrollToDate(it)
                memoViewModel.loadMemoByDate(it.toString()) // LocalDate를 문자열로 변환
            }
        })

        memoViewModel.clickedDateMemo.observe(viewLifecycleOwner, Observer { memo ->
            binding.diary.text = memo?.content
        })

        // 월별 메모 데이터를 뽑아서 데이터 있는 지 표시하기 위한 Observer
        memoViewModel.monthlyMemos.observe(viewLifecycleOwner, Observer { memos ->
            binding.calendarView.notifyCalendarChanged() // 달력이 전체적으로 변경됨을 알림
        })

        // DailyIntakeRecord 데이터를 관찰하여 달력에 표시하기 위한 Observer 추가
        homeViewModel.monthlyIntakeRecords.observe(viewLifecycleOwner, Observer { records ->
            binding.calendarView.notifyCalendarChanged() // 달력이 전체적으로 변경됨을 알림
        })
    }

    private fun setupCurrentDateObserver() {
        homeViewModel.currentDate.observe(viewLifecycleOwner, Observer { currentDate ->
            selectedDate = LocalDate.parse(currentDate)
            selectedDate?.let {
                binding.calendarView.notifyDateChanged(it)
                binding.calendarView.scrollToDate(it)
                memoViewModel.updateClickedDate(it)
                memoViewModel.loadMemoByDate(it.toString())
            }
        })
    }

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
            memoViewModel.loadMemosForMonth(month.yearMonth) // 스크롤된 월의 메모 로드
            homeViewModel.loadIntakeRecordsForMonth(month.yearMonth) // 스크롤된 월의 섭취 기록 로드
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
                                val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                textView.text = title
                            }
                    }
                }
            }
    }

    private fun updateMonthTitle(yearMonth: YearMonth) {
        val formattedMonth = yearMonth.format(monthYearFormatter)
        binding.Month.text = formattedMonth
    }

    private fun selectDate(date: LocalDate, dayPosition: DayPosition) {
        if (dayPosition == DayPosition.MonthDate && selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { binding.calendarView.notifyDateChanged(it) }
            binding.calendarView.notifyDateChanged(date)
            Log.d("CalendarFragment", "Selected date: $date")
            memoViewModel.updateClickedDate(date)
        }
    }

    private fun configureBinders(daysOfWeek: List<DayOfWeek>) {
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = CalendarDayLayoutBinding.bind(view).calendarDayText
            val memoIndicator = CalendarDayLayoutBinding.bind(view).memoIndicator // 빨간 점을 위한 뷰
            val intakeIndicator = CalendarDayLayoutBinding.bind(view).intakeIndicator // 초록색 점을 위한 뷰

            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        selectDate(day.date, day.position)
                    }
                }
            }
        }

        binding.calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)

            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                container.textView.text = data.date.dayOfMonth.toString()

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

                        // 날짜에 메모가 있는 경우 빨간 점 표시
                        if (memoViewModel.monthlyMemos.value?.containsKey(data.date) == true) {
                            container.memoIndicator.visibility = View.VISIBLE
                        } else {
                            container.memoIndicator.visibility = View.INVISIBLE
                        }

                        // 날짜에 DailyIntakeRecord가 있는 경우 초록색 점 표시
                        if (homeViewModel.monthlyIntakeRecords.value?.containsKey(data.date) == true) {
                            container.intakeIndicator.visibility = View.VISIBLE
                        } else {
                            container.intakeIndicator.visibility = View.INVISIBLE
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
                        container.memoIndicator.visibility = View.INVISIBLE
                        container.intakeIndicator.visibility = View.INVISIBLE
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
