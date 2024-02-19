package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat

class Splash_Screen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this@Splash_Screen,R.color.white)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        SharedPref.initialize(this)
        val userType = SharedPref.getUserType()

        Handler(Looper.getMainLooper()).postDelayed({
            if (userType=="User"){
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                val intent = Intent(this, AdminLoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        },1000)
    }
}