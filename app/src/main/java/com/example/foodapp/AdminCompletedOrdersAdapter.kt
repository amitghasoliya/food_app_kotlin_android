package com.example.foodapp

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.databinding.AdminCompletedOrderItemBinding

class AdminCompletedOrdersAdapter(
    private val customerNames: MutableList<String>,
    private val paymentStatus: MutableList<Boolean>,
    private val context: Context
): RecyclerView.Adapter<AdminCompletedOrdersAdapter.OrderStatusViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderStatusViewHolder {
        val binding = AdminCompletedOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return OrderStatusViewHolder(binding)
    }

    override fun getItemCount(): Int = customerNames.size

    override fun onBindViewHolder(holder: OrderStatusViewHolder, position: Int) {
        holder.bind(position, context)
    }

    inner class OrderStatusViewHolder(private val binding: AdminCompletedOrderItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int, context: Context) {
            binding.apply {
                customerName.text = customerNames[position]
                if (paymentStatus[position]==true){
                    moneyStatus.text = "Recieved"
                    orderDelivered.visibility = View.VISIBLE
                }else{
                    moneyStatus.text = "Not Recieved"
                    orderDelivered.visibility = View.INVISIBLE
                }
                val colorMap = mapOf(
                    true to ContextCompat.getColor(context, R.color.green), false to ContextCompat.getColor(context, R.color.Primary)
                )
                moneyStatus.setTextColor(colorMap[paymentStatus[position]]?:Color.BLACK)
                moneyStatus.backgroundTintList = ColorStateList.valueOf(colorMap[paymentStatus[position]]?:Color.BLACK)
            }
        }

    }
}