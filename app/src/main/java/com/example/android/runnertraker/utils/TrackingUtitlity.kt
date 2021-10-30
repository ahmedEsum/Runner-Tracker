package com.example.android.runnertraker.utils

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.widget.EditText
import com.example.android.runnertraker.services.polyline
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.TimeUnit
import android.net.ConnectivityManager
import android.provider.Settings
import android.widget.Toast
import timber.log.Timber


object TrackingUtility {

    fun hasPermissionLocation(context: Context) =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.hasPermissions(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }


    fun getTimeFormatted(ms: Long, includeMillis: Boolean = false): String {
        var millisTime = ms
        val hours = TimeUnit.MILLISECONDS.toHours(millisTime)
        millisTime -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisTime)
        millisTime -= TimeUnit.MINUTES.toMillis(minutes)
        val secs = TimeUnit.MILLISECONDS.toSeconds(millisTime)
        if (!includeMillis) {
            return "${if (hours < 10) "0" else ""}$hours:"+ "${if (minutes < 10) "0" else ""}$minutes:"+ "${if (secs < 10) "0" else ""}$secs"
        }
        millisTime -= TimeUnit.SECONDS.toMillis(secs)
        millisTime /= 10
        return "${if (hours < 10) "0" else ""}$hours:" + "${if (minutes < 10) "0" else ""}$minutes:" + "${if (secs < 10) "0" else ""}$secs:" + "${if (millisTime < 10) "0" else ""}$millisTime"

    }


    fun getDistanceForRun (polyline: polyline):Float{
        var resultDistance=0f
        for (i in 0..polyline.size-2){
            val fArray=FloatArray(1)
            val pos1 =polyline[i]
            val pos2 =polyline[i+1]

            Location.distanceBetween(pos1.latitude,pos1.longitude,pos2.latitude,pos2.longitude,fArray)
            resultDistance+=fArray[0]
        }
        return resultDistance
    }


    fun checkInputs(etName:EditText , etWeight:EditText , sharedPreferences:SharedPreferences): Boolean {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if (name.isEmpty() && weight.isEmpty()) {
            etName.error = "fill information please"
            etWeight.error = "fill information please"
            return false
        }
        sharedPreferences.edit().putString(Constants.NAME_PREF, name).putFloat(Constants.WEIGHT_PREF, weight.toFloat())
            .putBoolean(
                Constants.FIRST_TIME_TOGGLE, false
            ).apply()
        return true
    }

    fun isInternetConnected(getApplicationContext: Context): Boolean {
        var status = false
        val cm =
            getApplicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (cm.activeNetwork != null && cm.getNetworkCapabilities(cm.activeNetwork) != null) {
                // connected to the internet
                status = true
            }
        } else {
            if (cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnectedOrConnecting) {
                // connected to the internet
                status = true
            }
        }
        if (!status){
            Toast.makeText(getApplicationContext,"please enable your internet ",Toast.LENGTH_SHORT).show()
        }
        return status
    }

     fun checkInternetConnectionAndGps (context: Context):Boolean{
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var isGpsEnabled = false
        try {
            isGpsEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        }catch (e:Exception){
            Timber.d("gps is not enabled ")
        }

        if (!isGpsEnabled){
            AlertDialog.Builder(context)
                .setTitle("Gps is not enabled ")
                .setMessage("Gps is not enabled ... you can use app without enable it ")
                .setPositiveButton("Enable"){_,_,->
                    context.startActivity(Intent(Settings. ACTION_LOCATION_SOURCE_SETTINGS ))
                }
                .setNegativeButton("NO"){interfaceListener,_,->
                    interfaceListener.cancel()
                }.setCancelable(true)
                .show()
        }
        return isGpsEnabled
    }
}