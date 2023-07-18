package com.example.foodapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.databinding.CartItemViewBinding
import com.example.foodapp.databinding.MenuItemViewBinding

class MenuAdapter(private val menuItems: MutableList<String>, private val menuItemPrice: MutableList<String>, private val MenuItemImage: MutableList<Int>) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
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
        fun bind(position: Int){
            binding.apply {
                foodNameMenu.text = menuItems[position]
                priceMenu.text = menuItemPrice[position]
                menuFoodImage.setImageResource(MenuItemImage[position])
            }
        }
    }
}
