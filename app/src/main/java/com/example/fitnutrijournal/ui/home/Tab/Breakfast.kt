package com.example.fitnutrijournal.ui.home.Tab

import android.graphics.PorterDuff
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.FragmentBreakfastBinding
import com.example.fitnutrijournal.utils.NutritionProgressView
import com.example.fitnutrijournal.viewmodel.HomeViewModel

@RequiresApi(Build.VERSION_CODES.O)
class Breakfast : Fragment() {

    private var _binding: FragmentBreakfastBinding? = null
    private val binding get() = _binding!!
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBreakfastBinding.inflate(inflater, container, false).apply {
            viewModel = homeViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        // LifecycleOwner 설정
        binding.nutritionProgressView.setLifecycleOwner(viewLifecycleOwner)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}