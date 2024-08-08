package com.example.fitnutrijournal.data.adapter

import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.example.fitnutrijournal.R



@BindingAdapter("app:srcCompat")
fun setSrcCompat(view: ImageButton, isFavorite: LiveData<Boolean>?) {
    isFavorite?.observeForever { favorite ->
        val drawableId = if (favorite == true) {
            R.drawable.ic_star // 즐겨찾기 활성화 아이콘
        } else {
            R.drawable.ic_star_border // 즐겨찾기 비활성화 아이콘
        }
        view.setImageDrawable(ContextCompat.getDrawable(view.context, drawableId))
    }
}