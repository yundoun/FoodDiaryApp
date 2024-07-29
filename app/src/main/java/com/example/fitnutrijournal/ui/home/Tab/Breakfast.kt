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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }


    private fun setProgressbarColor() {
        setupCombinedIntakeObserver(
            homeViewModel.currentCarbIntake,
            homeViewModel.targetCarbIntake,
            binding.nutritionProgressView
        )
    }

    private fun setupCombinedIntakeObserver(
        currentIntakeLiveData: LiveData<Int>,
        targetIntakeLiveData: LiveData<Int>,
        progressBar: NutritionProgressView
    ) {
        val combinedIntake = MediatorLiveData<Pair<Int, Int>>().apply {
            var currentIntakeValue = currentIntakeLiveData.value ?: 0
            var targetIntakeValue = targetIntakeLiveData.value ?: 0

            addSource(currentIntakeLiveData) { currentIntake ->
                currentIntakeValue = currentIntake
                value = currentIntakeValue to targetIntakeValue
            }

            addSource(targetIntakeLiveData) { targetIntake ->
                targetIntakeValue = targetIntake
                value = currentIntakeValue to targetIntakeValue
            }
        }

        combinedIntake.observe(viewLifecycleOwner, Observer { (currentIntake, targetIntake) ->
            Log.d("progressbar", "Intake: $currentIntake Target: $targetIntake")
            updateProgressBarColor(progressBar, currentIntake, targetIntake)
        })
    }

    private fun updateProgressBarColor(progressBar: NutritionProgressView, currentIntake: Int, targetIntake: Int) {
        val progressDrawable = progressBar.progressDrawable.mutate() as LayerDrawable
        val progressLayer = progressDrawable.findDrawableByLayerId(android.R.id.progress) as ClipDrawable
        val colorResId = if (currentIntake > targetIntake) {
            R.color.progressbar_red
        } else {
            R.color.progressbar_green
        }
        progressLayer.setColorFilter(ContextCompat.getColor(requireContext(), colorResId), PorterDuff.Mode.SRC_IN)
        progressBar.progressDrawable = progressDrawable
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}