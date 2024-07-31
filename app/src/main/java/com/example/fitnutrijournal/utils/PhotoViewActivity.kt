package com.example.fitnutrijournal.utils

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.ActivityPhotoViewBinding

class PhotoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoUri = intent.getStringExtra(EXTRA_PHOTO_URI)
        if (photoUri != null) {
            Glide.with(this)
                .load(photoUri)
                .into(binding.imageView)
        } else {
            binding.imageView.setImageResource(R.drawable.image_sample)
        }

        binding.btnClose.setOnClickListener {
            finish()
        }
    }


    companion object {
        const val EXTRA_PHOTO_URI = "com.example.fitnutrijournal.ui.photo.PHOTO_URI"
    }
}