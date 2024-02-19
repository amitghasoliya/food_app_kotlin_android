package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.foodapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var database: DatabaseReference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference
        val location = SharedPref.getUserLocation()
        binding.location.text = location

        var NavController = findNavController(R.id.fragmentContainerView)
        var bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setupWithNavController(NavController)

        binding.notificationsButton.setOnClickListener {
            startActivity(Intent(this,notification_activity::class.java))
        }
    }

    private fun setUsername() {
        val userRef = database.child("Users").child(userId).child("name")
        userRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val userProfile = snapshot.getValue(String::class.java)
                    val firstName = userProfile?.substringBefore(" ")
                    if (userProfile != null && userProfile.isNotEmpty()){
                        binding.username.setText("Hey ${firstName} !")
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    override fun onStart() {
        super.onStart()
        SharedPref.initialize(this)
        val userLocation = SharedPref.getUserLocation()
        if (auth.currentUser != null && userLocation=="null"){
            startActivity(Intent(this,ChooseLocationActivity::class.java))
        }
        setUsername()
    }
}