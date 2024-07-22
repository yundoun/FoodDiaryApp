package com.example.fitnutrijournal.ui.home

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.fitnutrijournal.databinding.FragmentMealDetailBinding
import com.example.fitnutrijournal.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MealDetailFragment : Fragment() {

    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealDetailBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeViewModel.mealType.observe(viewLifecycleOwner) { mealType ->
            val mealText = when (mealType) {
                "breakfast" -> {
                    homeViewModel.currentCaloriesBreakfast.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = calories.toString() + "kcal" + "\n" + "총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeBreakfast.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = carb.toString() + "g" + "\n" + "탄수화물"
                    }
                    homeViewModel.currentProteinIntakeBreakfast.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = protein.toString() + "g" + "\n" + "단백질"
                    }
                    homeViewModel.currentFatIntakeBreakfast.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = fat.toString() + "g" + "\n" + "지방"
                    }
                    "아침"
                }

                "lunch" -> {
                    homeViewModel.currentCaloriesLunch.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = calories.toString() + "kcal" + "\n" + "총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeLunch.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = carb.toString() + "g" + "\n" + "탄수화물"
                    }
                    homeViewModel.currentProteinIntakeLunch.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = protein.toString() + "g" + "\n" + "단백질"
                    }
                    homeViewModel.currentFatIntakeLunch.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = fat.toString() + "g" + "\n" + "지방"
                    }
                    "점심"
                }

                "dinner" -> {
                    homeViewModel.currentCaloriesDinner.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = calories.toString() + "kcal" + "\n" + "총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeDinner.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = carb.toString() + "g" + "\n" + "탄수화물"
                    }
                    homeViewModel.currentProteinIntakeDinner.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = protein.toString() + "g" + "\n" + "단백질"
                    }
                    homeViewModel.currentFatIntakeDinner.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = fat.toString() + "g" + "\n" + "지방"
                    }
                    "저녁"
                }
                "snack" -> {
                    homeViewModel.currentCaloriesSnack.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = calories.toString() + "kcal" + "\n" + "총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeSnack.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = carb.toString() + "g" + "\n" + "탄수화물"
                    }
                    homeViewModel.currentProteinIntakeSnack.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = protein.toString() + "g" + "\n" + "단백질"
                    }
                    homeViewModel.currentFatIntakeSnack.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = fat.toString() + "g" + "\n" + "지방"
                    }
                    "간식"
                }
                else -> "식사"
            }
            binding.mealType.text = mealText
        }



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
