package com.example.fitnutrijournal.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@BindingAdapter("text")
fun setEditText(editText: EditText, value: LiveData<String>?) {
    if (value == null || value.value == editText.text.toString()) {
        return
    }
    editText.setText(value.value)
}

@InverseBindingAdapter(attribute = "text", event = "textAttrChanged")
fun getEditTextString(editText: EditText): String {
    return editText.text.toString()
}

@BindingAdapter("textAttrChanged")
fun setEditTextListener(editText: EditText, listener: InverseBindingListener?) {
    if (listener != null) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                listener.onChange()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}
