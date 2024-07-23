package com.example.fitnutrijournal.ui.diet

import DietTabAdapter
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.viewmodel.DietViewModel
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("NotifyDataSetChanged")
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
                findNavController().navigate(R.id.action_navigation_diet_to_FoodDetailFragment)
            },
            { diet ->
                // Handle item long click
                showDeleteConfirmationDialog(diet)
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

    private fun setupItemTouchHelper(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val removedItem = adapter.removeItem(position)
                dietViewModel.deleteFood(removedItem)

                Log.d("DietTabFragment", "Deleted item: ${removedItem.foodName}")
                Toast.makeText(requireContext(), "Deleted ${removedItem.foodName}", Toast.LENGTH_SHORT).show()
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}

