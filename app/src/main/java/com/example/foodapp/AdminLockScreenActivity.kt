package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.foodapp.databinding.ActivityAdminLockScreenBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.lang.ref.Reference

class AdminLockScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminLockScreenBinding
    private lateinit var databaseRef: DatabaseReference
    var passkey : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminLockScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.verifyPasskeyButton.setOnClickListener {
            if (passkey == binding.passkey.text.toString()){
                val intent = Intent(this, AdminLoginActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this, "Please enter correct key", Toast.LENGTH_SHORT).show()
            }
        }

        databaseRef = FirebaseDatabase.getInstance().reference
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    passkey = snapshot.child("Passkey").getValue(String::class.java)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }
}