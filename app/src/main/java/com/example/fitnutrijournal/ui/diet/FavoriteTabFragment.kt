package com.example.fitnutrijournal.ui.diet

import DietTabAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@RequiresApi(Build.VERSION_CODES.O)
class FavoriteTabFragment : Fragment() {

    private val dietViewModel: DietViewModel by activityViewModels()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_diet_tab, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)


        val adapter = DietTabAdapter(
            emptyList(),
            dietViewModel::toggleFavorite,
            dietViewModel.favorites,
            { diet ->
                dietViewModel.selectFood(diet.foodCd)
                findNavController().navigate(R.id.action_navigation_diet_to_FoodDetailFragment)
            },
            { food -> showDeleteConfirmationDialog(food) },  // 롱클릭 리스너 추가
            dietViewModel
        )
        recyclerView.adapter = adapter

        dietViewModel.favoriteFoods.observe(viewLifecycleOwner) { favorites ->
            adapter.updateDiets(favorites)
        }

        dietViewModel.checkedItems.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

        return view
    }
    private fun showDeleteConfirmationDialog(food: Food) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("삭제 확인")
            setMessage("이 항목을 삭제하시겠습니까?")
            setPositiveButton("확인") { dialog, _ ->
                dietViewModel.deleteFood(food)
                dialog.dismiss()
            }
            setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }
}
