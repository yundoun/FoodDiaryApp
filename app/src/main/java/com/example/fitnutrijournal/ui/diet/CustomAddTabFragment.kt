package com.example.fitnutrijournal.ui.diet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import com.example.fitnutrijournal.data.adapter.DietTabAdapter
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.google.android.material.snackbar.Snackbar

@RequiresApi(Build.VERSION_CODES.O)
class CustomAddTabFragment : Fragment() {
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

        adapter = DietTabAdapter(
            dietViewModel::toggleFavorite,
            dietViewModel.favorites,
            { food ->
                dietViewModel.selectFood(food.foodCd)
                findNavController().navigate(R.id.action_navigation_diet_to_FoodDetailFragment)
            },
            { food ->
                showDeleteConfirmationDialog(food)
            },  // 롱클릭 리스너 추가
            dietViewModel
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        dietViewModel.userAddedFoods.observe(viewLifecycleOwner) { diets ->
            Log.d("UserAddedTabFragment", "Updating adapter with user added diets: $diets")
            adapter.updateDiets(diets)
        }

        dietViewModel.checkedItems.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }

    }


    private fun showDeleteConfirmationDialog(food: Food) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.message_delete_title)
            setMessage(getString(R.string.message_delete_content, food.foodName))
            setPositiveButton(R.string.check) { dialog, _ ->
                // 임시 변수에 삭제될 음식 저장
                val deletedFood = food
                dietViewModel.deleteFood(food)
                dialog.dismiss()

                val snackbar =
                    Snackbar.make(requireView(), R.string.message_delete, Snackbar.LENGTH_LONG)
                snackbar.setAction(R.string.undo) {
                    // 삭제 취소 처리
                    dietViewModel.insertFood(deletedFood)
                }
                snackbar.show()
            }
            setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
        }.show()
    }

}