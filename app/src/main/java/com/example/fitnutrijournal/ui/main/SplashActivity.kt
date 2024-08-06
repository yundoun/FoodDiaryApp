package com.example.fitnutrijournal.ui.main


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 애니메이션 로드 및 적용
        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.logoImageView.startAnimation(fadeIn)

        // 일정 시간 후 메인 액티비티로 전환
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000) // 3초 대기
    }
}