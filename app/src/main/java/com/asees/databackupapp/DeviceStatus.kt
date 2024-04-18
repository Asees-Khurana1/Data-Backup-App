package com.asees.databackupapp

import android.content.Context
import android.os.BatteryManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi

object DeviceStatusUtils {

    @RequiresApi(Build.VERSION_CODES.M)
    fun hasEnoughBattery(context: Context): Boolean {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryPct = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val isCharging = batteryManager.isCharging

        return batteryPct > 30 || isCharging  // Consider enough if battery is above 30% or if charging
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}
