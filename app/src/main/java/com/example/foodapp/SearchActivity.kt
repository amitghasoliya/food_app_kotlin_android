package com.example.foodapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.databinding.ActivitySearchBinding
import com.example.foodapp.model.MenuItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchActivity : AppCompatActivity(){
    private lateinit var binding: ActivitySearchBinding
    private lateinit var adapter: MenuAdapter
    private lateinit var database: FirebaseDatabase
    private val originalMenuItems = mutableListOf<MenuItems>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.statusBarColor = ContextCompat.getColor(this, R.color.lightGrey)
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchView.isIconified = false
        retrieveMenuItem()
        setupSearchView()
    }

    private fun retrieveMenuItem() {
        SharedPref.initialize(this)
        val cityName = SharedPref.getUserLocation().toString()

        database = FirebaseDatabase.getInstance()
        val foodRef = database.reference.child(cityName).child("Menu")
        foodRef.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItems::class.java)
                    menuItem?.let { originalMenuItems.add(it) }
                }
                showAllMenu()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SearchActivity, error.toString(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showAllMenu() {
        val filteredMenuItem = ArrayList(originalMenuItems)
        setAdapter(filteredMenuItem)
    }

    private fun setAdapter(filteredMenuItem: List<MenuItems>) {
        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.line)!!)
        binding.recyclerSearch.addItemDecoration(dividerItemDecoration)

        adapter = MenuAdapter(filteredMenuItem,this)
        binding.recyclerSearch.layoutManager = LinearLayoutManager(this)
        binding.recyclerSearch.adapter = adapter
    }

    private fun setupSearchView(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String): Boolean {
                filterMenuItems(query)
                return true
            }
            override fun onQueryTextChange(newText: String): Boolean {
                filterMenuItems(newText)
                return true
            }
        })
    }

    private fun filterMenuItems(query: String){
        val filteredMenuItems = originalMenuItems.filter {
            it.foodName?.contains(query, ignoreCase = true) == true
        }
        setAdapter(filteredMenuItems)
    }

}