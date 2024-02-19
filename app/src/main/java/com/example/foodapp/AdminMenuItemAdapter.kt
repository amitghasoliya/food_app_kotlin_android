package com.example.foodapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ExpandableListView.OnChildClickListener
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.AdminMenuItemBinding
import com.example.foodapp.databinding.CartItemViewBinding
import com.example.foodapp.model.AllMenu
import com.google.firebase.database.DatabaseReference

class AdminMenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListener: (position:Int) -> Unit
): RecyclerView.Adapter<AdminMenuItemAdapter.MenuItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val binding = AdminMenuItemBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return MenuItemViewHolder(binding)
    }

    override fun getItemCount(): Int = menuList.size

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class MenuItemViewHolder(private val binding: AdminMenuItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                menuFoodName.text = menuList[position].foodName
                menuFoodPrice.text = "₹${ menuList[position].foodPrice }"
                Glide.with(context).load(Uri.parse(menuList[position].foodImage)).into(menuFoodImage)
                
                deleteMenuItem.setOnClickListener {
                    onDeleteClickListener(position)
                }
            }
        }
    }
}