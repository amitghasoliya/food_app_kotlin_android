package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.databinding.ActivityMenuBinding
import com.example.foodapp.model.MenuItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class menu : AppCompatActivity() {
    private lateinit var binding : ActivityMenuBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var menuItems : MutableList<MenuItems>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }
        retreiveMenuItems()
    }

    private fun retreiveMenuItems() {
        database = FirebaseDatabase.getInstance()
        val foodRef = FirebaseDatabase.getInstance().reference.child("menu")
        menuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItems::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setAdapter() {
        if (menuItems.isNotEmpty()){
            val adapter = MenuAdapter(menuItems,this)
            binding.menuRecycler.layoutManager = LinearLayoutManager(this)
            binding.menuRecycler.adapter = adapter
        }
    }
}