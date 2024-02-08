package com.example.foodapp.model

data class CartItems(
    var foodName: String ?= null,
    var foodPrice: String ?= null,
    var foodImage: String ?= null,
    var foodQuantity: Int ?= null
)
