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
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class MemoViewModel(application: Application) : AndroidViewModel(application) {

    private val memoRepository: MemoRepository
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _clickedDate = MutableLiveData<String>()
    val clickedDate: LiveData<String> get() = _clickedDate

    private val _clickedDateMemo = MutableLiveData<Memo?>()
    val clickedDateMemo: LiveData<Memo?> get() = _clickedDateMemo

    private val _monthlyMemos = MutableLiveData<Map<LocalDate, Memo>>()
    val monthlyMemos: LiveData<Map<LocalDate, Memo>> get() = _monthlyMemos

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

    // 특정 월에 속한 모든 날짜의 메모 로드
    fun loadMemosForMonth(month: YearMonth) {
        viewModelScope.launch {
            val startDate = month.atDay(1)
            val endDate = month.atEndOfMonth()
            val memos = memoRepository.getMemosBetweenDates(startDate.toString(), endDate.toString())
            _monthlyMemos.value = memos.associateBy { LocalDate.parse(it.date, dateFormatter) }
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
