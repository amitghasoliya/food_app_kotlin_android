package com.example.foodapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.MenuItemViewBinding
import com.example.foodapp.model.CartItems
import com.example.foodapp.model.MenuItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.time.times

class MenuAdapter(private val menuItems: List<MenuItems>, private val context: Context) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
    private var database : DatabaseReference = FirebaseDatabase.getInstance().reference
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid?:""

    init {
        val itemNumber = menuItems.size
        itemQuantities = IntArray(itemNumber){1}
        cartItemReference = database.child("Users").child(userId).child("CartItems")
    }
    companion object{
        private var itemQuantities : IntArray = intArrayOf()
        private lateinit var cartItemReference: DatabaseReference
    }

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
            binding.quantityLayout.visibility = View.VISIBLE
            val foodRef = database.child("Users").child(userId).child("CartItems")
            foodRef.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(foodSnapshot in snapshot.children){
                        val cartItems = foodSnapshot.getValue(CartItems::class.java)
                        if (menuItems[adapterPosition].foodImage == cartItems?.foodImage) {
                            binding.itemQuantity.text = cartItems?.foodQuantity.toString()
                            binding.quantityLayout.visibility = View.VISIBLE
                            binding.menuAddToCart.visibility = View.INVISIBLE
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show()
                }
            })

            binding.menuAddToCart.setOnClickListener {
                binding.quantityLayout.visibility = View.VISIBLE
                binding.menuAddToCart.visibility = View.INVISIBLE

                val foodName = menuItems[adapterPosition].foodName.toString()
                val foodPrice = menuItems[adapterPosition].foodPrice.toString()
                val foodImage = menuItems[adapterPosition].foodImage.toString()
                val cartItem = CartItems(foodName, foodPrice, foodImage, 1)
                database.child("Users").child(userId).child("CartItems").push().setValue(cartItem).addOnFailureListener {
                    Toast.makeText(context, "Failed to add in cart", Toast.LENGTH_SHORT).show()
                    binding.quantityLayout.visibility = View.INVISIBLE
                    binding.menuAddToCart.visibility = View.VISIBLE
                }
            }
        }

        fun bind(position: Int){
            val menuItem = menuItems[position]
            binding.apply {
                foodNameMenu.text = menuItem.foodName
                priceMenu.text = "₹${menuItem.foodPrice}"
                itemQuantity.text = itemQuantities[position].toString()
                Glide.with(context).load(Uri.parse(menuItem.foodImage)).into(menuFoodImage)
                decreaseQuantity.setOnClickListener {
                    decreaseQuan(position)
                }
                increaseQuantity.setOnClickListener {
                    increaseQuan(position)
                }
            }
        }

        private fun decreaseQuan(position: Int){
            if (itemQuantities[position]>1){
                itemQuantities[position]--
                binding.itemQuantity.text = itemQuantities[position].toString()
                updateQuantities(position)
            }
            else{
                deleteItem(position)
            }
        }
        private fun increaseQuan(position: Int){
            if (itemQuantities[position]<10){
                itemQuantities[position]++
                binding.itemQuantity.text = itemQuantities[position].toString()
                updateQuantities(position)

            }
        }
        private fun deleteItem(position: Int){
            getUniqueKeyAtPosition(position){uniqueKey ->
                if (uniqueKey != null){
                    cartItemReference.child(uniqueKey).removeValue()
                    binding.menuAddToCart.visibility = View.VISIBLE
                }
            }
        }

        private fun updateQuantities(position: Int){
            getUniqueKeyAtPosition(position){uniqueKey ->
                if (uniqueKey != null){
                    database.child("Users").child(userId).child("CartItems").child(uniqueKey).child("foodQuantity").setValue(
                        itemQuantities[position])
                }
            }
        }

        private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
            database.child("Users").child(userId).child("CartItems").addListenerForSingleValueEvent(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey: String ?= null
                    for (Snapshot in snapshot.children){
                        val item = Snapshot.getValue(CartItems::class.java)
                        if (menuItems[positionRetrieve].foodImage == item?.foodImage ){
                            uniqueKey = Snapshot.key
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


