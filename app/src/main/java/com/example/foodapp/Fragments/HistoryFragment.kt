package com.example.foodapp.Fragments

import android.content.Intent
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
import com.example.foodapp.RecentOrderItemDetails
import com.example.foodapp.databinding.FragmentHistoryBinding
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
            seeItemDetails()
        }
        binding.recievedButton.setOnClickListener {
            updateOrderStatus()
            binding.recievedButton.visibility = View.INVISIBLE
        }
        return binding.root
    }

    private fun updateOrderStatus() {
        val itemPushKey = listOfOrderItems[0].itemPushKey
        val completeOrderRef= database.reference.child("CompletedOrder").child(itemPushKey!!)
        completeOrderRef.child("paymentRecieved").setValue(true)
    }

    private fun seeItemDetails() {
        listOfOrderItems.firstOrNull()?.let { recentOrder ->
            val intent = Intent(requireContext(), RecentOrderItemDetails::class.java)
            intent.putExtra("RecentOrderItem", recentOrder)
            startActivity(intent)
        }
    }

    private fun retrieveOrderHistory() {
        binding.recentOrderItem.visibility = View.INVISIBLE
        userId = auth.currentUser?.uid?:""

        val orderItemRef = database.reference.child("users").child(userId).child("OrderHistory")
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
                    setPreviuslyOrderItem()
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
                buyAgainFoodName.text = it.foodNames?.firstOrNull()
                buyAgainFoodPrice.text = it.foodPrices?.firstOrNull()
                val image = it?.foodImages?.firstOrNull()
                Glide.with(requireContext()).load((Uri.parse(image))).into(buyAgainFoodImage)

                val isOrderAccepted = listOfOrderItems[0].orderAccepted
                if (isOrderAccepted==true){
                    recievedButton.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setPreviuslyOrderItem() {
        val buyAgainFoodName = mutableListOf<String>()
        val buyAgainFoodPrice = mutableListOf<String>()
        val buyAgainFoodImage = mutableListOf<String>()

        for (i in 1 until listOfOrderItems.size){
            listOfOrderItems[i].foodNames?.firstOrNull()?.let { buyAgainFoodName.add(it) }
            listOfOrderItems[i].foodPrices?.firstOrNull()?.let { buyAgainFoodPrice.add(it) }
            listOfOrderItems[i].foodImages?.firstOrNull()?.let { buyAgainFoodImage.add(it) }
        }
        buyAgainAdapter = BuyAgainAdapter(buyAgainFoodName, buyAgainFoodPrice,buyAgainFoodImage, requireContext())
        binding.recycler.adapter = buyAgainAdapter
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
    }

}