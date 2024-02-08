package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.foodapp.databinding.ActivityAdminProfileBinding
import com.example.foodapp.model.UserModel
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
        super.onCreate(savedInstanceState)
        binding = ActivityAdminProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminRef = database.reference.child("admin")

        binding.saveAdmin.setOnClickListener {
            updateAdminData()
        }

        binding.signoutAdmin.setOnClickListener {
            SharedPref.initialize(this)
            SharedPref.setUserType("User")
            auth.signOut()
            startActivity(Intent(this, AdminLoginActivity::class.java))
            finishAffinity()
        }
        retrieveAdminData()
    }

    private fun updateAdminData() {
        var updateName = binding.nameAdminProfile.text.toString()
        var updatePassword = binding.passwordAdminProfile.text.toString()
        var updateEmail = binding.emailAdminProfile.text.toString()
        var updatePhone = binding.phoneAdminProfile.text.toString()
        var updateAddress = binding.addressAdminProfile.text.toString()

        val currentUser = auth.currentUser?.uid
        if (currentUser != null){
            val userRef = adminRef.child(currentUser)
            userRef.child("name").setValue(updateName)
            userRef.child("email").setValue(updateEmail)
            userRef.child("password").setValue(updatePassword)
            userRef.child("phone").setValue(updatePhone)
            userRef.child("address").setValue(updateAddress)

            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            auth.currentUser?.updateEmail(updateEmail)
            auth.currentUser?.updatePassword(updatePassword)
        }

    }

    private fun retrieveAdminData() {
        val currentUser = auth.currentUser?.uid
        if (currentUser != null){
            val userRef = adminRef.child(currentUser)
            userRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        var ownerName = snapshot.child("name").getValue()
                        var email = snapshot.child("email").getValue()
                        var password = snapshot.child("password").getValue()
                        var address = snapshot.child("address").getValue()
                        var phone = snapshot.child("phone").getValue()
                        setData(ownerName,email,password,address,phone)
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
        email: Any?,
        password: Any?,
        address: Any?,
        phone: Any?
    ) {
        binding.nameAdminProfile.setText(ownerName.toString())
        binding.passwordAdminProfile.setText(password.toString())
        binding.emailAdminProfile.setText(email.toString())
        binding.phoneAdminProfile.setText(phone.toString())
        binding.addressAdminProfile.setText(address.toString())
    }
}