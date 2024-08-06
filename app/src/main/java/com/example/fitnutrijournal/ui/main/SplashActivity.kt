package com.example.fitnutrijournal.ui.main


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.data.database.FoodDatabase
import com.example.fitnutrijournal.data.repository.FoodApiRepository
import com.example.fitnutrijournal.data.repository.FoodRepository
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

        // 데이터 로드
        loadData()

        // 일정 시간 후 메인 액티비티로 전환
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 3000) // 3초 대기
    }

    private fun loadData() {
        val foodDao = FoodDatabase.getDatabase(applicationContext).foodDao()
        val foodRepository = FoodRepository(foodDao)
        val foodApiRepository = FoodApiRepository(foodRepository)

        // 로컬 데이터베이스에서 데이터 확인
        foodRepository.allFoods.observe(this, Observer { foods ->
            if (foods.isNullOrEmpty()) {
                foodApiRepository.fetchFoodInfo().observe(this, Observer { foodResponse ->
                    foodResponse?.i2790?.rows?.forEach { item ->
                        Log.d("FoodInfo", "호출 성공")
                    }
                })
            } else {
                Log.d("FoodInfo", "로컬 데이터베이스에 데이터가 존재합니다.")
            }
        })
    }
}