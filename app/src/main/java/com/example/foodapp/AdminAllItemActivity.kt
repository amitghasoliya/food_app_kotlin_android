package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.databinding.ActivityAdminAllItemBinding
import com.example.foodapp.model.AllMenu
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminAllItemActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminAllItemBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems : ArrayList<AllMenu> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminAllItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference
        retreiveMenuItem()
        
    }

    private fun retreiveMenuItem() {
        database = FirebaseDatabase.getInstance()
        val foodRef = databaseReference.child("menu")

        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                for(foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(AllMenu::class.java)
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
        val adapter = AdminMenuItemAdapter(this, menuItems, databaseReference){ position ->
            deleteMenuItems(position)
        }
        binding.recyclerAllItemAdmin.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerAllItemAdmin.adapter = adapter
    }

    private fun deleteMenuItems(position: Int) {
        val menuItemToDelete =  menuItems[position]
        val menuItemKey = menuItemToDelete.key
        val foodMenuRef = database.reference.child("menu").child(menuItemKey!!)
        foodMenuRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful){
                menuItems.removeAt(position)
                binding.recyclerAllItemAdmin.adapter?.notifyItemRemoved(position)
            }else
                Toast.makeText(this, "Not deleted", Toast.LENGTH_SHORT).show()
        }
    }
}