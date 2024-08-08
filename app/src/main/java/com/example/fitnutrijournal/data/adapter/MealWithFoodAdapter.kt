package com.example.fitnutrijournal.data.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.data.model.MealWithFood
import com.example.fitnutrijournal.databinding.ItemDietBinding
import com.example.fitnutrijournal.databinding.ItemMealBinding
import com.example.fitnutrijournal.viewmodel.DietViewModel
import java.util.Collections

class MealWithFoodAdapter(
    private var mealsWithFood: List<MealWithFood>,
    private val onItemClick: (MealWithFood) -> Unit,
    private val viewModel: DietViewModel
) : RecyclerView.Adapter<MealWithFoodAdapter.MealViewHolder>() {

    init {
        setHasStableIds(true)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateMealsWithFood(newMealsWithFood: List<MealWithFood>) {
        mealsWithFood = newMealsWithFood
        notifyDataSetChanged()
    }

    inner class MealViewHolder(private val binding: ItemMealBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: MealWithFood) {
            val food = item.food
            val meal = item.meal
            Log.d("MealWithFoodAdapter", "Binding Meal ID: ${meal.id}, Quantity: ${meal.quantity}")
            binding.foodName.text = food.foodName
            binding.foodTotalContent.text = "${meal.quantity} g"
            binding.foodCalories.text = String.format("%.2f kcal", meal.quantity * food.caloriesPerGram)
            binding.root.setOnClickListener {
                viewModel.setSelectedMealQuantity(meal.quantity)
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        val item = mealsWithFood[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = mealsWithFood.size

    override fun getItemId(position: Int): Long { // 추가: 고유 ID 반환
        return mealsWithFood[position].meal.id
    }

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

    // 추가: 아이템 순서를 변경하는 메서드
    fun moveItem(fromPosition: Int, toPosition: Int) {
        val mutableList = mealsWithFood.toMutableList()
        Collections.swap(mutableList, fromPosition, toPosition)
        mealsWithFood = mutableList
        notifyItemMoved(fromPosition, toPosition)
    }
}
