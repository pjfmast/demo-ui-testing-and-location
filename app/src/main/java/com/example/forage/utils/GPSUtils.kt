package com.example.forage.utils

import android.Manifest
import android.annotation.SuppressLint
import android.location.LocationManager.*
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.widget.Toast
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.appcompat.app.AlertDialog


object GPSUtils {

    var longitude: Double? = null
        private set
    var latitude: Double? = null
        private set

    private const val REQUEST_LOCATION = 1

    private const val coarse = Manifest.permission.ACCESS_COARSE_LOCATION
    private const val fine = Manifest.permission.ACCESS_FINE_LOCATION
    private val permissions = arrayOf(coarse, fine)

    private lateinit var locationManager: LocationManager

    fun initPermissions(activity: Activity) {
        ActivityCompat.requestPermissions(activity, permissions, REQUEST_LOCATION)
    }

    fun findDeviceLocation(activity: Activity) {
        locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!locationManager.isProviderEnabled(GPS_PROVIDER)) {
            enableGps(activity)
        } else {
            getLocation(activity)
        }
    }

    private fun enableGps(activity: Activity) =
        AlertDialog.Builder(activity).setMessage("Enable GPS").setCancelable(false)
            .setPositiveButton("YES") { _, _ ->
                activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.cancel()
            }
            .create()
            .show()

    @SuppressLint("MissingPermission")
    private fun getLocation(activity: Activity) {
        if (!activity.hasPermission(coarse) && !activity.hasPermission(fine)) {
            initPermissions(activity)
        }

        val location = listOf(GPS_PROVIDER, NETWORK_PROVIDER, PASSIVE_PROVIDER)
            .map { locationManager.getLastKnownLocation(it) }
            .firstOrNull()

        when (location) {
            null -> Toast.makeText(activity, "Cannot determine location", Toast.LENGTH_SHORT).show()
            else -> {
                latitude = location.latitude
                longitude = location.longitude
            }
        }
    }

    private fun Activity.hasPermission(permission: String) =
        ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}