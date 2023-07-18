package com.example.foodapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodapp.databinding.CartItemViewBinding

class CartAdapter(private val cartItems: MutableList<String>, private val cartItemPrice: MutableList<String>,private val cartImage : MutableList<Int>) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private val itemQuantities = IntArray(cartItems.size){1}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class CartViewHolder(private val binding: CartItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                val quantity = itemQuantities[position]
                cartFoodName.text = cartItems[position]
                cartFoodPrice.text = cartItemPrice[position]
                cartFoodImage.setImageResource(cartImage[position])
                cartQuantity.text = quantity.toString()
                decreaseQuantity.setOnClickListener {
                    decreaseQuan(position)
                }
                increaseQuantity.setOnClickListener {
                    increaseQuan(position)
                }
                deleteCartItem.setOnClickListener {
                    val itemPosition = adapterPosition
                    if (itemPosition != RecyclerView.NO_POSITION){
                        deleteItem(position)
                    }
                }


            }
        }
        private fun decreaseQuan(position: Int){
            if (itemQuantities[position]>1){
                itemQuantities[position]--
                binding.cartQuantity.text = itemQuantities[position].toString()
            }
        }
        private fun increaseQuan(position: Int){
            if (itemQuantities[position]<10){
                itemQuantities[position]++
                binding.cartQuantity.text = itemQuantities[position].toString()
            }
        }
        private fun deleteItem(position: Int){
            if (itemQuantities[position]>1){
                cartItems.removeAt(position)
                cartImage.removeAt(position)
                cartItemPrice.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cartItems.size)
            }
        }

    }
}