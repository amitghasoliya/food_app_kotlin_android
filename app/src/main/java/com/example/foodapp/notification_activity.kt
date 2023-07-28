package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.databinding.ActivityNotificationBinding

class notification_activity : AppCompatActivity() {
    private lateinit var binding : ActivityNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetController?.isAppearanceLightStatusBars = true

        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val notification = listOf("Your order has been Cancelled Successfully","Order has taken by driver", "Congrats order has been placed")
        val adapter = notificationAdapter(ArrayList(notification))
        binding.notificationRecycler.adapter = adapter
        binding.notificationRecycler.layoutManager = LinearLayoutManager(this)
    }
}