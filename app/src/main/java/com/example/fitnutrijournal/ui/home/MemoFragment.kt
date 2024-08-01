package com.example.fitnutrijournal.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.fitnutrijournal.data.model.Memo
import com.example.fitnutrijournal.databinding.FragmentMemoBinding
import com.example.fitnutrijournal.viewmodel.MemoViewModel
import com.google.android.material.snackbar.Snackbar

@RequiresApi(Build.VERSION_CODES.O)
class MemoFragment : Fragment() {

    private var _binding: FragmentMemoBinding? = null
    private val binding get() = _binding!!
    private val memoViewModel: MemoViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMemoBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 처음에는 저장 버튼을 비활성화
        binding.saveBtn.isEnabled = false

        binding.tvDate.text = memoViewModel.clickedDate.value

        memoViewModel.clickedDateMemo.observe(viewLifecycleOwner) { memo ->
            binding.editTextDiary.setText(memo?.content ?: "")
        }

        binding.saveBtn.setOnClickListener {
            val content = binding.editTextDiary.text.toString()
            Log.d("DiaryFragment", "content: $content")
            val clickedDate = memoViewModel.clickedDate.value
            Log.d("DiaryFragment", "selectedDate: $clickedDate")
            if (clickedDate != null && content.isNotBlank()) {
                val memo = Memo(clickedDate, content)
                memoViewModel.insertOrUpdate(memo)
                Toast.makeText(requireContext(), "메모가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }

        // EditText에 TextWatcher 추가
        binding.editTextDiary.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                // EditText의 내용이 비어있는지 확인하고, 저장 버튼의 활성화 상태 업데이트
                binding.saveBtn.isEnabled = !s.isNullOrBlank()
            }
        })

        binding.backBtn.setOnClickListener{

            findNavController().popBackStack()
        }

        binding.clearMemoBtn.setOnClickListener {
            binding.editTextDiary.text.clear()
        }

        binding.deleteMemoBtn.setOnClickListener {
            val clickedDate = memoViewModel.clickedDate.value
            if (clickedDate != null) {
                memoViewModel.deleteByDate(clickedDate)
                Toast.makeText(requireContext(), "메모가 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            }
        }


        // ConstraintLayout에 터치 리스너 추가
        binding.root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
                binding.editTextDiary.clearFocus()
            }
            false
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}
