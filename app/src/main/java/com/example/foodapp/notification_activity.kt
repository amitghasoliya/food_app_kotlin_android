package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.databinding.ActivityNotificationBinding
import com.example.foodapp.model.NotificationItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class notification_activity : AppCompatActivity() {
    private lateinit var binding : ActivityNotificationBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var databaseRef: DatabaseReference
    private var notiList: ArrayList<NotificationItems> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference

        binding.apply {
            setSupportActionBar(notificationToolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setTitle(null)
            notificationToolbar.setNavigationOnClickListener {
                finish()
            }
        }

        binding.swipeRefreshNotifications.setOnRefreshListener {
            notiList.clear()
            loadNotifications()
            binding.swipeRefreshNotifications.isRefreshing = false
        }
        loadNotifications()
    }

    private fun loadNotifications() {
        binding.noNotifications.visibility = View.VISIBLE
        val userId = auth.currentUser?.uid!!
        val notiRef = databaseRef.child("Users").child(userId).child("Notifications").orderByChild("time")
        notiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (Snapshot in snapshot.children){
                    val item = Snapshot.getValue(NotificationItems::class.java)!!
                    item.let {
                        binding.noNotifications.visibility = View.INVISIBLE
                        notiList.add(it) }
                }
                notiList.reverse()
                setAdapter()
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun setAdapter() {
        val adapter = notificationAdapter(notiList)
        binding.notificationRecycler.adapter = adapter
        binding.notificationRecycler.layoutManager = LinearLayoutManager(this)
    }
}