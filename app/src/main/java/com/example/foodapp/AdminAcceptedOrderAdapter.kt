package com.example.foodapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.Fragments.AcceptedOrders
import com.example.foodapp.databinding.AdminPendingOrderItemBinding
import com.example.foodapp.model.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AdminAcceptedOrderAdapter(private val foodName:MutableList<String>, private val foodItems: MutableList<OrderDetail>, private val context: Context, private val itemClicked: OnItemClicked) : RecyclerView.Adapter<AdminAcceptedOrderAdapter.AcceptedOrderViewHolder>() {

    interface OnItemClicked{
        fun onItemDispatchClickListener(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptedOrderViewHolder {
        val binding = AdminPendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AcceptedOrderViewHolder(binding)
    }

    override fun getItemCount(): Int = foodName.size

    override fun onBindViewHolder(holder: AcceptedOrderViewHolder, position: Int) {
        holder.bind(position, context)
    }

    inner class AcceptedOrderViewHolder(private val binding: AdminPendingOrderItemBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int, context: Context) {
            val item = foodItems[position]
            binding.apply {
                pendingOrderFoodName.text = item.foodNames!!.joinToString("\n")
                pendingOrderFoodQuantity.text = "${item.foodQuantities!!.joinToString("\n")}"
                val size = item.foodNames!!.size
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
                    text = "Dispatch"
                    setOnClickListener {
                        foodName.removeAt(adapterPosition)
                        notifyItemRemoved(adapterPosition)
                        itemClicked.onItemDispatchClickListener(position)
                    }
                }
            }
        }

    }
}