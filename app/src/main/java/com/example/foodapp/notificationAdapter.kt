package com.example.foodapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.databinding.NotificationItemViewBinding
import com.example.foodapp.model.NotificationItems
import java.util.concurrent.TimeUnit

class notificationAdapter(private var notification: ArrayList<NotificationItems>): RecyclerView.Adapter<notificationAdapter.notificationViewHolder>() {

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
                notificationTextview.text = notification[position].notification
                val time = notification[position].time!!.toLong()
                val timeDetails = getTime(time)
                notificationTime.text = timeDetails
                notificationIcon.setImageResource(R.drawable.icon_info)
            }
        }

        private fun getTime(time: Long): String {
            val timeDifference = System.currentTimeMillis() - time
            val days = TimeUnit.MILLISECONDS.toDays(timeDifference)
            val hours = TimeUnit.MILLISECONDS.toHours(timeDifference)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference)

            return if (days>0){
                "$days days ago"
            }else if (hours>0){
                "$hours hrs ago"
            }else if(minutes>0){
                "$minutes min ago"
            }else{
                "Just now"
            }
        }
    }
}