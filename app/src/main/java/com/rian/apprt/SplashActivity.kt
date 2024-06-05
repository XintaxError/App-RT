package com.rian.apprt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.rian.apprt.User.MainActivity
import com.rian.apprt.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var preferences: SharedPreferences

    companion object {
        private const val PREF_NAME = "akun"
        private const val LOGIN = "isLoggedIn"
        private const val TAG = "SplashActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        fillVersion()

        // Cek apakah pengguna sudah login
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isLoggedIn = preferences.getBoolean(LOGIN, false)

        // Redirect ke activity yang sesuai setelah delay
        Handler(Looper.getMainLooper()).postDelayed({
            val targetActivity = if (isLoggedIn) MainActivity::class.java else LandingPageActivity::class.java
            val intent = Intent(this@SplashActivity, targetActivity)
            startActivity(intent)
            finish()
        }, 5000)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Kosongkan untuk menonaktifkan tombol kembali selama splash screen
    }

    private fun fillVersion() {
        val appName = getString(R.string.app_name)
        binding.tvSplashAppTitle.text = appName

        try {
            val versionName = packageManager.getPackageInfo(packageName, 0).versionName
            val versionText = getString(R.string.splash_app_version, versionName)
            binding.tvSplashAppVersion.text = versionText
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, e.message ?: "")
        }
    }
}


