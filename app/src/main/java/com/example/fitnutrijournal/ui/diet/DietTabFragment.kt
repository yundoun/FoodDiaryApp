package com.example.fitnutrijournal.ui.diet

import DietTabAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.viewmodel.DietViewModel

class DietTabFragment : Fragment() {

    companion object {
        private const val ARG_TAB_NAME = "tab_name"

        fun newInstance(tabName: String) = DietTabFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_TAB_NAME, tabName)
            }
        }
    }

    private val dietViewModel: DietViewModel by activityViewModels()
    private lateinit var adapter: DietTabAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_diet_tab, container, false)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = DietTabAdapter(
            emptyList(),
            dietViewModel::toggleFavorite,
            dietViewModel.favorites,
            { diet ->
                // Handle item click
                dietViewModel.selectFood(diet.foodCd)
                findNavController().navigate(R.id.action_navigation_diet_to_dietDetailFragment)
            },
            dietViewModel // DietViewModel 전달
        )
        recyclerView.adapter = adapter



        dietViewModel.filteredFoods.observe(viewLifecycleOwner) { foods ->
            Log.d("DietTabFragment", "Updating adapter with diets: $foods")
            adapter.updateDiets(foods)
        }

        dietViewModel.isCheckboxVisible.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged() // 가시성 변경 시 어댑터 갱신
        }

        // 체크박스 상태 변경 시 어댑터 갱신
        dietViewModel.checkedItems.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

    }
}

