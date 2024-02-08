package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.foodapp.databinding.ActivityAdminMainBinding
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
    private lateinit var completedOrderRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.addItemAdmin.setOnClickListener{
            val intent = Intent(this,AdminAddItemActivity::class.java)
            startActivity(intent)
        }
        binding.allItemMenuAdmin.setOnClickListener{
            val intent = Intent(this,AdminAllItemActivity::class.java)
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

        binding.orderStatus.setOnClickListener {
            val intent = Intent(this,AdminOrderStatus::class.java)
            startActivity(intent)
        }
        pendingOrders()
        completedOrder()
    }

    private fun completedOrder() {
        var completedOrderRef = database.reference.child("CompletedOrder")
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
        database = FirebaseDatabase.getInstance()
        var pendingOrderRef = database.reference.child("OrderDetails")
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
}