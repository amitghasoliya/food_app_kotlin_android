package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.example.foodapp.databinding.ActivityChooseLocationBinding
import com.example.foodapp.model.Users
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ChooseLocationActivity : AppCompatActivity() {

    val binding : ActivityChooseLocationBinding by lazy {
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val locationList = arrayOf("Gurugram", "Delhi", "Jhajjar", "Faridabad")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList)
        binding.spinner.setAdapter(adapter)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (parent != null){
                    binding.next.setOnClickListener {
                        val city = parent.selectedItem.toString()
                        SharedPref.initialize(this@ChooseLocationActivity)
                        SharedPref.setUserLocation(city)
                        val intent = Intent(this@ChooseLocationActivity,MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

    }
}