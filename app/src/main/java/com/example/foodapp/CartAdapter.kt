package com.example.foodapp

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.CartItemViewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(private val cartItems: MutableList<String>, private val cartItemPrice: MutableList<String>, private val cartImage: MutableList<String>, private val cartQuantity: MutableList<Int>,private val context: Context, private val OnItemClicked: onItemClicked) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth = FirebaseAuth.getInstance()

    interface onItemClicked {
        fun onRemoveAllCartItems()
    }
    init {
        val database = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid?:""
        itemQuantities = cartQuantity.toIntArray()
        cartItemReference = database.reference.child("Users").child(userId).child("CartItems")
    }
    companion object{
        private var itemQuantities : IntArray = intArrayOf()
        private lateinit var cartItemReference: DatabaseReference
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun getItemCount(): Int = cartItems.size

    fun getUpdatedItemQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartQuantity)
        return itemQuantity
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class CartViewHolder(private val binding: CartItemViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                cartFoodName.text = cartItems[position]
                cartFoodPrice.text = "₹${cartItemPrice[position]}"
                Glide.with(context).load(Uri.parse(cartImage[position])).into(cartFoodImage)
                cartQuantity.text = itemQuantities[position].toString()
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
                cartQuantity[position] = itemQuantities[position]
                binding.cartQuantity.text = itemQuantities[position].toString()
                updateQuantities(position)
            }
            else{
                deleteItem(position)
            }
        }
        private fun increaseQuan(position: Int){
            if (itemQuantities[position]<10){
                itemQuantities[position]++
                cartQuantity[position] = itemQuantities[position]
                binding.cartQuantity.text = itemQuantities[position].toString()
                updateQuantities(position)
            }
        }
        private fun updateQuantities(position: Int){
            getUniqueKeyAtPosition(position){uniqueKey ->
                if (uniqueKey != null){
                    cartItemReference.child(uniqueKey).child("foodQuantity").setValue(
                        itemQuantities[position])
                }
            }
        }
        private fun deleteItem(position: Int){
            val positionRetrieve = position
            getUniqueKeyAtPosition(positionRetrieve){uniqueKey ->
                if (uniqueKey != null){
                    removeItem(position, uniqueKey)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            cartItemReference.child(uniqueKey).removeValue().addOnSuccessListener {
                cartItems.removeAt(position)
                cartImage.removeAt(position)
                cartItemPrice.removeAt(position)
                cartQuantity.removeAt(position)
                if (cartItems.isEmpty()){
                    OnItemClicked.onRemoveAllCartItems()
                }
                // update item quantities
                itemQuantities = itemQuantities.filterIndexed { index, i -> index != position  }.toIntArray()
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, cartItems.size)
            }.addOnFailureListener {
                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }

        private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
            cartItemReference.addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String ?= null
                    snapshot.children.forEachIndexed{index, dataSnapshot ->
                        if (index == positionRetrieve){
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}