package com.example.fitnutrijournal.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ExerciseViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is exercise fragment"
    }
    val text: LiveData<String> = _text
}