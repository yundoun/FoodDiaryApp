package com.example.fitnutrijournal.ui.report

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.adapter.PhiChartFoodListAdapter
import com.example.fitnutrijournal.databinding.FragmentReportBinding
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.example.fitnutrijournal.viewmodel.ReportViewModel
import com.example.fitnutrijournal.viewmodel.ReportViewModelFactory
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@RequiresApi(Build.VERSION_CODES.O)
class ReportFragment : Fragment() {

    private lateinit var reportViewModel: ReportViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var foodListAdapter: PhiChartFoodListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        val root = binding.root

        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        reportViewModel = ViewModelProvider(this, ReportViewModelFactory(requireActivity().application, homeViewModel)).get(ReportViewModel::class.java)

        foodListAdapter = PhiChartFoodListAdapter()
        binding.foodListRecyclerView.adapter = foodListAdapter
        binding.foodListRecyclerView.layoutManager = LinearLayoutManager(context)

        reportViewModel.caloriesByMealType.observe(viewLifecycleOwner, Observer { data ->
            updatePieChart(data)
        })

        reportViewModel.mealsWithFoods.observe(viewLifecycleOwner, Observer { data ->
            Log.d("ReportFragment", "Observing meals with foods: $data")
            foodListAdapter.submitList(data)
        })

        refreshData()

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

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

        binding.pieChart.data = pieData
        binding.pieChart.setUsePercentValues(true)
        binding.pieChart.centerText = "섭취한 총 칼로리\n${totalCalories}"
        binding.pieChart.setCenterTextSize(16f)
        binding.pieChart.setCenterTextColor(R.color.text_gray)
        binding.pieChart.invalidate() // Refresh the chart
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
