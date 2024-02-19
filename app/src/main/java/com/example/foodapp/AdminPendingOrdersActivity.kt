package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.foodapp.databinding.ActivityAdminPendingOrdersBinding
import com.google.android.material.tabs.TabLayout

class AdminPendingOrdersActivity : AppCompatActivity(){
    private lateinit var binding : ActivityAdminPendingOrdersBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightGrey)
        super.onCreate(savedInstanceState)
        binding = ActivityAdminPendingOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            setSupportActionBar(orderDashboardToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(null)
            orderDashboardToolbar.setNavigationOnClickListener {
                finish()
            }

            tabLayout.addTab(tabLayout.newTab().setText("Pending Orders"))
            tabLayout.addTab(tabLayout.newTab().setText("Accepted Orders"))
            val adapter = ViewpagerAdapter(supportFragmentManager, lifecycle)
            viewPager.adapter = adapter
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {
                }
                override fun onTabReselected(tab: TabLayout.Tab) {
                }
            })

            viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    tabLayout.selectTab(tabLayout.getTabAt(position))
                }
            })
        }

    }

//    private fun showAcceptedOrders() {
//        val databaseRe = database.reference.child("Admin").child(userId.toString()).child("PendingOrders")
//        databaseRe.addListenerForSingleValueEvent(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for(orderSnapshot in snapshot.children){
//                    val orderDetails = orderSnapshot.getValue(OrderDetail::class.java)
//                    orderDetails?.let { if (orderDetails.orderAccepted==true){
//                        listOfOrderItems.add(it)
//                    } }
//                }
//                addDataToList()
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//    }

//    private fun getOrderDetails() {
//        databaseRef.addListenerForSingleValueEvent(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for(orderSnapshot in snapshot.children){
//                    val orderDetails = orderSnapshot.getValue(OrderDetail::class.java)
//                    orderDetails?.let { listOfOrderItems.add(it) }
//                }
//                addDataToList()
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//    }

//    private fun addDataToList() {
//        for(orderItem in listOfOrderItems){
//            orderItem.userName?.let { listOfFoodNames.add(it) }
//            orderItem.totalPrice?.let { listOfFoodPrices.add(it) }
//            orderItem.itemPushKey?.let { listOfPushKey.add(it) }
//            orderItem.orderAccepted?.let { listOfOrderAccepted.add(it) }
//            orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach{
//                listOfFoodImages.add(it)
//            }
//        }
//        setAdapter()
//    }

//    private fun setAdapter() {
//        val adapter = AdminPendingOrderAdapter(listOfFoodNames,listOfFoodPrices, listOfFoodImages,listOfPushKey,listOfOrderAccepted,this,this)
//        binding.recyclerPendingOrdersAdmin.layoutManager = LinearLayoutManager(this)
//        binding.recyclerPendingOrdersAdmin.adapter = adapter
//    }
//
//    override fun onItemAcceptClickListener(position: Int) {
//        updateOrderAcceptStatus(position)
//    }
//
//    override fun onItemDispatchClickListener(position: Int) {
//        val dispatchItemPushKey = listOfPushKey[position]
//        val dispatchItemOrderRef = database.reference.child("CompletedOrder").child(dispatchItemPushKey)
//        dispatchItemOrderRef.setValue(listOfOrderItems[position])
//            .addOnSuccessListener { deleteThisItemFromOrderDetails(dispatchItemPushKey) }
//    }
//
//    private fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String){
//        val orderDetailsItemRef = database.reference.child("admin").child(userId.toString()).child("PendingOrders").child(dispatchItemPushKey)
//        orderDetailsItemRef.removeValue()
//            .addOnSuccessListener {
//                Toast.makeText(this, "Order is dispatched", Toast.LENGTH_SHORT).show()
//            }
//    }
//
//    private fun updateOrderAcceptStatus(position: Int) {
//        val userIdOfClickedItem = listOfOrderItems[position].userUid
//        val pushKeyOfClickedItem = listOfPushKey[position]
//        val orderHistoryRef = database.reference.child("Users").child(userIdOfClickedItem!!).child("OrderHistory").child(pushKeyOfClickedItem!!)
//        orderHistoryRef.child("orderAccepted").setValue(true)
////        databaseRef.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
//    }
}