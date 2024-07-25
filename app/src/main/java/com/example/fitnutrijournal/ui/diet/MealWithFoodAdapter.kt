package com.example.fitnutrijournal.ui.diet

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.data.model.MealWithFood
import com.example.fitnutrijournal.databinding.ItemDietBinding
import com.example.fitnutrijournal.viewmodel.DietViewModel

class MealWithFoodAdapter(
    private var mealsWithFood: List<MealWithFood>,
    private val onItemClick: (MealWithFood) -> Unit,
    private val viewModel: DietViewModel
) : RecyclerView.Adapter<MealWithFoodAdapter.MealViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateMealsWithFood(newMealsWithFood: List<MealWithFood>) {
        mealsWithFood = newMealsWithFood
        notifyDataSetChanged()
    }

    inner class MealViewHolder(private val binding: ItemDietBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: MealWithFood) {
            val food = item.food
            binding.foodName.text = food.foodName
            binding.foodTotalContent.text = "${item.meal.quantity} g"
            binding.foodCalories.text = String.format("%.2f kcal", item.meal.quantity * food.caloriesPerGram)
            binding.root.setOnClickListener {
                viewModel.setSelectedMealQuantity((item.meal.quantity).toInt())
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemDietBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val item = mealsWithFood[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = mealsWithFood.size

    fun removeItemById(id: Long): MealWithFood? {
        val index = mealsWithFood.indexOfFirst { it.meal.id == id }
        return if (index != -1) {
            val mutableList = mealsWithFood.toMutableList()
            val removedItem = mutableList.removeAt(index)
            mealsWithFood = mutableList
            notifyItemRemoved(index)
            removedItem
        } else {
            null
        }
    }

    fun getItem(position: Int): MealWithFood {
        return mealsWithFood[position]
    }
}
