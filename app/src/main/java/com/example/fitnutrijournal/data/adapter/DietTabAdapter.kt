package com.example.fitnutrijournal.data.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.databinding.ItemDietBinding
import com.example.fitnutrijournal.viewmodel.DietViewModel
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class DietTabAdapter(
    private val toggleFavorite: (Food) -> Unit,
    private val favorites: LiveData<Set<String>>,
    private val onItemClick: (Food) -> Unit,
    private val onItemLongClick: ((Food) -> Unit)? = null,
    private val viewModel: DietViewModel
) : ListAdapter<Food, DietTabAdapter.DietViewHolder>(DietDiffCallback()),
    FastScrollRecyclerView.SectionedAdapter {

    class DietDiffCallback : DiffUtil.ItemCallback<Food>() {
        override fun areItemsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem.foodCd == newItem.foodCd
        }

        override fun areContentsTheSame(oldItem: Food, newItem: Food): Boolean {
            return oldItem == newItem
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateDiets(newDiets: List<Food>) {
        submitList(newDiets)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateCheckedItems() {
        notifyDataSetChanged()
    }

    inner class DietViewHolder(private val binding: ItemDietBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: Food) {
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DietViewHolder {
        val binding = ItemDietBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DietViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DietViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    private fun updateFavoriteButton(button: ImageButton, foodCode: String) {
        val isFavorite = favorites.value?.contains(foodCode) ?: false
        button.setImageResource(
            if (isFavorite) R.drawable.ic_star
            else R.drawable.ic_star_border
        )
    }

    override fun getSectionName(position: Int): String {
        return getItem(position).initial.toString()
    }
}
