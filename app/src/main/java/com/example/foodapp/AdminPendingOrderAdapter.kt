package com.example.foodapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.AdminPendingOrderItemBinding
import com.example.foodapp.model.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminPendingOrderAdapter(private val usernames: MutableList<String>,private val foodItems: MutableList<OrderDetail>, private val context: Context, private val itemClicked: OnItemClicked) : RecyclerView.Adapter<AdminPendingOrderAdapter.PendingOrderViewHolder>() {

    private var databaseRef: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    interface OnItemClicked{
        fun onItemAcceptClickListener(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = AdminPendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun getItemCount(): Int = usernames.size

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position, context)
    }

    inner class PendingOrderViewHolder(private val binding: AdminPendingOrderItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, context: Context) {
            val item = foodItems[position]
            binding.apply {
                pendingOrderFoodName.text = item.foodNames!!.joinToString("\n")
                pendingOrderFoodQuantity.text = "${item.foodQuantities!!.joinToString("\n")}"
                val size =foodItems[position].foodNames!!.size
                if (item.foodNames!!.size>4){
                    pendingOrderFoodName.text = "${ item.foodNames?.dropLast(size-3)?.joinToString("\n")} \n${item.foodNames!!.get(3).substringBefore(" ")}.. & more"
                }
                pendingOrderFoodQuantity.text = item.foodQuantities?.joinToString("\n"){
                    "${it} ×"
                }
                customerName.text = "Name: ${item.userName}"
                customerPhone.text = "Phone: ${item.phoneNumber}"
                customerAddress.text = "Address: ${item.address}"
                pendingOrderFoodPrice.text = item.totalPrice
                Glide.with(context).load(Uri.parse(foodItems[position].foodImages!!.get(0))).into(pendingOrderFoodImage)
                pendingOrderAcceptButton.apply {
                    text = "Accept"
                    setOnClickListener {
                        SharedPref.initialize(context)
                        val cityName = SharedPref.getUserLocation().toString()
                        val userId = auth.currentUser?.uid!!

                        val orderRef = FirebaseDatabase.getInstance().getReference(cityName).child("OrderDetails").child(item.itemPushKey.toString())
                        orderRef.child("orderAccepted").setValue(true)
                        val adminLocation = databaseRef.reference.child("Admin").child(userId).child("PendingOrders").child(item.itemPushKey.toString())
                        adminLocation.setValue(item)
                        adminLocation.child("restaurantId").setValue(userId)
                        adminLocation.child("orderAccepted").setValue(true)
                        orderRef.removeValue()

                        usernames.removeAt(position)
                        notifyItemRemoved(position)
                        itemClicked.onItemAcceptClickListener(position)
                    }
                }

            }
        }

    }
}