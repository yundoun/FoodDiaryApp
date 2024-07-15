package com.example.fitnutrijournal.ui.diet

import DietTabAdapter
import android.os.Bundle
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

class FavoriteTabFragment : Fragment() {

    private val dietViewModel: DietViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diet_tab, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)


        dietViewModel.favoriteDiets.observe(viewLifecycleOwner) { favorites ->
            recyclerView.adapter = DietTabAdapter(
                favorites,
                dietViewModel::toggleFavorite,
                dietViewModel.favorites,
                { diet ->
                    // 아이템 클릭 이벤트
                    dietViewModel.selectDiet(diet.foodCode)
                    findNavController().navigate(R.id.action_navigation_diet_to_dietDetailFragment)
                },
                dietViewModel // DietViewModel 전달
            )
        }

        return view
    }
}
