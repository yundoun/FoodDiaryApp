package com.example.fitnutrijournal.ui.diet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diet_tab, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)


        val tabName = arguments?.getString(ARG_TAB_NAME) ?: "음식"
        val items = getDataForTab(tabName)

        dietViewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            recyclerView.adapter = DietTabAdapter(items, dietViewModel::toggleFavorite, favorites)
        }


        return view
    }

    private fun getDataForTab(tabName: String): List<String> {
        return when (tabName) {
            "음식" -> List(20) { "Item ${it + 1} for Tab 1" }
            "최근" -> List(20) { "Item ${it + 1} for Tab 2" }
            "즐겨찾기" -> List(20) { "Item ${it + 1} for Tab 3" }
            else -> emptyList()
        }
    }
}

