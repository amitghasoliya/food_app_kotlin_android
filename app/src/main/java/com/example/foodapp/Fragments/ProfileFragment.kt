package com.example.foodapp.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.foodapp.LoginActivity
import com.example.foodapp.MainActivity
import com.example.foodapp.R
import com.example.foodapp.SharedPref
import com.example.foodapp.model.Users
import com.example.foodapp.databinding.FragmentProfileBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    private lateinit var binding : FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val database= FirebaseDatabase.getInstance()
    private val userId = auth.currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater,container,false)

        binding.signout.setOnClickListener {
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
            GoogleSignIn.getClient(requireContext(),gso).signOut()
            FirebaseAuth.getInstance().signOut()
            SharedPref.initialize(requireContext())
            SharedPref.setUserLocation("null")
            SharedPref.logoutUser(requireContext())
            startActivity(Intent(requireContext(),LoginActivity::class.java))
            activity?.finishAffinity()
        }

        binding.save.setOnClickListener {
            val name = binding.name.text.toString()
            val address = binding.address.text.toString()
            val phone = binding.phone.text.toString()
            val email = auth.currentUser?.email.toString()
            updateUserData(name,address,email,phone)
        }

        setUserData()
        return binding.root
    }

    private fun updateUserData(name: String, address: String, email:String,phone: String) {
        if (userId != null){
            val userRef = database.getReference("Users").child(userId)
            userRef.child("name").setValue(name)
            userRef.child("email").setValue(email)
            userRef.child("address").setValue(address)
            userRef.child("phone").setValue(phone).addOnSuccessListener {
                Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                activity?.finishAffinity()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Profile updation failed", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setUserData() {
        binding.email.setText(auth.currentUser?.email.toString())
        if (userId != null){
            val userRef = database.getReference("Users").child(userId)
            userRef.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val userProfile = snapshot.getValue(Users::class.java)
                        if (userProfile != null){
                            binding.name.setText(userProfile.name)
                            binding.address.setText(userProfile.address)
                            binding.phone.setText(userProfile.phone)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }

}