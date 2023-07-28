package com.example.foodapp

import android.app.Notification
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.databinding.NotificationItemViewBinding

class notificationAdapter(private var notification: ArrayList<String>): RecyclerView.Adapter<notificationAdapter.notificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): notificationViewHolder {
        val binding = NotificationItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return notificationViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notification.size
    }

    override fun onBindViewHolder(holder: notificationViewHolder, position: Int) {
        holder.bind(position)
    }
    inner class notificationViewHolder(private val binding: NotificationItemViewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int){
            binding.apply {
                notificationTextview.text = notification[position]
                notificationIcon.setImageResource(R.drawable.baseline_info_24)
            }
        }
    }
}