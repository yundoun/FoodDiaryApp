package com.example.fitnutrijournal.ui.main

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.fitnutrijournal.R
import com.example.fitnutrijournal.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        navView.setupWithNavController(navController)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment = navController.currentDestination?.id
        val previousBackStackEntry = navController.previousBackStackEntry

        if (currentFragment == R.id.navigation_home && previousBackStackEntry == null) {
            // HomeFragment에서 뒤로가기 버튼을 눌렀을 때 백스택이 비어 있는 경우
            val builder = AlertDialog.Builder(this)
            val messageView = View.inflate(this, R.layout.dialog_message, null)
            val messageTextView = messageView.findViewById<TextView>(R.id.dialog_message_text)
            messageTextView.text = "앱을 종료하시겠습니까?"
            messageTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black))

            builder.setView(messageView)
                .setPositiveButton("예") { dialog, which ->
                    super.onBackPressed()
                }
                .setNegativeButton("아니오", null)
                .show()
        } else {
            // 다른 Fragment에서 뒤로가기 버튼을 눌렀을 때 또는 백스택에 다른 항목이 남아 있는 경우
            if (!navController.popBackStack()) {
                super.onBackPressed()
            }
        }
    }

    fun showBottomNavigation(show: Boolean) {
        binding.navView.visibility = if (show) View.VISIBLE else View.GONE
    }
}
