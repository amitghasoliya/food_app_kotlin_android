package com.example.foodapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.AdminPendingOrderItemBinding

class AdminPendingOrderAdapter(private val foodName:MutableList<String>, private val quantity:MutableList<String>, private val foodImage: MutableList<String>, private val context: Context, private val itemClicked: OnItemClicked) : RecyclerView.Adapter<AdminPendingOrderAdapter.PendingOrderViewHolder>() {

    interface OnItemClicked{
        fun onItemAcceptClickListener(position: Int)
        fun onItemDispatchClickListener(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PendingOrderViewHolder {
        val binding = AdminPendingOrderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PendingOrderViewHolder(binding)
    }

    override fun getItemCount(): Int = foodName.size

    override fun onBindViewHolder(holder: PendingOrderViewHolder, position: Int) {
        holder.bind(position, context)
    }

    inner class PendingOrderViewHolder(private val binding: AdminPendingOrderItemBinding): RecyclerView.ViewHolder(binding.root) {
        private var isAccepted = false
        fun bind(position: Int, context: Context) {
            binding.apply {
                pendingOrderFoodName.text = foodName[position]
                pendingOrderFoodQuantity.text = quantity[position]
                Glide.with(context).load(Uri.parse(foodImage[position])).into(pendingOrderFoodImage)
                pendingOrderAcceptButton.apply {
                    if (!isAccepted){
                        text = "Accept"
                    }else{
                        text = "Dispatch"
                    }
                    setOnClickListener {
                        if (!isAccepted){
                            text = "Dispatch"
                            isAccepted = true
                            itemClicked.onItemAcceptClickListener(position)
                        }else{
                            foodName.removeAt(adapterPosition)
                            notifyItemRemoved(adapterPosition)
                            itemClicked.onItemDispatchClickListener(position)
                        }
                    }
                }

            }
        }

    }
}