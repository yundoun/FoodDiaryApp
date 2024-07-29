package com.example.fitnutrijournal.ui.home

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.model.MealWithFood
import com.example.fitnutrijournal.databinding.FragmentMealDetailBinding
import com.example.fitnutrijournal.ui.diet.MealWithFoodAdapter
import com.example.fitnutrijournal.ui.main.MainActivity
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.example.fitnutrijournal.viewmodel.DietViewModelFactory
import com.example.fitnutrijournal.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

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
        val adapter = MealWithFoodAdapter(
            emptyList(),
            { mealWithFood ->
                dietViewModel.selectFood(mealWithFood.food.foodCd)
                dietViewModel.setSaveButtonVisibility(false)
                dietViewModel.setUpdateButtonVisibility(true)
                findNavController().navigate(R.id.action_mealDetailFragment_to_foodDetailFragment)
            },
            dietViewModel
        )
        recyclerView.adapter = adapter

        setupItemTouchHelper(recyclerView, adapter)

        binding.btnAddFood.setOnClickListener {
            val source = homeViewModel.mealType.value ?: "breakfast"
            val action =
                MealDetailFragmentDirections.actionMealDetailFragmentToNavigationDiet(source)
            findNavController().navigate(action)
        }

        homeViewModel.mealType.observe(viewLifecycleOwner) { mealType ->
            dietViewModel.setMealType(mealType)
            homeViewModel.filterFoodsByMealType(mealType)
            val mealText = when (mealType) {
                "breakfast" -> {
                    homeViewModel.currentCaloriesBreakfast.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = "$calories kcal\n총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeBreakfast.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = "$carb g\n탄수화물"
                    }
                    homeViewModel.currentProteinIntakeBreakfast.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = "$protein g\n단백질"
                    }
                    homeViewModel.currentFatIntakeBreakfast.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = "$fat g\n지방"
                    }
                    "아침"
                }
                "lunch" -> {
                    homeViewModel.currentCaloriesLunch.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = "$calories kcal\n총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeLunch.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = "$carb g\n탄수화물"
                    }
                    homeViewModel.currentProteinIntakeLunch.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = "$protein g\n단백질"
                    }
                    homeViewModel.currentFatIntakeLunch.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = "$fat g\n지방"
                    }
                    "점심"
                }
                "dinner" -> {
                    homeViewModel.currentCaloriesDinner.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = "$calories kcal\n총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeDinner.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = "$carb g\n탄수화물"
                    }
                    homeViewModel.currentProteinIntakeDinner.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = "$protein g\n단백질"
                    }
                    homeViewModel.currentFatIntakeDinner.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = "$fat g\n지방"
                    }
                    "저녁"
                }
                "snack" -> {
                    homeViewModel.currentCaloriesSnack.observe(viewLifecycleOwner) { calories ->
                        binding.calories.text = "$calories kcal\n총 섭취량"
                    }
                    homeViewModel.currentCarbIntakeSnack.observe(viewLifecycleOwner) { carb ->
                        binding.carb.text = "$carb g\n탄수화물"
                    }
                    homeViewModel.currentProteinIntakeSnack.observe(viewLifecycleOwner) { protein ->
                        binding.protein.text = "$protein g\n단백질"
                    }
                    homeViewModel.currentFatIntakeSnack.observe(viewLifecycleOwner) { fat ->
                        binding.fat.text = "$fat g\n지방"
                    }
                    "간식"
                }
                else -> "식사"
            }
            binding.mealType.text = mealText
        }

        homeViewModel.filteredFoods.observe(viewLifecycleOwner) { foods ->
            viewLifecycleOwner.lifecycleScope.launch {
                val uniqueMeals = mutableListOf<MealWithFood>()
                val date = homeViewModel.currentDate.value ?: LocalDate.now().format(homeViewModel.dateFormatter)
                val mealType = homeViewModel.mealType.value ?: "breakfast"
                val meals = homeViewModel.mealRepository.getMealsByDateAndTypeSync(date, mealType) // 필터링된 mealType으로 가져오기

                meals.forEach { meal ->
                    val food = foods.find { it.foodCd == meal.dietFoodCode }
                    if (food != null) {
                        uniqueMeals.add(MealWithFood(meal = meal, food = food))
                    }
                }

                Log.d("MealDetailFragment", "Updating Adapter with Meals: $uniqueMeals")
                adapter.updateMealsWithFood(uniqueMeals)
            }
        }


        dietViewModel.loadMealsWithFood()
    }

    private fun setupItemTouchHelper(recyclerView: RecyclerView, adapter: MealWithFoodAdapter) {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

                private val background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.delete_red))
                private val deleteIcon: Drawable? = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
                private val iconMargin = resources.getDimension(R.dimen.icon_margin).toInt()

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val removedItem = adapter.removeItemById(adapter.getItem(position).meal.id)
                    if (removedItem != null) {
                        dietViewModel.deleteMealById(removedItem.meal.id)
                        Toast.makeText(
                            requireContext(),
                            "${removedItem.food.foodName}이 삭제 되었습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val iconTop = itemView.top + (itemView.height - deleteIcon!!.intrinsicHeight) / 2
                    val iconMargin = iconMargin
                    val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                    val iconRight = itemView.right - iconMargin
                    val iconBottom = iconTop + deleteIcon.intrinsicHeight

                    if (dX < 0) { // Swiping to the left
                        background.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    } else { // view is unswiped
                        background.setBounds(0, 0, 0, 0)
                        deleteIcon.setBounds(0, 0, 0, 0)
                    }

                    background.draw(c)
                    c.save()

                    if (dX < 0) {
                        c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    } else {
                        c.clipRect(0, 0, 0, 0)
                    }

                    deleteIcon.draw(c)
                    c.restore()
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.refreshFilteredFoods()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
