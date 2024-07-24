package com.example.fitnutrijournal.ui.home

import android.annotation.SuppressLint
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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.model.Meal
import com.example.fitnutrijournal.databinding.FragmentMealDetailBinding
import com.example.fitnutrijournal.ui.diet.DietTabAdapter
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.DietViewModelFactory
import com.example.fitnutrijournal.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
class MealDetailFragment : Fragment() {

    private var _binding: FragmentMealDetailBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()
    private val dietViewModel: DietViewModel by activityViewModels {
        DietViewModelFactory(requireActivity().application, homeViewModel)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMealDetailBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        (activity as MainActivity).showBottomNavigation(false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        dietViewModel.setCheckboxVisible(null)

        val recyclerView = binding.foodList
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = DietTabAdapter(
            emptyList<Meal>(),
            { item ->
                if (item is Meal) dietViewModel.toggleFavorite(item)
            },
            dietViewModel.favorites,
            { item ->
                if (item is Meal) {
                    dietViewModel.selectFood(item.dietFoodCode)
                    dietViewModel.setSaveButtonVisibility(false)
                    dietViewModel.setUpdateButtonVisibility(true)
                    findNavController().navigate(R.id.action_mealDetailFragment_to_foodDetailFragment)
                }
            },
            null,  // 롱클릭 리스너를 null로 설정
            dietViewModel
        )
        recyclerView.adapter = adapter

        // ItemTouchHelper 설정
        setupItemTouchHelper(recyclerView, adapter)

        binding.btnAddFood.setOnClickListener {
            val source = homeViewModel.mealType.value ?: "breakfast"
            Log.d("MealDetailFragment", "Adding food to mealType: $source")
            val action =
                MealDetailFragmentDirections.actionMealDetailFragmentToNavigationDiet(source)
            findNavController().navigate(action)
        }

        homeViewModel.mealType.observe(viewLifecycleOwner) { mealType ->
            Log.d("MealDetailFragment", "Observed mealType: $mealType")
            dietViewModel.setMealType(mealType) // HomeViewModel의 mealType을 DietViewModel에 전달
            val mealText = when (mealType) {
                "breakfast" -> {
                    homeViewModel.currentCaloriesBreakfast.observe(viewLifecycleOwner) { calories ->
                        Log.d("MealDetailFragment", "Breakfast calories updated: $calories")
                        binding.calories.text = calories.toString() + "kcal" + "\n" + "총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeBreakfast.observe(viewLifecycleOwner) { carb ->
                        Log.d("MealDetailFragment", "Breakfast carbs updated: $carb")
                        binding.carb.text = carb.toString() + "g" + "\n" + "탄수화물"
                    }
                    homeViewModel.currentProteinIntakeBreakfast.observe(viewLifecycleOwner) { protein ->
                        Log.d("MealDetailFragment", "Breakfast protein updated: $protein")
                        binding.protein.text = protein.toString() + "g" + "\n" + "단백질"
                    }
                    homeViewModel.currentFatIntakeBreakfast.observe(viewLifecycleOwner) { fat ->
                        Log.d("MealDetailFragment", "Breakfast fat updated: $fat")
                        binding.fat.text = fat.toString() + "g" + "\n" + "지방"
                    }
                    "아침"
                }

                "lunch" -> {
                    homeViewModel.currentCaloriesLunch.observe(viewLifecycleOwner) { calories ->
                        Log.d("MealDetailFragment", "Lunch calories updated: $calories")
                        binding.calories.text = calories.toString() + "kcal" + "\n" + "총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeLunch.observe(viewLifecycleOwner) { carb ->
                        Log.d("MealDetailFragment", "Lunch carbs updated: $carb")
                        binding.carb.text = carb.toString() + "g" + "\n" + "탄수화물"
                    }
                    homeViewModel.currentProteinIntakeLunch.observe(viewLifecycleOwner) { protein ->
                        Log.d("MealDetailFragment", "Lunch protein updated: $protein")
                        binding.protein.text = protein.toString() + "g" + "\n" + "단백질"
                    }
                    homeViewModel.currentFatIntakeLunch.observe(viewLifecycleOwner) { fat ->
                        Log.d("MealDetailFragment", "Lunch fat updated: $fat")
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

        // Meal 데이터를 관찰하여 RecyclerView 업데이트
        homeViewModel.filteredFoods.observe(viewLifecycleOwner, Observer { foods ->
            Log.d("MealDetailFragment", "Filtered foods observed: ${foods.map { it.foodName }}")
            val meals = foods.map { food ->
                Meal(
                    id = 0L, // 실제 ID를 설정해야 합니다.
                    date = "", // 실제 날짜를 설정해야 합니다.
                    mealType = "", // 실제 식사 유형을 설정해야 합니다.
                    dietFoodCode = food.foodCd,
                    quantity = food.servingSize
                )
            }
            adapter.updateItems(meals)
        })
    }

    private fun setupItemTouchHelper(recyclerView: RecyclerView, adapter: DietTabAdapter) {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val item = adapter.getItem(position)
                    if (item is Meal) {
                        val removedItem = adapter.removeItemById(item.id)
                        if (removedItem != null) {
                            Log.d("MealDetailFragment", "onSwiped called for item: ${removedItem.dietFoodCode}")
                            dietViewModel.deleteMealById(removedItem.id)

                            Toast.makeText(
                                requireContext(),
                                "${removedItem.dietFoodCode}이 삭제 되었습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()

        // RecyclerView에 표시되는 데이터를 관찰하고 변경 사항을 반영
        Log.d("MealDetailFragment", "onResume called, refreshing filtered foods")
        homeViewModel.refreshFilteredFoods()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
