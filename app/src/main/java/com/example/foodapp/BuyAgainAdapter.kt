package com.example.foodapp

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.foodapp.databinding.BuyAgainItemViewBinding
import com.example.foodapp.model.OrderDetail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class BuyAgainAdapter(private val buyAgainList: MutableList<OrderDetail>, private val context: Context):
    RecyclerView.Adapter<BuyAgainAdapter.BuyAgainViewHolder>() {

    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val userId = auth.currentUser?.uid!!

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainViewHolder {
        val binding = BuyAgainItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BuyAgainViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return buyAgainList.size
    }

    override fun onBindViewHolder(holder: BuyAgainViewHolder, position: Int) {
        holder.bind(buyAgainList[position],position,context)
    }

    inner class BuyAgainViewHolder(private val binding: BuyAgainItemViewBinding): RecyclerView.ViewHolder(binding.root){
        init {
            binding.recievedButton.setOnClickListener {
                binding.orderStatusHistory.text = "Delivered"
                binding.orderStatusHistory.setTextColor(context.resources.getColor(R.color.green, context.theme))
                binding.recievedButton.visibility = View.INVISIBLE

                val itemPushKey = buyAgainList[adapterPosition].itemPushKey!!
                val RestId = buyAgainList[adapterPosition].restaurantId!!
                val orderHistoryRef = database.reference.child("Users").child(userId).child("OrderHistory").child(itemPushKey)
                val completeOrderRef= database.reference.child("Admin").child(RestId).child("CompletedOrders").child(itemPushKey)
                orderHistoryRef.child("paymentReceived").setValue(true)
                completeOrderRef.child("paymentReceived").setValue(true)
            }

            binding.buyAgainFoodButton.setOnClickListener {
                val intent = Intent(context, pay_out::class.java)
                intent.putExtra("foodName", buyAgainList[adapterPosition].foodNames as ArrayList<String>)
                intent.putExtra("foodPrice", buyAgainList[adapterPosition].foodPrices as ArrayList<String>)
                intent.putExtra("foodImage", buyAgainList[adapterPosition].foodImages as ArrayList<String>)
                intent.putExtra("foodQuantities", buyAgainList[adapterPosition].foodQuantities as ArrayList<Int>)
                context.startActivity(intent)
            }
        }
        fun bind(orderDetail: OrderDetail,position: Int, context: Context) {
            binding.apply {
                buyAgainFoodName.text = "${ orderDetail.foodNames?.joinToString("\n")}"
                buyAgainFoodQuantity.text = "${ orderDetail.foodQuantities?.joinToString("\n"){
                    "${it} ×"
                }}"
                totolPrice.text = orderDetail.totalPrice
                restName.text = buyAgainList[position].restaurantName
                if (orderDetail.foodNames!!.size>4){
                    val size = orderDetail.foodNames!!.size
                    buyAgainFoodName.text = "${ orderDetail.foodNames?.dropLast(size-3)?.joinToString("\n")} \n${orderDetail.foodNames!!.get(3).substringBefore(" ")}.. & more"
                }
                if (orderDetail.orderAccepted ==true){
                    orderStatusHistory.text = "Order Preparing.."
                    orderStatusHistory.setTextColor(context.resources.getColor(R.color.Primary, context.theme))
                    buyAgainFoodButton.visibility = View.INVISIBLE
                    totolPrice.visibility = View.INVISIBLE
                }
                else{
                    restName.text = "Order not accepted!"
                    orderStatusHistory.text = "Order Pending"
                    orderStatusHistory.setTextColor(Color.BLACK)
                    buyAgainFoodButton.visibility = View.INVISIBLE
                    totolPrice.visibility = View.INVISIBLE
                }
                if (orderDetail.orderDispatched==true){
                    orderStatusHistory.text = "On the way.."
                    orderStatusHistory.setTextColor(context.resources.getColor(R.color.green, context.theme))
                    recievedButton.visibility = View.VISIBLE
                    totolPrice.visibility = View.INVISIBLE
                    buyAgainFoodButton.visibility = View.INVISIBLE
                }
                if (orderDetail.paymentReceived == true){
                    orderStatusHistory.text = "Delivered"
                    orderStatusHistory.setTextColor(context.resources.getColor(R.color.green, context.theme))
                    recievedButton.visibility = View.INVISIBLE
                    totolPrice.visibility = View.VISIBLE
                    buyAgainFoodButton.visibility = View.VISIBLE
                }
                Glide.with(context).load(Uri.parse(buyAgainList[position].foodImages!![0])).into(binding.buyAgainFoodImage)
            }

        }

    }
}