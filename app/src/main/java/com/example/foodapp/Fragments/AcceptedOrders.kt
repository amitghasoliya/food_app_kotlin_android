package com.example.foodapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.AdminAcceptedOrderAdapter
import com.example.foodapp.databinding.FragmentAcceptedOrdersBinding
import com.example.foodapp.model.NotificationItems
import com.example.foodapp.model.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AcceptedOrders : Fragment(),
    AdminAcceptedOrderAdapter.OnItemClicked {
    private lateinit var binding : FragmentAcceptedOrdersBinding
    private var listOfUsernames : MutableList<String> = mutableListOf()
    private var listOfOrderItems : MutableList<OrderDetail> = mutableListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAcceptedOrdersBinding.inflate(inflater,container,false)
        database = FirebaseDatabase.getInstance()

        binding.swipeRefreshAcceptedOrders.setOnRefreshListener {
            listOfUsernames.clear()
            listOfOrderItems.clear()
            showAcceptedOrders()
            binding.swipeRefreshAcceptedOrders.isRefreshing = false
        }
        databaseRef = database.reference.child("Admin").child(userId.toString()).child("PendingOrders")
        showAcceptedOrders()
        return binding.root
    }

    private fun showAcceptedOrders() {
        databaseRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(orderSnapshot in snapshot.children){
                    val orderDetails = orderSnapshot.getValue(OrderDetail::class.java)
                    orderDetails?.let {
                        listOfOrderItems.add(it)
                    }
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
            orderItem.userName?.let { listOfUsernames.add(it) }
        }
        setAdapter()
    }

    private fun setAdapter() {
        val adapter = AdminAcceptedOrderAdapter(listOfUsernames,listOfOrderItems,requireContext(), this)
        binding.recyclerAcceptedOrdersAdmin.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerAcceptedOrdersAdmin.adapter = adapter
    }

    override fun onItemDispatchClickListener(position: Int) {
        val userUid = listOfOrderItems[position].userUid!!
        val dispatchItemPushKey = listOfOrderItems[position].itemPushKey!!
        val userLocation = database.reference.child("Users").child(userUid).child("OrderHistory").child(dispatchItemPushKey)
        userLocation.child("orderDispatched").setValue(true)

        val time = System.currentTimeMillis().toString()
        val notiKey = database.reference.child("Users").child(userUid).child("Notifications").push().key
        val notificationRef = database.reference.child("Users").child(userUid).child("Notifications").child(notiKey.toString())
        val notification = NotificationItems("Your order is dispatched",time,false)
        notificationRef.setValue(notification)

        val dispatchItemOrderRef = database.reference.child("Admin").child(userId.toString()).child("CompletedOrders").child(dispatchItemPushKey)
        dispatchItemOrderRef.setValue(listOfOrderItems[position])
        dispatchItemOrderRef.child("orderDispatched").setValue(true)
            .addOnSuccessListener { deleteThisItemFromOrderDetails(dispatchItemPushKey) }
    }

    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String){
        val orderDetailsItemRef = database.reference.child("Admin").child(userId.toString()).child("PendingOrders").child(dispatchItemPushKey)
        orderDetailsItemRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Order is dispatched", Toast.LENGTH_SHORT).show()
            }
    }
}