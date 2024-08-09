package com.example.fitnutrijournal.ui.diet

import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.FragmentFoodDetailBinding
import com.example.fitnutrijournal.ui.Activity.MainActivity
import com.example.fitnutrijournal.utils.ModalBottomSheet
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.google.android.material.snackbar.Snackbar

@RequiresApi(Build.VERSION_CODES.O)
class FoodDetailFragment : Fragment(), ModalBottomSheet.OnMealTypeSelectedListener {

    private val dietViewModel: DietViewModel by activityViewModels()
    private lateinit var binding: FragmentFoodDetailBinding
    private var mealId: Long = -1L // mealId 변수를 추가

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFoodDetailBinding.inflate(inflater, container, false)
        binding.viewModel = dietViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        (activity as MainActivity).showBottomNavigation(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mealId = arguments?.getLong("mealId") ?: -1L

        setupUI()
        setupObservers()
        updateButtonStates()
    }

    private fun setupUI() {
        binding.apply {

            btnBack.setOnClickListener { findNavController().popBackStack() }
            favoriteBtn.setOnClickListener { dietViewModel.toggleFavorite() }
            binding.btnAddFromLibrary.setOnClickListener { showBottomSheet() }

            btnUpdate.setOnClickListener {
                dietViewModel.updateFoodIntake(mealId)
                showToast(R.string.message_meal_update)
                findNavController().popBackStack()
            }

            btnSave.setOnClickListener {
                dietViewModel.saveCurrentFoodIntake()
                showToast(R.string.message_meal_save)
                findNavController().popBackStack()
            }

            totalContentInput.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) { updateButtonStates() }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    dietViewModel.updateTotalContent(s.toString())
                }
            })
        }
    }

    private fun setupObservers() {
        dietViewModel.apply {
            isSaveButtonVisible.observe(viewLifecycleOwner) { isVisible ->
                binding.btnSave.visibility = if (isVisible) View.VISIBLE else View.GONE
                binding.btnUpdate.visibility = if (isVisible) View.GONE else View.VISIBLE
                Log.d("FoodDetailFragment", "isSaveButtonVisible: $isVisible")
            }
            isUpdateButtonVisible.observe(viewLifecycleOwner) { isVisible ->
                binding.btnUpdate.visibility = if (isVisible) View.VISIBLE else View.GONE
                Log.d("FoodDetailFragment", "isUpdateButtonVisible: $isVisible")
            }
            isAddFromLibraryButtonVisible.observe(viewLifecycleOwner) { isVisible ->
                binding.btnAddFromLibrary.visibility = if (isVisible) View.VISIBLE else View.GONE
                Log.d("FoodDetailFragment", "isAddFromLibraryButtonVisible: $isVisible")
            }
            selectedFood.observe(viewLifecycleOwner) { food ->
                binding.totalContentInput.setText(
                    selectedMealQuantity.value?.toString() ?: food?.servingSize?.toString() ?: ""
                )
            }
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun updateButtonStates() {
        val isTotalContentNotEmpty = binding.totalContentInput.text.toString().isNotEmpty()
        binding.btnSave.isEnabled = isTotalContentNotEmpty
        binding.btnUpdate.isEnabled = isTotalContentNotEmpty
    }

    private fun showBottomSheet() {
        ModalBottomSheet(this).show(childFragmentManager, ModalBottomSheet.TAG)
    }

    override fun onMealTypeSelected(mealType: String) {
        dietViewModel.setMealType(mealType)
        dietViewModel.saveCurrentFoodIntake()
        Snackbar.make(requireView(), R.string.message_meal_add, Snackbar.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dietViewModel.clearSelectedMealQuantity()
    }
}
