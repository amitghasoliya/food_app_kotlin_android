package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.foodapp.databinding.ActivityAdminProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminProfileActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightGrey)

        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminRef = database.reference.child("Admin")

        binding.saveAdmin.setOnClickListener {
            updateAdminData()
        }

        binding.apply {
            setSupportActionBar(adminProfileToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(null)
            adminProfileToolbar.setNavigationOnClickListener {
                finish()
            }
        }
        binding.signoutAdmin.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            GoogleSignIn.getClient(this,gso).signOut()
            SharedPref.initialize(this)
            SharedPref.setUserType("User")
            SharedPref.setRestName("Restaurant")
            auth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }
        retrieveAdminData()
    }

    private fun updateAdminData() {
        val updateName = binding.nameAdminProfile.text.toString()
        val updateRestName = binding.restaurantAdminProfile.text.toString()
        val updatePhone = binding.phoneAdminProfile.text.toString()
        val updateAddress = binding.addressAdminProfile.text.toString()

        val currentUser = auth.currentUser?.uid
        if (currentUser != null){
            val userRef = adminRef.child(currentUser)
            userRef.child("name").setValue(updateName)
            userRef.child("nameOfRestaurant").setValue(updateRestName)
            userRef.child("email").setValue(auth.currentUser?.email.toString())
            userRef.child("phone").setValue(updatePhone)
            userRef.child("address").setValue(updateAddress)
        }
    }

    private fun retrieveAdminData() {
        val currentUser = auth.currentUser?.uid
        if (currentUser != null){
            val userRef = adminRef.child(currentUser)
            userRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val ownerName = snapshot.child("name").getValue()
                        val restaurantName = snapshot.child("nameOfRestaurant").getValue()
                        SharedPref.initialize(this@AdminProfileActivity)
                        SharedPref.setRestName(restaurantName.toString())
                        val address = snapshot.child("address").getValue()
                        val phone = snapshot.child("phone").getValue()
                        setData(ownerName,restaurantName,address,phone)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

    private fun setData(
        ownerName: Any?,
        nameOfRestaurant: Any?,
        address: Any?,
        phone: Any?
    ) {
        binding.nameAdminProfile.setText(ownerName.toString())
        binding.emailAdminProfile.setText(auth.currentUser?.email.toString())
        binding.restaurantAdminProfile.setText(nameOfRestaurant.toString())
        binding.phoneAdminProfile.setText(phone.toString())
        binding.addressAdminProfile.setText(address.toString())
    }
}