package com.example.fitnutrijournal.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import com.example.fitnutrijournal.databinding.NutritionProgressViewBinding

class NutritionProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    val binding: NutritionProgressViewBinding =
        NutritionProgressViewBinding.inflate(LayoutInflater.from(context), this, true)

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        binding.lifecycleOwner = lifecycleOwner
    }
}