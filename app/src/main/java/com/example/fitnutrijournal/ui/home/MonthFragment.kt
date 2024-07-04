// MonthFragment.kt
package com.example.fitnutrijournal.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.fitnutrijournal.databinding.FragmentMonthBinding
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

class MonthFragment : Fragment() {

    private var _binding: FragmentMonthBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by viewModels()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    private val today: String = dateFormat.format(Calendar.getInstance().time)

    companion object {
        private const val ARG_MONTH = "month"
        private const val ARG_YEAR = "year"

        fun newInstance(month: Int, year: Int): MonthFragment {
            val fragment = MonthFragment()
            val args = Bundle()
            args.putInt(ARG_MONTH, month)
            args.putInt(ARG_YEAR, year)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMonthBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        val month = arguments?.getInt(ARG_MONTH) ?: 0
        val year = arguments?.getInt(ARG_YEAR) ?: 0

        binding.monthText.text = "${year}년 ${month + 1}월"

        setupCalendar(month, year)

        return binding.root
    }

    private fun setupCalendar(month: Int, year: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        val days = mutableListOf<Date>()

        for (i in 1..daysInMonth) {
            days.add(calendar.time)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val layoutManager = GridLayoutManager(context, 7)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) 7 else 1
            }
        }

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = CalendarDateAdapter(days, today, homeViewModel)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
