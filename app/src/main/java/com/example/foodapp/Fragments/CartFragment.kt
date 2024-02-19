package com.example.foodapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.CartAdapter
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentCartBinding
import com.example.foodapp.model.CartItems
import com.example.foodapp.pay_out
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment(), CartAdapter.onItemClicked {
    private lateinit var binding : FragmentCartBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var foodNames : MutableList<String>
    private lateinit var foodImagesUri : MutableList<String>
    private lateinit var foodPrices : MutableList<String>
    private lateinit var foodQuantity : MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()
        retrieveItems()

        binding.proceed.setOnClickListener {
            getOrderItemDetails()
        }
        return binding.root
    }

    private fun getOrderItemDetails() {
        val orderIdReference = database.reference.child("Users").child(userId).child("CartItems")
        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodQuantities = cartAdapter.getUpdatedItemQuantities()

        orderIdReference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                foodQuantities.clear()
                for (foodSnapshot in snapshot.children){
                    val orderItems = foodSnapshot.getValue(CartItems::class.java)
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                    orderItems?.foodQuantity?.let { foodQuantities.add(it) }
                }
                sendDetailsToPayOutActivity(foodName, foodPrice, foodImage, foodQuantities)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendDetailsToPayOutActivity(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodImage: MutableList<String>,
        foodQuantities: MutableList<Int>
    ) {
        if(isAdded && context!=null && foodImage.isNotEmpty()){
            val intent = Intent(requireContext(), pay_out::class.java)
            intent.putExtra("foodName", foodName as ArrayList<String>)
            intent.putExtra("foodPrice", foodPrice as ArrayList<String>)
            intent.putExtra("foodImage", foodImage as ArrayList<String>)
            intent.putExtra("foodQuantities", foodQuantities as ArrayList<Int>)
            startActivity(intent)
        }
    }

    private fun retrieveItems() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid?:""
        val foodRef = database.reference.child("Users").child(userId).child("CartItems")
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodImagesUri = mutableListOf()
        foodQuantity = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(foodSnapshot in snapshot.children){
                    val cartItems = foodSnapshot.getValue(CartItems::class.java)
                    cartItems?.foodName?.let {foodNames.add(it)}
                    cartItems?.foodPrice?.let {foodPrices.add(it)}
                    cartItems?.foodImage?.let {foodImagesUri.add(it)}
                    cartItems?.foodQuantity?.let {foodQuantity.add(it)}
                }
                setAdapter()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAdapter() {
        if (foodNames.isNotEmpty()){
            binding.emptyCartText.visibility = View.INVISIBLE
            binding.proceed.visibility = View.VISIBLE
        }
        cartAdapter = CartAdapter(foodNames, foodPrices,foodImagesUri,foodQuantity,requireContext(), this)
        binding.cartRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecycler.adapter = cartAdapter
    }

    override fun onRemoveAllCartItems() {
        binding.emptyCartText.visibility = View.VISIBLE
        binding.proceed.visibility = View.INVISIBLE
    }

}