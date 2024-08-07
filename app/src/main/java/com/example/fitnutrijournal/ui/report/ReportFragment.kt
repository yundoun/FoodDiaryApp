package com.example.fitnutrijournal.ui.report

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.adapter.PhiChartFoodListAdapter
import com.example.fitnutrijournal.databinding.FragmentReportBinding
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.example.fitnutrijournal.viewmodel.ReportViewModel
import com.example.fitnutrijournal.viewmodel.ReportViewModelFactory
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

@RequiresApi(Build.VERSION_CODES.O)
class ReportFragment : Fragment() {

    private lateinit var reportViewModel: ReportViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodListAdapter: PhiChartFoodListAdapter

    // Pie chart
    private var usePercentValues = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // view model 초기화
        homeViewModel = ViewModelProvider(requireActivity())[HomeViewModel::class.java]
        reportViewModel = ViewModelProvider(this, ReportViewModelFactory(requireActivity().application, homeViewModel))[ReportViewModel::class.java]

        // data binding 초기화
        _binding = FragmentReportBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = reportViewModel
            homeViewModel = this@ReportFragment.homeViewModel
        }

        (activity as MainActivity).showBottomNavigation(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        observeViewModels()
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupUI() {
        binding.calendarLayout.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_report_to_calendarFragment)
        }

        foodListAdapter = PhiChartFoodListAdapter()
        binding.foodListRecyclerView.apply {
            adapter = foodListAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun observeViewModels() {
        reportViewModel.caloriesByMealType.observe(viewLifecycleOwner, Observer { data ->
            updatePieChart(data)
        })

        reportViewModel.mealsWithFoods.observe(viewLifecycleOwner, Observer { data ->
            Log.d("ReportFragment", "Observing meals with foods: $data")
            foodListAdapter.submitList(data)
        })
    }


    @SuppressLint("SetTextI18n")
    private fun updatePieChart(data: Map<String, Int>) {
        Log.d("ReportFragment", "Updating PieChart with: $data")

        val totalCalories = data.values.sum()

        val mealTypeMapping = mapOf(
            "breakfast" to "아침",
            "lunch" to "점심",
            "dinner" to "저녁",
            "snack" to "간식"
        )

        val entries = data.map {
            val mealType = mealTypeMapping[it.key] ?: it.key // 한글 매핑이 없으면 원래 키 사용
            PieEntry(it.value.toFloat(), mealType)
        }

        val dataSet = PieDataSet(entries, null)
        dataSet.colors = listOf(
            resources.getColor(R.color.colorBreakfast, null),
            resources.getColor(R.color.colorLunch, null),
            resources.getColor(R.color.colorDinner, null),
            resources.getColor(R.color.colorSnack, null)
        )

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)
        pieData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return if (usePercentValues) {
                    "${value.toInt()}%"
                } else {
                    "${value.toInt()}g"
                }
            }
        })


        binding.pieChart.data = pieData
        binding.pieChart.apply {
            setUsePercentValues(usePercentValues)
            centerText = "섭취한 칼로리\n${totalCalories}"
            setCenterTextSize(16f)
            setCenterTextColor(R.color.text_gray)
            invalidate() // Refresh the chart
        }
        binding.totalCalorie.text = "${totalCalories}kcal"

        binding.pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                usePercentValues = false
                binding.pieChart.setUsePercentValues(false)
                binding.pieChart.data.notifyDataChanged()
                binding.pieChart.notifyDataSetChanged()
                binding.pieChart.invalidate() // Refresh the chart to show the values instead of percent
            }

            override fun onNothingSelected() {
                // Optional: Reset to percent values when nothing is selected
                usePercentValues = true
                binding.pieChart.setUsePercentValues(true)
                binding.pieChart.data.notifyDataChanged()
                binding.pieChart.notifyDataSetChanged()
                binding.pieChart.invalidate() // Refresh the chart to show the percent values again
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun refreshData() {
        val currentDate = homeViewModel.currentDate.value
        if (currentDate != null) {
            reportViewModel.loadCaloriesByMealType(currentDate)
            reportViewModel.loadMealsWithFoods(currentDate)
        }
    }
}
