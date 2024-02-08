package com.example.foodapp

import android.content.Context
import android.content.SharedPreferences

class SharedPref {
    companion object {
        private const val PREF_NAME = "sharedcheckLogin"
        private const val UserType = "User"
        private const val UserLocation = "null"
        private const val isUserLogined = "false"
        private lateinit var sharedPreferences: SharedPreferences

        fun initialize(context: Context) {
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        }

        fun setUserType(name: String) {
            val editor = sharedPreferences.edit()
            editor.putString(UserType, name)
            editor.apply()
        }

        fun getUserType(): String? {
            return sharedPreferences.getString(UserType, null)
        }

        fun setUserLocation(name: String) {
            val editor = sharedPreferences.edit()
            editor.putString(UserLocation, name)
            editor.apply()
        }

        fun isLogin(): Boolean?{
            return sharedPreferences.getBoolean(isUserLogined, false)
        }
        fun setLogin() {
            val editor = sharedPreferences.edit()
            editor.putBoolean(isUserLogined, true)
            editor.apply()
        }

        fun getUserLocation(): String? {
            return sharedPreferences.getString(UserLocation, null)
        }

        fun logoutUser(context:Context) {
            val editor = sharedPreferences.edit()
            editor.clear()
        }
    }
}