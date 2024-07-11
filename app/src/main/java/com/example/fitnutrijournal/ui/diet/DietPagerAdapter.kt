package com.example.fitnutrijournal.ui.diet

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.fitnutrijournal.viewmodel.FavoriteTabFragment

class DietPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    companion object {
        private val TAB_TITLES = arrayOf("음식", "최근", "즐겨찾기")
    }

    override fun getItemCount(): Int {
        return TAB_TITLES.size
    }

    override fun createFragment(position: Int): Fragment {
        return if (position == 2) {
            FavoriteTabFragment()
        } else {
            DietTabFragment.newInstance(TAB_TITLES[position])
        }
    }

    fun getTabTitle(position: Int): String {
        return TAB_TITLES[position]
    }
}
