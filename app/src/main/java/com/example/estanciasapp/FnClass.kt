package com.example.estanciasapp

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.Gravity
import android.widget.TextView
import android.widget.Toast


class FnClass {

    fun haveNetwork(context:Context):Boolean{
        val connectivityManager=context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities=connectivityManager.activeNetwork?: return false
        val networkInfo=connectivityManager.getNetworkCapabilities(networkCapabilities)?: return false

        return networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)||networkInfo.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }

}