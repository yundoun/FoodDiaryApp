package com.example.fitnutrijournal.ui.Activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
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

    private val permissions = arrayOf(
        Manifest.permission.INTERNET,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.CAMERA
    )

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            logPermissionsStatus(permissions)
            if (permissions.values.all { it }) {
                onPermissionsGranted()
            } else {
                showPermissionsDeniedDialog()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        checkPermissions()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val currentFragment = navController.currentDestination?.id
        val previousBackStackEntry = navController.previousBackStackEntry

        if (currentFragment == R.id.navigation_home && previousBackStackEntry == null) {
            val builder = AlertDialog.Builder(this)
            val messageView = View.inflate(this, R.layout.dialog_message, null)
            val messageTextView = messageView.findViewById<TextView>(R.id.dialog_message_text)
            messageTextView.text = "앱을 종료하시겠습니까?"
            messageTextView.setTextColor(ContextCompat.getColor(this, android.R.color.black))

            builder.setView(messageView)
                .setPositiveButton("예") { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton("아니오", null)
                .show()
        } else {
            if (!navController.popBackStack()) {
                super.onBackPressed()
            }
        }
    }

    fun showBottomNavigation(show: Boolean) {
        binding.navView.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun checkPermissions() {
        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionsLauncher.launch(permissionsToRequest)
        } else {
            onPermissionsGranted()
        }
    }

    private fun showPermissionsDeniedDialog() {
        AlertDialog.Builder(this)
            .setTitle("권한 거부됨")
            .setMessage("필요한 권한이 승인되지 않았습니다. 앱을 종료합니다.")
            .setPositiveButton("확인") { _, _ ->
                finish()
            }
            .show()
    }

    private fun logPermissionsStatus(permissions: Map<String, Boolean>) {
        permissions.forEach { (permission, isGranted) ->
            Log.d("PermissionsStatus", "Permission: $permission, Granted: $isGranted")
        }
    }

    private fun onPermissionsGranted() {
        // 필요한 권한이 모두 허용된 경우 실행할 작업
        Log.d("PermissionsStatus", "All required permissions granted")
        // 추가로 필요한 초기화 작업이 있다면 여기서 수행
    }
}
