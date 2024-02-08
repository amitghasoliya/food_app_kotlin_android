package com.example.foodapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.example.foodapp.databinding.ActivityAdminAddItemBinding
import com.example.foodapp.databinding.ActivityAdminMainBinding
import com.example.foodapp.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AdminAddItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminAddItemBinding
    private lateinit var foodName : String
    private lateinit var foodPrice : String
    private var foodImage : Uri? = null

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        binding.selectImageAdmin.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.addItemButtonAdmin.setOnClickListener {
            foodName = binding.addFoodNameAdmin.text.toString().trim()
            foodPrice = binding.addFoodPriceAdmin.text.toString().trim()
            if (!foodName.isBlank() || foodPrice.isBlank()){
                uploadData()
                Toast.makeText(this, "Item Add Successfully", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, "Fill all details ", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadData() {
        val menuRef = database.getReference("menu")
        val newItemKey = menuRef.push().key

        if (foodImage != null){
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("menu_image/${newItemKey}.jpg")
            val uploadTask = imageRef.putFile(foodImage!!)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener {
                    downloadUrl ->
                    val newItem = AllMenu(newItemKey,foodName, foodPrice, downloadUrl.toString())
                    newItemKey?.let {
                        key ->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this, "Data uploaded", Toast.LENGTH_SHORT).show()
                        }
                            .addOnFailureListener {
                                Toast.makeText(this, "Data upload failed", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Image upload failed", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }
    val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()){ uri ->
        if (uri != null){
            binding.addFoodImageAdmin.setImageURI(uri)
            foodImage = uri
        }

    }
}