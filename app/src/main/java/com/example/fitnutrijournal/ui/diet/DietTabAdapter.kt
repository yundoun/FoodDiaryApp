package com.example.fitnutrijournal.ui.diet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnutrijournal.R

class DietTabAdapter(
    private val items: List<String>,
    private val onFavoriteClicked: (String) -> Unit,
    private val favorites: Set<String>
) : RecyclerView.Adapter<DietTabAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view)
        val favoriteButton: ImageButton = view.findViewById(R.id.favorite_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diet, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item
        holder.favoriteButton.setImageResource(
            if (favorites.contains(item)) android.R.drawable.btn_star_big_on
            else android.R.drawable.btn_star_big_off
        )

        holder.favoriteButton.setOnClickListener {
            onFavoriteClicked(item)
        }
    }

    override fun getItemCount(): Int = items.size
}
