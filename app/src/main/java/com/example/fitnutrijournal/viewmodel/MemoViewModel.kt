package com.example.fitnutrijournal.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.example.fitnutrijournal.data.database.FoodDatabase
import com.example.fitnutrijournal.data.model.Memo
import com.example.fitnutrijournal.data.repository.MemoRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class MemoViewModel(application: Application) : AndroidViewModel(application) {

    private val memoRepository: MemoRepository
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _clickedDate = MutableLiveData<String>()
    val clickedDate: LiveData<String> get() = _clickedDate

    private val _clickedDateMemo = MutableLiveData<Memo?>()
    val clickedDateMemo: LiveData<Memo?> get() = _clickedDateMemo

    init {
        val memoDao = FoodDatabase.getDatabase(application).memoDao()
        memoRepository = MemoRepository(memoDao)
        updateClickedDate(LocalDate.now())
    }

    fun updateClickedDate(date: LocalDate) {
        val newDate = date.format(dateFormatter)
        _clickedDate.value = newDate
        loadMemoByDate(newDate)
    }

    fun loadMemoByDate(date: String) {
        viewModelScope.launch {
            _clickedDateMemo.value = memoRepository.getMemoByDate(date)
        }
    }

    fun insertOrUpdate(memo: Memo) = viewModelScope.launch {
        memoRepository.insertOrUpdate(memo)
    }

    fun insert(memo: Memo) = viewModelScope.launch {
        memoRepository.insert(memo)
    }

    fun update(memo: Memo) = viewModelScope.launch {
        memoRepository.update(memo)
    }

    fun deleteByDate(date: String) = viewModelScope.launch {
        memoRepository.deleteByDate(date)
    }
}
