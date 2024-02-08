package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.foodapp.databinding.ActivityPayOutBinding
import com.example.foodapp.model.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class pay_out : AppCompatActivity() {
    private lateinit var binding: ActivityPayOutBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var totalAmount: String
    private lateinit var foodNames : ArrayList<String>
    private lateinit var foodImagesUri : ArrayList<String>
    private lateinit var foodPrices : ArrayList<String>
    private lateinit var foodQuantity : ArrayList<Int>
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference

        setUserData()

        val intent = intent
        foodNames = intent.getStringArrayListExtra("foodName") as ArrayList<String>
        foodPrices = intent.getStringArrayListExtra("foodPrice") as ArrayList<String>
        foodImagesUri = intent.getStringArrayListExtra("foodImage") as ArrayList<String>
        foodQuantity = intent.getIntegerArrayListExtra("foodQuantities") as ArrayList<Int>

        totalAmount = calculateTotal()
        binding.totalAmount.setText("Total Amount: $totalAmount")
        binding.backButton.setOnClickListener {
            finish()
        }
        binding.placeOrder.setOnClickListener {
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()

            if (name.isBlank() && address.isBlank() && phone.isBlank()){
                Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
            }else{
                placeOrder()
            }

            val intent = Intent(this@pay_out,congrats_activity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid?:""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseRef.child("OrderDetails").push().key
        val orderDetails = OrderDetail(userId, name, foodNames, foodPrices, foodImagesUri, foodQuantity, address, phone, totalAmount, false, false, itemPushKey, time)
        val orderRef = databaseRef.child("OrderDetails").child(itemPushKey!!)
        orderRef.setValue(orderDetails)
        removeItemFromCart()
        addOrderToHistory(orderDetails)
    }

    private fun addOrderToHistory(orderDetails: OrderDetail) {
        databaseRef.child("users").child(userId).child("OrderHistory").child(orderDetails.itemPushKey!!).setValue(orderDetails)
    }

    private fun removeItemFromCart() {
        val cartItemsRef = databaseRef.child("users").child(userId).child("CartItems")
        cartItemsRef.removeValue()
    }

    private fun calculateTotal(): String {
        var totalAmount = 0
        for (i in 0 until foodPrices.size){
            var price = foodPrices[i]
            val firstChar = price.first()
            val priceIntValue = price.toInt()
//            if (firstChar=='₹'){
//                price.drop(1).toInt()
//            } else {
//                price.toInt()
//            }
            var quantity = foodQuantity[i]
            totalAmount += priceIntValue *quantity
        }
        return "₹$totalAmount"
    }

    private fun setUserData() {
        val user = auth.currentUser
        if(user!=null){
            val userId = user.uid
            val userRef = databaseRef.child("users").child(userId)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        val names = snapshot.child("name").getValue(String::class.java)?:""
                        val address = snapshot.child("address").getValue(String::class.java)?:""
                        val phone = snapshot.child("phone").getValue(String::class.java)?:""

                        binding.name.setText(names)
                        binding.address.setText(address)
                        binding.phone.setText(phone)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
}