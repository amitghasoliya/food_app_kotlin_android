package com.example.foodapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.foodapp.databinding.ActivityPayOutBinding
import com.example.foodapp.model.AllMenu
import com.example.foodapp.model.NotificationItems
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
    private lateinit var notification : NotificationItems
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        SharedPref.initialize(this)
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

        binding.apply {
            setSupportActionBar(checkoutToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(null)
            checkoutToolbar.setNavigationOnClickListener {
                finish()
            }
        }
        binding.codRadioButton.isChecked = true
        binding.placeOrder.setOnClickListener {
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()

            if (name.isBlank() || address.isBlank() || phone.isBlank()){
                Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
            }else{
                placeOrder()
            }
        }
    }

    private fun placeOrder() {
        val cityName = SharedPref.getUserLocation().toString()

        setOrderCounts(cityName)

        userId = auth.currentUser?.uid?:""
        val time = System.currentTimeMillis()
        val itemPushKey = databaseRef.child(cityName).child("OrderDetails").push().key
        val orderDetails = OrderDetail(userId, name, foodNames, foodPrices, foodImagesUri, foodQuantity, address, phone, totalAmount,"","", false,false, false, itemPushKey, time)
        val orderRef = databaseRef.child(cityName).child("OrderDetails").child(itemPushKey!!)
        orderRef.setValue(orderDetails)
        removeItemFromCart()
        notification = NotificationItems("Your order is placed successfully!", time.toString(),false)
        addOrderToHistory(orderDetails, notification)
        val intent = Intent(this@pay_out,congrats_activity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setOrderCounts(cityName:String) {
        val menuRef = databaseRef.child(cityName).child("Menu")
        menuRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (Snapshot in snapshot.children){
                    val item = Snapshot.getValue(AllMenu::class.java)!!
                    val newOrderCount = item.orderCount!!+1
                    for (item1 in foodImagesUri){
                        if (item1 == item.foodImage){
                            val itemKey = item.key
                            databaseRef.child(cityName).child("Menu").child(itemKey.toString()).child("orderCount").setValue(newOrderCount)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun addOrderToHistory(orderDetails: OrderDetail, notification:NotificationItems) {
        databaseRef.child("Users").child(userId).child("OrderHistory").child(orderDetails.itemPushKey!!).setValue(orderDetails)
        val uniqueKey = databaseRef.child("Users").child(userId).child("Notifications").push().key
        databaseRef.child("Users").child(userId).child("Notifications").child(uniqueKey!!).setValue(notification)
    }

    private fun removeItemFromCart() {
        val cartItemsRef = databaseRef.child("Users").child(userId).child("CartItems")
        cartItemsRef.removeValue()
    }

    private fun calculateTotal(): String {
        var totalAmount = 0
        for (i in 0 until foodPrices.size){
            var price = foodPrices[i]
            val priceIntValue = price.toInt()
            var quantity = foodQuantity[i]
            totalAmount += priceIntValue *quantity
        }
        return "₹$totalAmount"
    }

    private fun setUserData() {
        val user = auth.currentUser
        if(user!=null){
            val userId = user.uid
            val userRef = databaseRef.child("Users").child(userId)

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
                    Toast.makeText(this@pay_out, "Check internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}