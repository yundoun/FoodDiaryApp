package com.example.fitnutrijournal.utils

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.ModalBottomSheetContentBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ModalBottomSheet(private val listener: OnMealTypeSelectedListener) : BottomSheetDialogFragment() {

    private var _binding: ModalBottomSheetContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ModalBottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addBreakfast.setOnClickListener {
            listener.onMealTypeSelected("breakfast")
            dismiss()
        }

        binding.addLunch.setOnClickListener {
            listener.onMealTypeSelected("lunch")
            dismiss()
        }

        binding.addDinner.setOnClickListener {
            listener.onMealTypeSelected("dinner")
            dismiss()
        }

        binding.addSnack.setOnClickListener {
            listener.onMealTypeSelected("snack")
            dismiss()
        }
    }

    interface OnMealTypeSelectedListener {
        fun onMealTypeSelected(mealType: String)
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}

