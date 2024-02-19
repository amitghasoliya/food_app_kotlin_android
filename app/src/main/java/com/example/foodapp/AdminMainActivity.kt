package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.databinding.ActivityAdminMainBinding
import com.example.foodapp.model.AllMenu
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminMainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAdminMainBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var menuItems : ArrayList<AllMenu> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        SharedPref.initialize(this)

        binding.addItemAdmin.setOnClickListener{
            val intent = Intent(this,AdminAddItemActivity::class.java)
            startActivity(intent)
        }
        binding.pendingOrderAdmin.setOnClickListener {
            val intent = Intent(this,AdminPendingOrdersActivity::class.java)
            startActivity(intent)
        }
        binding.profileAdmin.setOnClickListener {
            val intent = Intent(this,AdminProfileActivity::class.java)
            startActivity(intent)
        }

        binding.completedOrderButton.setOnClickListener {
            val intent = Intent(this,AdminCompletedOrders::class.java)
            startActivity(intent)
        }

        binding.swipeRefreshAdminMain.setOnRefreshListener {
            menuItems.clear()
            pendingOrders()
            completedOrder()
            retrieveMenuItem()
            binding.swipeRefreshAdminMain.isRefreshing = false
        }

        databaseReference = FirebaseDatabase.getInstance().reference
        pendingOrders()
        completedOrder()
        retrieveMenuItem()
    }

    private fun completedOrder() {
        val userId = auth.currentUser?.uid!!
        val ref = databaseReference.child("Admin").child(userId)
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    val restName = snapshot.child("nameOfRestaurant").getValue()
                    SharedPref.initialize(this@AdminMainActivity)
                    SharedPref.setRestName(restName.toString())
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        val completedOrderRef = databaseReference.child("Admin").child(userId).child("CompletedOrders")
        var completedOrderItemCount = 0
        completedOrderRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                completedOrderItemCount = snapshot.childrenCount.toInt()
                binding.completedOrderCount.text = completedOrderItemCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun pendingOrders(){
        val cityName = SharedPref.getUserLocation().toString()
        database = FirebaseDatabase.getInstance()
        val pendingOrderRef = database.reference.child(cityName).child("OrderDetails")
        var pendingOrderItemCount = 0
        pendingOrderRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                pendingOrderItemCount = snapshot.childrenCount.toInt()
                binding.pendingOrderCount.text = pendingOrderItemCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun retrieveMenuItem() {
        val cityName = SharedPref.getUserLocation().toString()

        database = FirebaseDatabase.getInstance()
        val foodRef = databaseReference.child(cityName).child("Menu")

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
        val cityName = SharedPref.getUserLocation().toString()

        val menuItemToDelete =  menuItems[position]
        val menuItemKey = menuItemToDelete.key
        val foodMenuRef = database.reference.child(cityName).child("Menu").child(menuItemKey!!)
        foodMenuRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful){
                menuItems.removeAt(position)
                binding.recyclerAllItemAdmin.adapter?.notifyItemRemoved(position)
            }else
                Toast.makeText(this, "Not deleted", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStart() {
        super.onStart()
        SharedPref.initialize(this)
        val userLocation = SharedPref.getUserLocation()
        if (auth.currentUser != null && userLocation=="null"){
            startActivity(Intent(this,AdminChooseLocationActivity::class.java))
        }
    }
}