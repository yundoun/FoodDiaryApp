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

    }

    fun clearCheckedItems() {
        dietViewModel.clearCheckedItems()
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



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
