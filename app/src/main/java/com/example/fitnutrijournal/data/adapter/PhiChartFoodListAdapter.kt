package com.example.fitnutrijournal.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.data.model.MealWithFood
import com.example.fitnutrijournal.databinding.ItemPhiChartFoodBinding

class PhiChartFoodListAdapter : ListAdapter<MealWithFood, PhiChartFoodListAdapter.FoodViewHolder>(FoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemPhiChartFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    class FoodViewHolder(private val binding: ItemPhiChartFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(mealWithFood: MealWithFood) {
            binding.foodName.text = mealWithFood.food.foodName
            binding.foodQuantity.text = "${mealWithFood.meal.quantity}g"
            binding.foodCalories.text = String.format("%.2f kcal", mealWithFood.food.calories * mealWithFood.meal.quantity / mealWithFood.food.servingSize.toDouble())
        }
    }

    class FoodDiffCallback : DiffUtil.ItemCallback<MealWithFood>() {
        override fun areItemsTheSame(oldItem: MealWithFood, newItem: MealWithFood): Boolean {
            return oldItem.meal.id == newItem.meal.id
        }

        override fun areContentsTheSame(oldItem: MealWithFood, newItem: MealWithFood): Boolean {
            return oldItem == newItem
        }
    }
}
