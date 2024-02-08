package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.databinding.ActivityAdminPendingOrdersBinding
import com.example.foodapp.model.OrderDetail
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminPendingOrdersActivity : AppCompatActivity(), AdminPendingOrderAdapter.OnItemClicked{
    private lateinit var binding : ActivityAdminPendingOrdersBinding
    private var listOfFoodNames : MutableList<String> = mutableListOf()
    private var listOfFoodPrices : MutableList<String> = mutableListOf()
    private var listOfFoodImages : MutableList<String> = mutableListOf()
    private var listOfOrderItems : MutableList<OrderDetail> = mutableListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPendingOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference.child("OrderDetails")
        getOrderDetails()
    }

    private fun getOrderDetails() {
        databaseRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(orderSnapshot in snapshot.children){
                    val orderDetails = orderSnapshot.getValue(OrderDetail::class.java)
                    orderDetails?.let { listOfOrderItems.add(it) }
                }
                addDataToList()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun addDataToList() {
        for(orderItem in listOfOrderItems){
            orderItem.userName?.let { listOfFoodNames.add(it) }
            orderItem.totalPrice?.let { listOfFoodPrices.add(it) }
            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach{
                listOfFoodImages.add(it)
            }
        }
        setAdapter()
    }

    private fun setAdapter() {
        val adapter = AdminPendingOrderAdapter(listOfFoodNames,listOfFoodPrices, listOfFoodImages, this,this)
        binding.recyclerPendingOrdersAdmin.layoutManager = LinearLayoutManager(this)
        binding.recyclerPendingOrdersAdmin.adapter = adapter
    }

    override fun onItemAcceptClickListener(position: Int) {
        val childItemPushKey = listOfOrderItems[position].itemPushKey
        val clickItemOrderRef = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderRef?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptStatus(position)
    }

    override fun onItemDispatchClickListener(position: Int) {
        val dispatchItemPushKey = listOfOrderItems[position].itemPushKey
        val dispatchItemOrderRef = database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
        dispatchItemOrderRef.setValue(listOfOrderItems[position])
            .addOnSuccessListener { deleteThisItemFromOrderDetails(dispatchItemPushKey) }
    }

    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String){
        val orderDetailsItemRef = database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Order is dispatched", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateOrderAcceptStatus(position: Int) {
        val userIdOfClickedItem = listOfOrderItems[position].userUid
        val pushKeyOfClickedItem = listOfOrderItems[position].itemPushKey
        val orderHistoryRef = database.reference.child("users").child(userIdOfClickedItem!!).child("OrderHistory").child(pushKeyOfClickedItem!!)
        orderHistoryRef.child("orderAccepted").setValue(true)
        databaseRef.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
    }
}