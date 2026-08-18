package com.vyuvancollectors.Activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.provider.Settings
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.vyuvancollectors.R
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

fun checkLocationSettings(context: Context, onResult: (Boolean) -> Unit) {
    val client = LocationServices.getSettingsClient(context)
    val locationRequest = com.google.android.gms.location.LocationRequest.create().apply {
        priority = com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    val builder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)

    val task = client.checkLocationSettings(builder.build())

    task.addOnSuccessListener {
        // Location settings are satisfied
        onResult(true)
    }

    task.addOnFailureListener { exception ->
        if (exception is ApiException) {
            when (exception.statusCode) {
                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    val builder = AlertDialog.Builder(context)
                    builder.setIcon(R.drawable.logo)
                    builder.setTitle("GPS Settings")
                    builder.setMessage("GPS Location is off. Please go to settings menu and enable your location")

                    builder.setPositiveButton("Settings") { dialog, _ ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        context.startActivity(intent)
                    }

                    builder.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }

                    val alert = builder.create()
                    alert.show()

                    val pButton = alert.getButton(DialogInterface.BUTTON_POSITIVE)
                    pButton.setTextColor(Color.WHITE)
                    pButton.gravity = Gravity.CENTER

                    val nButton = alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                    nButton.setTextColor(Color.WHITE)
                    nButton.gravity = Gravity.CENTER
                    // Location settings are not satisfied, but this can be fixed by showing the user a dialog
                    onResult(false)
                }
                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    // Location settings are not satisfied and cannot be fixed here
                    onResult(false)
                }
            }
        }
    }

    fun showSettingsAlert() {

    }
}