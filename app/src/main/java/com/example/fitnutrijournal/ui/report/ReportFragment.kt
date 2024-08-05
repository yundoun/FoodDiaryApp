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
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import com.example.fitnutrijournal.viewmodel.ReportViewModel
import com.example.fitnutrijournal.viewmodel.ReportViewModelFactory
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

@RequiresApi(Build.VERSION_CODES.O)
class ReportFragment : Fragment() {

    private lateinit var reportViewModel: ReportViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_report, container, false)
        pieChart = root.findViewById(R.id.pieChart)

        // ViewModel 초기화
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        reportViewModel = ViewModelProvider(
            this,
            ReportViewModelFactory(requireActivity().application, homeViewModel)
        )[ReportViewModel::class.java]

        reportViewModel.caloriesByMealType.observe(viewLifecycleOwner, Observer { data ->
            updatePieChart(data)
        })

        return root
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


        val dataSet = PieDataSet(entries, "Calories by Meal Type")
        dataSet.colors = listOf(
            resources.getColor(R.color.colorBreakfast, null),
            resources.getColor(R.color.colorLunch, null),
            resources.getColor(R.color.colorDinner, null),
            resources.getColor(R.color.colorSnack, null)
        )

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)

        pieChart.data = pieData
        pieChart.setUsePercentValues(true)
        pieChart.centerText = "섭취한 총 칼로리\n${totalCalories}"
        pieChart.setCenterTextSize(12f)
        pieChart.setCenterTextColor(Color.BLACK)
        pieChart.invalidate() // Refresh the chart
    }
}

