package com.example.foodapp.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class OrderDetail() : Parcelable{
    var userUid: String?= null
    var userName: String?= null
    var foodNames: MutableList<String>?= null
    var foodPrices: MutableList<String>?= null
    var foodImages: MutableList<String>?= null
    var foodQuantities: MutableList<Int>?= null
    var address: String?= null
    var totalPrice: String?= null
    var phoneNumber: String?= null
    var orderAccepted: Boolean?= false
    var paymentReceived: Boolean?= false
    var itemPushKey: String ?= null
    var currentTime: Long = 0

    constructor(parcel: Parcel) : this() {
        userUid = parcel.readString()
        userName = parcel.readString()
        address = parcel.readString()
        totalPrice = parcel.readString()
        phoneNumber = parcel.readString()
        orderAccepted = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        paymentReceived = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        itemPushKey = parcel.readString()
        currentTime = parcel.readLong()
    }

    constructor(
        userId: String,
        name: String,
        foodNames: ArrayList<String>,
        foodPrices: ArrayList<String>,
        foodImagesUri: ArrayList<String>,
        foodQuantity: ArrayList<Int>,
        address: String,
        phone: String,
        totalAmount: String,
        b: Boolean,
        b1: Boolean,
        itemPushKey: String?,
        time: Long
    ): this(){
        this.userUid = userId
        this.userName = name
        this.foodNames = foodNames
        this.foodPrices = foodPrices
        this.foodImages = foodImagesUri
        this.foodQuantities = foodQuantity
        this.address = address
        this.phoneNumber = phone
        this.totalPrice = totalAmount
        this.orderAccepted = b
        this.paymentReceived = b1
        this.itemPushKey = itemPushKey
        this.currentTime = time
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userUid)
        parcel.writeString(userName)
        parcel.writeString(address)
        parcel.writeString(totalPrice)
        parcel.writeString(phoneNumber)
        parcel.writeValue(orderAccepted)
        parcel.writeValue(paymentReceived)
        parcel.writeString(itemPushKey)
        parcel.writeLong(currentTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<OrderDetail> {
        override fun createFromParcel(parcel: Parcel): OrderDetail {
            return OrderDetail(parcel)
        }

        override fun newArray(size: Int): Array<OrderDetail?> {
            return arrayOfNulls(size)
        }
    }

}
