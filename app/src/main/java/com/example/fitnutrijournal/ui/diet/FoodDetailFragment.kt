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

        binding.btnUpdate.setOnClickListener {
            dietViewModel.updateFoodIntake(mealId)
            Toast.makeText(context, "식사가 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.favoriteBtn.setOnClickListener {
            dietViewModel.toggleFavorite()
        }

        dietViewModel.isSaveButtonVisible.observe(viewLifecycleOwner) { isVisible ->
            binding.btnSave.visibility = if (isVisible) View.VISIBLE else View.GONE
            binding.btnUpdate.visibility = if (isVisible) View.GONE else View.VISIBLE
            Log.d("FoodDetailFragment", "isSaveButtonVisible: $isVisible")
        }

        dietViewModel.isUpdateButtonVisible.observe(viewLifecycleOwner) { isVisible ->
            binding.btnUpdate.visibility = if (isVisible) View.VISIBLE else View.GONE
            Log.d("FoodDetailFragment", "isUpdateButtonVisible: $isVisible")
        }

        dietViewModel.isAddFromLibraryButtonVisible.observe(viewLifecycleOwner) { isVisible ->
            binding.btnAddFromLibrary.visibility = if (isVisible) View.VISIBLE else View.GONE
            Log.d("FoodDetailFragment", "isAddFromLibraryButtonVisible: $isVisible")
        }

        dietViewModel.selectedFood.observe(viewLifecycleOwner) { food ->
            val selectedMealQuantity = dietViewModel.selectedMealQuantity.value
            if (selectedMealQuantity != null) {
                binding.totalContentInput.setText(selectedMealQuantity.toString())
                Log.d("FoodDetailFragment", "selectedMealQuantity: $selectedMealQuantity")

            } else {
                binding.totalContentInput.setText(food?.servingSize?.toString() ?: "")
            }
        }

        binding.totalContentInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateButtonStates()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dietViewModel.updateTotalContent(s.toString())
            }
        })

        binding.btnSave.setOnClickListener {
            dietViewModel.saveCurrentFoodIntake()
            Toast.makeText(context, "식사가 저장되었습니다.", Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        binding.btnAddFromLibrary.setOnClickListener {
            val modalBottomSheet = ModalBottomSheet(this)
            modalBottomSheet.show(childFragmentManager, ModalBottomSheet.TAG)
        }

        updateButtonStates()
    }

    private fun updateButtonStates() {
        val totalContent = binding.totalContentInput.text.toString()
        val isTotalContentNotEmpty = totalContent.isNotEmpty()

        binding.btnSave.isEnabled = isTotalContentNotEmpty
        binding.btnUpdate.isEnabled = isTotalContentNotEmpty
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dietViewModel.clearSelectedMealQuantity()
    }

    override fun onMealTypeSelected(mealType: String) {
        dietViewModel.setMealType(mealType)
        dietViewModel.saveCurrentFoodIntake()
        Snackbar.make(requireView(), "식사가 추가되었습니다.", Snackbar.LENGTH_SHORT).show()
    }

}
