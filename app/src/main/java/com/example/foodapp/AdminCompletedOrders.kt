package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.databinding.ActivityAdminCompletedOrdersBinding
import com.example.foodapp.model.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminCompletedOrders : AppCompatActivity() {
    private lateinit var binding : ActivityAdminCompletedOrdersBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private var listOfCompleteOrderList : ArrayList<OrderDetail> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightGrey)
        super.onCreate(savedInstanceState)
        binding = ActivityAdminCompletedOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setSupportActionBar(orderStatusToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(null)
            orderStatusToolbar.setNavigationOnClickListener {
                finish()
            }
        }
        auth = FirebaseAuth.getInstance()
        retrieveCompleteOrderDetails()
    }

    private fun retrieveCompleteOrderDetails() {
        database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid!!
        val completeOrderRef = database.reference.child("Admin").child(userId).child("CompletedOrders")
            .orderByChild("currentItem")
        completeOrderRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listOfCompleteOrderList.clear()
                for (orderSnapshot in snapshot.children){
                    val completeOrder = orderSnapshot.getValue(OrderDetail::class.java)
                    completeOrder?.let { listOfCompleteOrderList.add(it) }
                }
                listOfCompleteOrderList.reverse()
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun setAdapter() {
        val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()

        for (order in listOfCompleteOrderList){
            order.userName?.let { customerName.add(it) }
            moneyStatus.add(order.paymentReceived!!)
        }

        val adapter = AdminCompletedOrdersAdapter(customerName,moneyStatus, this)
        binding.recyclerOrderStatus.adapter = adapter
        binding.recyclerOrderStatus.layoutManager = LinearLayoutManager(this)

    }
}