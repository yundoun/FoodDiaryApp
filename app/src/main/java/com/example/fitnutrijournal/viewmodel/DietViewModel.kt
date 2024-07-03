package com.example.fitnutrijournal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DietViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is diet fragment"
    }
    val text: LiveData<String> = _text
}