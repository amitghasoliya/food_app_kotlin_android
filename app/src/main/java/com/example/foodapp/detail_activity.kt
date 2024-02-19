package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.example.foodapp.databinding.ActivityDetailBinding

class detail_activity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        val name = intent.getStringExtra("name")
        val price = intent.getStringExtra("price")
        val image = intent.getIntExtra("image",0)

        binding.detailImage.setImageResource(image)
        binding.detailFoodName.text = name
        binding.detailPrice.text = price
    }
}