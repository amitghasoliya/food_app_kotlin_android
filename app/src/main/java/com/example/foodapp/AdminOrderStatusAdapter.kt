package com.example.foodapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.AdminOrderStatusItemBinding

class AdminOrderStatusAdapter(
    private val customerNames: MutableList<String>,
    private val paymentStatus: MutableList<Boolean>
): RecyclerView.Adapter<AdminOrderStatusAdapter.OrderStatusViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderStatusViewHolder {
        val binding = AdminOrderStatusItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return OrderStatusViewHolder(binding)
    }

    override fun getItemCount(): Int = customerNames.size

    override fun onBindViewHolder(holder: OrderStatusViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class OrderStatusViewHolder(private val binding: AdminOrderStatusItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                customerName.text = customerNames[position]
                if (paymentStatus[position]==true){
                    moneyStatus.text = "Recieved"
                }else{
                    moneyStatus.text = "Not Recieved"
                }
                val colorMap = mapOf(
                    true to Color.GREEN, false to Color.RED, "Pending" to Color.GRAY
                )
                moneyStatus.setTextColor(colorMap[paymentStatus[position]]?:Color.BLACK)
                moneyStatus.backgroundTintList = ColorStateList.valueOf(colorMap[paymentStatus[position]]?:Color.BLACK)
            }
        }

    }
}