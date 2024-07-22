package com.example.fitnutrijournal.utils


import DietTabAdapter
import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.data.model.Food
import com.example.fitnutrijournal.viewmodel.DietViewModel

@SuppressLint("NotifyDataSetChanged")
fun setupRecyclerView(
    context: Context,
    recyclerView: RecyclerView,
    lifecycleOwner: LifecycleOwner,
    viewModel: DietViewModel,
    onItemClick: (Food) -> Unit
): DietTabAdapter {
    recyclerView.layoutManager = LinearLayoutManager(context)
    val adapter = DietTabAdapter(
        emptyList(),
        viewModel::toggleFavorite,
        viewModel.favorites,
        onItemClick,
        viewModel
    )
    recyclerView.adapter = adapter

    viewModel.filteredFoods.observe(lifecycleOwner) { foods ->
        adapter.updateDiets(foods)
    }

    viewModel.isCheckboxVisible.observe(lifecycleOwner) {
        adapter.notifyDataSetChanged()
    }

    viewModel.checkedItems.observe(lifecycleOwner) {
        adapter.notifyDataSetChanged()
    }

    return adapter
}