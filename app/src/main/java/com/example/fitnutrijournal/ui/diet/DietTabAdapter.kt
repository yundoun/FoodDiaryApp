package com.example.fitnutrijournal.ui.diet

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.data.model.Meal
import com.example.fitnutrijournal.databinding.ItemDietBinding
import com.example.fitnutrijournal.viewmodel.DietViewModel

class DietTabAdapter(
    private var items: List<Any>,
    private val toggleFavorite: (Any) -> Unit,
    private val favorites: LiveData<Set<String>>,
    private val onItemClick: (Any) -> Unit,
    private val onItemLongClick: ((Any) -> Unit)? = null,
    private val viewModel: DietViewModel
) : RecyclerView.Adapter<DietTabAdapter.DietViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItems: List<Any>) {
        items = newItems
        notifyDataSetChanged() // This will notify the RecyclerView to refresh its items
    }

    inner class DietViewHolder(private val binding: ItemDietBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Any) {
            when (item) {
                is Food -> {
                    binding.foodName.text = item.foodName
                    binding.foodTotalContent.text = "${item.servingSize} g"
                    binding.foodCalories.text = "${item.calories} kcal"
                    updateFavoriteButton(binding.favoriteBtn, item.foodCd)

                    binding.favoriteBtn.setOnClickListener {
                        toggleFavorite(item)
                    }

                    binding.root.setOnClickListener {
                        onItemClick(item)
                    }

                    binding.root.setOnLongClickListener {
                        onItemLongClick?.invoke(item)
                        true
                    }

                    when (viewModel.isCheckboxVisible.value) {
                        true -> {
                            binding.favoriteBtn.visibility = View.GONE
                            binding.checkbox.visibility = View.VISIBLE
                        }
                        false -> {
                            binding.favoriteBtn.visibility = View.VISIBLE
                            binding.checkbox.visibility = View.GONE
                        }
                        else -> {
                            binding.favoriteBtn.visibility = View.GONE
                            binding.checkbox.visibility = View.GONE
                        }
                    }

                    binding.checkbox.setOnCheckedChangeListener(null)
                    binding.checkbox.isChecked = viewModel.checkedItems.value?.contains(item) == true
                    binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                        viewModel.toggleCheckedItem(item)
                    }
                }
                is Meal -> {
                    binding.foodName.text = item.dietFoodCode // 식품 코드를 표시하거나 적절한 방법으로 변경
                    binding.foodTotalContent.text = "${item.quantity} g"
                    // 칼로리 정보는 별도로 조회하여 설정
                    updateFavoriteButton(binding.favoriteBtn, item.dietFoodCode)

                    binding.favoriteBtn.setOnClickListener {
                        toggleFavorite(item)
                    }

                    binding.root.setOnClickListener {
                        onItemClick(item)
                    }

                    binding.root.setOnLongClickListener {
                        onItemLongClick?.invoke(item)
                        true
                    }

                    when (viewModel.isCheckboxVisible.value) {
                        true -> {
                            binding.favoriteBtn.visibility = View.GONE
                            binding.checkbox.visibility = View.VISIBLE
                        }
                        false -> {
                            binding.favoriteBtn.visibility = View.VISIBLE
                            binding.checkbox.visibility = View.GONE
                        }
                        else -> {
                            binding.favoriteBtn.visibility = View.GONE
                            binding.checkbox.visibility = View.GONE
                        }
                    }

                    binding.checkbox.setOnCheckedChangeListener(null)
                    binding.checkbox.isChecked = viewModel.checkedItems.value?.contains(item) == true
                    binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                        viewModel.toggleCheckedItem(item)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietViewHolder {
        val binding = ItemDietBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DietViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)

        favorites.observeForever {
            holder.bind(item)
        }
    }

    private fun updateFavoriteButton(button: ImageButton, foodCode: String) {
        val isFavorite = favorites.value?.contains(foodCode) ?: false
        button.setImageResource(
            if (isFavorite) R.drawable.ic_star
            else R.drawable.ic_star_border
        )
    }

    override fun getItemCount(): Int = items.size

    fun removeItemById(id: Long): Meal? {
        val index = items.indexOfFirst { it is Meal && it.id == id }
        return if (index != -1) {
            val removedItem = items.toMutableList().apply { removeAt(index) } as Meal
            notifyItemRemoved(index)
            removedItem
        } else {
            null
        }
    }

    fun getItem(position: Int): Any {
        return items[position]
    }
}
