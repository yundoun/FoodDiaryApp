// CalendarDateAdapter.kt
package com.example.fitnutrijournal.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.databinding.ItemCalendarDateBinding
import com.example.fitnutrijournal.databinding.ItemWeekDayHeaderBinding
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarDateAdapter(
    private val days: List<Date>,
    private val today: String,
    private val viewModel: HomeViewModel
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    private val viewTypeHeader = 0
    private val viewTypeDate = 1

    class WeekDayViewHolder(val binding: ItemWeekDayHeaderBinding) : RecyclerView.ViewHolder(binding.root)

    class DateViewHolder(val binding: ItemCalendarDateBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) viewTypeHeader else viewTypeDate
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == viewTypeHeader) {
            val binding = ItemWeekDayHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            WeekDayViewHolder(binding)
        } else {
            val binding = ItemCalendarDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DateViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == viewTypeHeader) {
            val binding = (holder as WeekDayViewHolder).binding
            val weekDays = listOf("일", "월", "화", "수", "목", "금", "토")
            for (i in 0 until binding.weekDaysContainer.childCount) {
                val textView = binding.weekDaysContainer.getChildAt(i) as TextView
                textView.text = weekDays[i]
            }
        } else {
            val binding = (holder as DateViewHolder).binding
            val date = days[position - 1]
            val calendar = Calendar.getInstance().apply { time = date }
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            binding.dateText.text = day.toString()

            val dateStr = dateFormat.format(date)
            if (dateStr == today) {
                binding.dateText.setTextColor(Color.WHITE)
                binding.dateText.setBackgroundColor(Color.GREEN)
            } else if (dateStr == viewModel.date.value) {
                binding.dateText.setTextColor(Color.WHITE)
                binding.dateText.setBackgroundColor(Color.parseColor("#A5D6A7")) // 연한 초록색
            } else {
                binding.dateText.setTextColor(Color.BLACK)
                binding.dateText.setBackgroundColor(Color.TRANSPARENT)
            }

            binding.root.setOnClickListener {
                viewModel.date.value = dateStr
                notifyDataSetChanged()
            }
        }
    }

    override fun getItemCount(): Int {
        return days.size + 1 // +1 for the header
    }
}
