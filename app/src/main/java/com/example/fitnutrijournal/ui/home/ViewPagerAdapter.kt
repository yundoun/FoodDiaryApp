package com.example.fitnutrijournal.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fitnutrijournal.ui.home.detail.Breakfast
import com.example.fitnutrijournal.ui.home.detail.Dinner
import com.example.fitnutrijournal.ui.home.detail.Lunch
import com.example.fitnutrijournal.ui.home.detail.Snack

class ViewPagerAdapter(fragmentActivity: TodaySummaryDetailFragment) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> Breakfast()
            1 -> Lunch()
            2 -> Dinner()
            3 -> Snack()
            else -> Breakfast()
        }
    }
}