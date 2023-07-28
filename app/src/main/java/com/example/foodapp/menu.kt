package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.databinding.ActivityMenuBinding

class menu : AppCompatActivity() {
    private lateinit var binding : ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetController?.isAppearanceLightStatusBars = true

        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }
        val menuFoodName = listOf("Burger","Sandwich","Momo","Chowmein","Pizza","Shake")
        val menuItemPrice = listOf("₹ 20","₹ 40","₹ 60","₹ 20","₹ 40","₹ 60")
        val menuImage = listOf(R.drawable.burger, R.drawable.sandwich,R.drawable.momos,R.drawable.chowmein, R.drawable.pizza,R.drawable.shake)
        val adapter = MenuAdapter(ArrayList(menuFoodName),ArrayList(menuItemPrice),ArrayList(menuImage), this)
        binding.menuRecycler.layoutManager = LinearLayoutManager(this)
        binding.menuRecycler.adapter = adapter
    }
}