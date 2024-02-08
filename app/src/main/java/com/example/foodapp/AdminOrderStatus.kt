package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.databinding.ActivityAdminOrderStatusBinding
import com.example.foodapp.model.OrderDetail
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminOrderStatus : AppCompatActivity() {
    private lateinit var binding : ActivityAdminOrderStatusBinding
    private lateinit var database : FirebaseDatabase
    private var listOfCompleteOrderList : ArrayList<OrderDetail> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrderStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)

        retrieveCompleteOrderDetails()
    }

    private fun retrieveCompleteOrderDetails() {
        database = FirebaseDatabase.getInstance()
        val completeOrderRef = database.reference.child("CompletedOrder")
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

        val adapter = AdminOrderStatusAdapter(customerName,moneyStatus)
        binding.recyclerOrderStatus.adapter = adapter
        binding.recyclerOrderStatus.layoutManager = LinearLayoutManager(this)

    }
}