package com.example.foodapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.foodapp.MenuAdapter
import com.example.foodapp.R
import com.example.foodapp.SearchActivity
import com.example.foodapp.SharedPref
import com.example.foodapp.databinding.FragmentHomeBinding
import com.example.foodapp.menu
import com.example.foodapp.model.MenuItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database : FirebaseDatabase
    private lateinit var menuItems : MutableList<MenuItems>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.banner_1, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner_2, ScaleTypes.FIT))
        imageList.add(SlideModel(R.drawable.banner_3, ScaleTypes.FIT))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.FIT)

        imageSlider.setItemClickListener(object:ItemClickListener{
            override fun doubleClick(position: Int) {
                TODO("Not yet implemented")
            }
            override fun onItemSelected(position: Int) {
                val itemPosition = imageList[position]
                val itemMessage= "Selected Image $position"
//                Toast.makeText(requireContext(),itemMessage, Toast.LENGTH_SHORT).show()
            }
        })
        binding.viewMenu.setOnClickListener {
            startActivity(Intent(requireContext(), menu::class.java))
        }

        binding.searchView.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        retrievePopularItems()
        return binding.root
    }

    private fun retrievePopularItems() {
        SharedPref.initialize(requireContext())
        val cityName = SharedPref.getUserLocation().toString()

        database = FirebaseDatabase.getInstance()
        val foodRef = database.reference.child(cityName).child("Menu").orderByChild("orderCount")
        menuItems = mutableListOf()

        foodRef.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children){
                    val menuItem = foodSnapshot.getValue(MenuItems::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Check network connection", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setAdapter() {
        val index = menuItems.indices.reversed()
        val numItemToShow = 6
        val subsetMenuItems = index.take(numItemToShow).map { menuItems[it] }

        val dividerItemDecoration = DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.line)!!)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)

        val adapter = MenuAdapter(subsetMenuItems,requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

}