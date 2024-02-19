package com.example.foodapp.Fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.foodapp.BuyAgainAdapter
import com.example.foodapp.R
import com.example.foodapp.OrderItemDetails
import com.example.foodapp.databinding.FragmentHistoryBinding
import com.example.foodapp.model.NotificationItems
import com.example.foodapp.model.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HistoryFragment : Fragment() {

    private lateinit var binding : FragmentHistoryBinding
    private lateinit var buyAgainAdapter : BuyAgainAdapter
    private lateinit var auth : FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private var listOfOrderItems : MutableList<OrderDetail> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater, container, false)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        retrieveOrderHistory()
        binding.recentOrderItem.setOnClickListener {
//            seeItemDetails()
        }
        binding.recievedButton.setOnClickListener {
            updateOrderStatus()
            binding.orderStatus.text = "Delivered"
            binding.orderStatus.setTextColor(resources.getColor(R.color.green, activity?.theme))
            binding.recievedButton.visibility = View.INVISIBLE
        }
        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderItems[0].itemPushKey!!
        val RestId = listOfOrderItems[0].restaurantId!!
        val orderHistoryRef = database.reference.child("Users").child(userId).child("OrderHistory").child(itemPushKey)
        val completeOrderRef= database.reference.child("Admin").child(RestId).child("CompletedOrders").child(itemPushKey)
        orderHistoryRef.child("paymentReceived").setValue(true)
        completeOrderRef.child("paymentReceived").setValue(true)

        val time = System.currentTimeMillis().toString()
        val notiKey = database.reference.child("Users").child(userId).child("Notifications").push().key
        val notificationRef = database.reference.child("Users").child(userId).child("Notifications").child(notiKey.toString())
        val notification = NotificationItems("Your order is delivered, Enjoy your food!",time,false)
        notificationRef.setValue(notification)
    }

    private fun seeItemDetails() {
        listOfOrderItems.firstOrNull()?.let { recentOrder ->
            val intent = Intent(requireContext(), OrderItemDetails::class.java)
            intent.putExtra("OrderItem", recentOrder)
            startActivity(intent)
        }
    }

    private fun retrieveOrderHistory() {
        binding.recentOrderItem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid?:""

        val orderItemRef = database.reference.child("Users").child(userId).child("OrderHistory")
        val shortingQuery = orderItemRef.orderByChild("currentTime")
        shortingQuery.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children){
                    val orderHistoryItem = orderSnapshot.getValue(OrderDetail::class.java)
                    orderHistoryItem?.let { listOfOrderItems.add(it) }
                }
                listOfOrderItems.reverse()
                if (listOfOrderItems.isNotEmpty()){
                    setDataInRecentOrderItem()
                    if (listOfOrderItems.size>1){
                        setPreviouslyOrderItem()
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setDataInRecentOrderItem() {
        binding.recentOrderItem.visibility = View.VISIBLE
        val recentOrderItem = listOfOrderItems.firstOrNull()
        recentOrderItem?.let {
            with(binding){
                buyAgainFoodName.text = it.foodNames?.joinToString("\n")
                restName.text = it.restaurantName.toString()
                val size =it.foodNames!!.size
                if (it.foodNames!!.size>4){
                    buyAgainFoodName.text = "${ it.foodNames?.dropLast(size-3)?.joinToString("\n")} \n${it.foodNames!!.get(3).substringBefore(" ")}.. & more"
                }
                buyAgainFoodQuantity.text = it.foodQuantities?.joinToString("\n"){
                    "${it} ×"
                }

                if (it.orderAccepted ==true){
                    orderStatus.text = "Order Preparing.."
                    orderStatus.setTextColor(resources.getColor(R.color.Primary, activity?.theme))
                }else{
                    restName.text = "Waiting to be accepted.."
                    orderStatus.text = "Order Pending"
                    orderStatus.setTextColor(Color.BLACK)
                }

                val image = it.foodImages?.firstOrNull()
                Glide.with(requireContext()).load((Uri.parse(image))).into(buyAgainFoodImage)

                val isOrderDispatched = listOfOrderItems[0].orderDispatched
                if (isOrderDispatched==true){
                    orderStatus.text = "On the way.."
                    orderStatus.setTextColor(resources.getColor(R.color.green, activity?.theme))
                    recievedButton.visibility = View.VISIBLE
                }
                if (it.paymentReceived == true){
                    orderStatus.text = "Delivered"
                    orderStatus.setTextColor(resources.getColor(R.color.green, activity?.theme))
                    recievedButton.visibility = View.INVISIBLE
                }
            }
        }
    }

    private fun setPreviouslyOrderItem() {
        if (listOfOrderItems[1].foodNames!!.isNotEmpty()){
            binding.noHistoryText.visibility = View.INVISIBLE
        }
        buyAgainAdapter = BuyAgainAdapter(listOfOrderItems.subList(1, listOfOrderItems.size), requireContext())
        binding.recycler.adapter = buyAgainAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
    }

}