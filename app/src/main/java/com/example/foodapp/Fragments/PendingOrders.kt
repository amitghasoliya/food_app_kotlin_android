package com.example.foodapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.AdminPendingOrderAdapter
import com.example.foodapp.SharedPref
import com.example.foodapp.databinding.FragmentPendingOrdersBinding
import com.example.foodapp.model.NotificationItems
import com.example.foodapp.model.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingOrders : Fragment(), AdminPendingOrderAdapter.OnItemClicked {
    private lateinit var binding : FragmentPendingOrdersBinding
    private var listOfUsername : MutableList<String> = mutableListOf()
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
        binding = FragmentPendingOrdersBinding.inflate(inflater,container,false)

        binding.swipeRefreshPendingOrders.setOnRefreshListener {
            listOfUsername.clear()
            listOfOrderItems.clear()
            getOrderDetails()
            binding.swipeRefreshPendingOrders.isRefreshing = false
        }

        SharedPref.initialize(requireContext())
        val cityName = SharedPref.getUserLocation().toString()
        database = FirebaseDatabase.getInstance()
        databaseRef = database.reference.child(cityName).child("OrderDetails")
        getOrderDetails()
        return binding.root
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
            orderItem.userName?.let { listOfUsername.add(it) }
        }
        setAdapter()
    }

    private fun setAdapter() {
        val adapter = AdminPendingOrderAdapter(listOfUsername,listOfOrderItems,requireContext(), this)
        binding.recyclerPendingOrdersAdmin.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerPendingOrdersAdmin.adapter = adapter
    }

    override fun onItemAcceptClickListener(position: Int) {
        val restName = SharedPref.getRestName()
        val userIdOfClickedItem = listOfOrderItems[position].userUid!!
        val pushKeyOfClickedItem = listOfOrderItems[position].itemPushKey!!

        val time = System.currentTimeMillis().toString()
        val notiKey = database.reference.child("Users").child(userIdOfClickedItem).child("Notifications").push().key
        val notificationRef = database.reference.child("Users").child(userIdOfClickedItem).child("Notifications").child(notiKey!!)
        val notification = NotificationItems("Your order is accepted by $restName",time,false)
        notificationRef.setValue(notification)

        val orderHistoryRef = database.reference.child("Users").child(userIdOfClickedItem).child("OrderHistory").child(pushKeyOfClickedItem)
        orderHistoryRef.child("orderAccepted").setValue(true)
        orderHistoryRef.child("restaurantId").setValue(userId)
        orderHistoryRef.child("restaurantName").setValue(restName)
    }
}