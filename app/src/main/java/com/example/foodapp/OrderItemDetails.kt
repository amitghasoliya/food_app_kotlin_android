package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foodapp.databinding.ActivityOrderItemDetailsBinding

class OrderItemDetails : AppCompatActivity() {
    private lateinit var binding: ActivityOrderItemDetailsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderItemDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}