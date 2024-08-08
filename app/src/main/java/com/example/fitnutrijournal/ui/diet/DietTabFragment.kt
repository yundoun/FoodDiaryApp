package com.example.fitnutrijournal.ui.diet

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.adapter.DietTabAdapter
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.databinding.FragmentDietTabBinding
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.google.android.material.snackbar.Snackbar

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
    private var _binding: FragmentDietTabBinding? = null
    private val binding get() = _binding!!
    lateinit var adapter: DietTabAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDietTabBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)

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
        binding.recyclerView.adapter = adapter

        dietViewModel.filteredFoods.observe(viewLifecycleOwner) { foods ->
            adapter.updateDiets(foods)
        }

        dietViewModel.isCheckboxVisible.observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged() // 가시성 변경 시 어댑터 갱신
        }

        dietViewModel.checkedItems.observe(viewLifecycleOwner, Observer { checkedItems ->
            Log.d("DietTabFragment", "checkedItems 변경됨: ${checkedItems.size}")
            checkedItems.forEach { food ->
                Log.d("DietTabFragment", "체크된 아이템: ${food.foodCd}")
            }
            adapter.updateCheckedItems()
        })
    }

    fun clearCheckedItems() {
        dietViewModel.clearCheckedItems()
    }

    fun clearSelectedCountFoodItem() {
        dietViewModel.clearSelectedCountFoodItem()
    }

    @SuppressLint("StringFormatInvalid")
    private fun showDeleteConfirmationDialog(food: Food) {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(R.string.message_delete_title)
            setMessage(getString(R.string.message_delete_content, food.foodName))
            setPositiveButton(R.string.check) { dialog, _ ->
                // 임시 변수에 삭제될 음식 저장
                val deletedFood = food
                dietViewModel.deleteFood(food)
                dialog.dismiss()

                val snackbar = Snackbar.make(requireView(), R.string.message_delete, Snackbar.LENGTH_LONG)
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
