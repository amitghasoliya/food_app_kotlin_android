package com.example.foodapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.MenuItemViewBinding
import com.example.foodapp.model.CartItems
import com.example.foodapp.model.MenuItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MenuAdapter(private val menuItems: List<MenuItems>, private val context: Context) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
//    private val itemClickListener:OnClickListener ?= null
    private lateinit var database : DatabaseReference
    private lateinit var auth : FirebaseAuth

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class MenuViewHolder(private val binding: MenuItemViewBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.menuAddToCart.setOnClickListener {
                val foodName = menuItems[adapterPosition].foodName.toString()
                val foodPrice = menuItems[adapterPosition].foodPrice.toString()
                val foodImage = menuItems[adapterPosition].foodImage.toString()

                auth = FirebaseAuth.getInstance()
                database = FirebaseDatabase.getInstance().reference
                val userId = auth.currentUser?.uid?:""
                val cartItem = CartItems(foodName, foodPrice, foodImage, 1)
                database.child("users").child(userId).child("CartItems").push().setValue(cartItem)
                Toast.makeText(context, "added", Toast.LENGTH_SHORT).show()
            }
        }

        fun bind(position: Int){
            val menuItem = menuItems[position]
            binding.apply {
                foodNameMenu.text = menuItem.foodName
                priceMenu.text = menuItem.foodPrice
                Glide.with(context).load(Uri.parse(menuItem.foodImage)).into(menuFoodImage)
            }

        }
    }


//    interface OnClickListener {
//        fun onItemClick(position: Int)
//    }
}


