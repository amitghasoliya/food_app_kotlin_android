package com.example.foodapp

import android.R
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.foodapp.databinding.ActivityAdminChooseLocationBinding
import com.example.foodapp.databinding.ActivityChooseLocationBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdminChooseLocationActivity : AppCompatActivity() {

    val binding : ActivityAdminChooseLocationBinding by lazy {
        ActivityAdminChooseLocationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val locationList = arrayOf("Gurugram", "Delhi", "Jhajjar", "Faridabad")
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, locationList)
        binding.spinner.setAdapter(adapter)

        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (parent != null){
                    binding.next.setOnClickListener {
                        val city = parent.selectedItem.toString()
                        SharedPref.initialize(this@AdminChooseLocationActivity)
                        SharedPref.setUserLocation(city)
                        val intent = Intent(this@AdminChooseLocationActivity,AdminMainActivity::class.java)
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