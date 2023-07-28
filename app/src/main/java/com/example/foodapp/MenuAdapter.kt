package com.example.foodapp

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.databinding.CartItemViewBinding
import com.example.foodapp.databinding.MenuItemViewBinding

class MenuAdapter(private val menuItems: MutableList<String>, private val menuItemPrice: MutableList<String>, private val MenuItemImage: MutableList<Int>,private val context: Context) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    private val itemClickListener:OnClickListener ?= null
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
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION){
                    itemClickListener?.onItemClick(position)
                }
                val intent = Intent(context, detail_activity::class.java)
                intent.putExtra("name",menuItems.get(position))
                intent.putExtra("image",MenuItemImage.get(position))
                intent.putExtra("price",menuItemPrice.get(position))
                context.startActivity(intent)
            }
        }
        fun bind(position: Int){
            binding.apply {
                foodNameMenu.text = menuItems[position]
                priceMenu.text = menuItemPrice[position]
                menuFoodImage.setImageResource(MenuItemImage[position])
            }
        }
    }
    interface OnClickListener {
        fun onItemClick(position: Int)
    }
}


