// CalendarDateAdapter.kt
package com.example.fitnutrijournal.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.databinding.ItemCalendarDateBinding
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarDateAdapter(
    private val days: List<Date>,
    private val today: String,
    private val viewModel: HomeViewModel
) : RecyclerView.Adapter<CalendarDateAdapter.ViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)

    class ViewHolder(val binding: ItemCalendarDateBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCalendarDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = days[position]
        val calendar = Calendar.getInstance().apply { time = date }
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        holder.binding.dateText.text = day.toString()

        // 오늘 날짜 강조
        val dateStr = dateFormat.format(date)
        if (dateStr == today) {
            holder.binding.dateText.setTextColor(Color.WHITE)
            holder.binding.dateText.setBackgroundColor(Color.GREEN)
        } else if (dateStr == viewModel.date.value) {
            holder.binding.dateText.setTextColor(Color.WHITE)
            holder.binding.dateText.setBackgroundColor(Color.parseColor("#A5D6A7")) // 연한 초록색
        } else {
            holder.binding.dateText.setTextColor(Color.BLACK)
            holder.binding.dateText.setBackgroundColor(Color.TRANSPARENT)
        }

        holder.binding.root.setOnClickListener {
            viewModel.date.value = dateStr
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return days.size
    }
}
