package com.example.foodapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isEmpty
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.foodapp.CartAdapter
import com.example.foodapp.R
import com.example.foodapp.databinding.FragmentCartBinding
import com.example.foodapp.pay_out

class CartFragment : Fragment() {
    private lateinit var binding : FragmentCartBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater,container,false)

        val cartFoodName = listOf("Burger","Sandwich","momo")
        val cartItemPrice = listOf("₹ 20","₹ 40","₹ 60")
        val cartImage = listOf(R.drawable.burger, R.drawable.sandwich,R.drawable.momos)
        val adapter = CartAdapter(ArrayList(cartFoodName),ArrayList(cartItemPrice),ArrayList(cartImage))
        binding.cartRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.cartRecycler.adapter = adapter

        binding.proceed.setOnClickListener {
            val intent = Intent(requireContext(),pay_out::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    companion object {
    }
}