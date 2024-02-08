package com.example.foodapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.foodapp.databinding.ActivityAdminSignupBinding
import com.example.foodapp.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AdminSignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdminSignupBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var username: String
    private lateinit var nameOfRestaurant: String
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.Primary)

        super.onCreate(savedInstanceState)
        binding = ActivityAdminSignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)

        binding.googleAdmin.setOnClickListener {
            val signInClient = googleSignInClient.signInIntent
            launcher.launch(signInClient)
        }

        binding.signupAdmin.setOnClickListener {
            username = binding.nameAdmin.text.toString().trim()
            nameOfRestaurant = binding.nameOfRestaurantAdmin.text.toString().trim()
            email = binding.emailAdmin.text.toString().trim()
            password = binding.passwordAdmin.text.toString().trim()

            if (username.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()){
                Toast.makeText(this, "Please fill details", Toast.LENGTH_SHORT).show()
            }
            else{
                createAccount(email,password)
            }

        }

        binding.goToLoginAdmin.setOnClickListener {
            finish()
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{ task ->
            if (task.isSuccessful){
                Toast.makeText(this@AdminSignupActivity, "Account Created", Toast.LENGTH_SHORT).show()
                saveUserData()
                val intent = Intent(this,AdminMainActivity::class.java)
                startActivity(intent)
                SharedPref.initialize(this)
                SharedPref.setUserType("Admin")
                finish()
            }
            else{
                Toast.makeText(this@AdminSignupActivity, "Account Creation Failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode== Activity.RESULT_OK){
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            if (task.isSuccessful){
                val account: GoogleSignInAccount?=task.result
                val credential = GoogleAuthProvider.getCredential(account?.idToken,null)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful){
                        Toast.makeText(this,"Successful",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this,AdminMainActivity::class.java))
                        SharedPref.initialize(this)
                        SharedPref.setUserType("Admin")
                        finish()
                    }else{
                        Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        else{
            Toast.makeText(this,"Failed",Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveUserData() {
        username = binding.nameAdmin.text.toString().trim()
        nameOfRestaurant = binding.nameOfRestaurantAdmin.text.toString().trim()
        email = binding.emailAdmin.text.toString().trim()
        password = binding.passwordAdmin.text.toString().trim()
        val user= UserModel(username,nameOfRestaurant,email,password)
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("admin").child(userId).setValue(user)
    }
}