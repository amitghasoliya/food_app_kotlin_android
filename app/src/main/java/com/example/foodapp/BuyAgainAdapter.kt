package com.example.foodapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.BuyAgainItemViewBinding

class BuyAgainAdapter(private val buyAgainFoodName: MutableList<String>, private val buyAgainFoodPrice: MutableList<String>,private val buyAgainFoodImage:MutableList<String>, private val context: Context):
    RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding = BuyAgainItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuyAgainViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return buyAgainFoodName.size
    }

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(buyAgainFoodName[position],buyAgainFoodPrice[position], buyAgainFoodImage[position], context)
    }

    inner class BuyAgainViewHolder(private val binding: BuyAgainItemViewBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(foodName: String, foodPrice: String, foodImage: String, context: Context) {
            binding.buyAgainFoodName.text = foodName
            binding.buyAgainFoodPrice.text = foodPrice
            Glide.with(context).load(Uri.parse(foodImage)).into(binding.buyAgainFoodImage)
        }

    }
}