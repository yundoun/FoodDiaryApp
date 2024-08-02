package com.example.fitnutrijournal.utils

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData

@BindingAdapter("textOrGone")
fun bindTextOrGone(view: TextView, text: LiveData<String>?) {
    if (text != null && !text.value.isNullOrEmpty()) {
        view.text = text.value
        view.visibility = View.VISIBLE
    } else {
        view.visibility = View.GONE
    }
}
