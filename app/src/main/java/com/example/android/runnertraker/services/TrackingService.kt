package com.example.android.runnertraker.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.*
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getService
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.example.android.runnertraker.R
import com.example.android.runnertraker.utils.Constants.FASTEST_INTERVAL_TIME
import com.example.android.runnertraker.utils.Constants.INTERVAL_TIME
import com.example.android.runnertraker.utils.Constants.NOTIFICATION_ID
import com.example.android.runnertraker.utils.Constants.NOTIFICATION_ID_Foreground
import com.example.android.runnertraker.utils.Constants.NOTIFICATION_NAME
import com.example.android.runnertraker.utils.Constants.PAUSE_SERVICE
import com.example.android.runnertraker.utils.Constants.START_OR_RESUME_SERVICE
import com.example.android.runnertraker.utils.Constants.STOP_SERVICE
import com.example.android.runnertraker.utils.Constants.TIMER_DELAY
import com.example.android.runnertraker.utils.TrackingUtility
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

typealias polyline = MutableList<LatLng>
typealias polylines = MutableList<polyline>

@AndroidEntryPoint
class TrackingService : LifecycleService() {
    @Inject
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient //to get user current location
    @Inject
    lateinit var notificationBase:NotificationCompat.Builder
    private val trackingTimeInSecs =
        MutableLiveData<Long>()// to update foreground service timer text notification
    private var isFirstRun = true //to manage the pause and start or resume run
    private var stopServiceAction = false //to kill all process of this service

    private lateinit var currentNotificationBase:NotificationCompat.Builder
    //    private var isTimerEnabled = false //to manage timer control
    private var trackingTimeStarted = 0L //first time of the tracking
    private var trackingTimeLap =
        0L //tracking time per lap. if user paused the lap then this will be the time for it and it will be reset
    private var totalTrackingTime = 0L // the whole tracking time for the run
    private var timePerSecCounter = 0L // to check if one sec passed to updated "trackingTimeInSecs"

    companion object {
        val isTrackingMTD = MutableLiveData<Boolean>()//to manage tracking operation
        val pathPoints = MutableLiveData<polylines>()
        val trackingTimeInMillis = MutableLiveData<Long>()//to update run time

    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTrackingMTD.value!!) {
                result.locations.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
                        Timber.d("latitude is ${location.latitude} and longtiude is ${location.longitude}")
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        currentNotificationBase=notificationBase
        initiateObjects()
        isTrackingMTD.observe(this, {
            updateLocationTracking(it)
            updateTrackingNotification(it)
        })
    }
    private fun initiateObjects() {
        isTrackingMTD.postValue(false)
        pathPoints.postValue(mutableListOf())
        trackingTimeInMillis.postValue(0L)
        trackingTimeInSecs.postValue(0L)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                START_OR_RESUME_SERVICE -> {
                    if (isFirstRun) {
                        startForegroundService()
                        Timber.d("service has been started !")
                        isFirstRun = false
                    } else {
                        Timber.d("service has been resumed !")
                        startTrackingTimer()
                    }
                }
                PAUSE_SERVICE -> {
                    Timber.d("service has been paused !")
                    pauseService()
                }
                STOP_SERVICE -> {
                    Timber.d("service has been stopped !")
                    stopService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTrackingTimer() {
        addEmptyPolyline()
        isTrackingMTD.postValue(true)
        trackingTimeStarted = System.currentTimeMillis()

        CoroutineScope(Dispatchers.Main).launch {
            while (isTrackingMTD.value!!) {
                trackingTimeLap = System.currentTimeMillis() - trackingTimeStarted
                trackingTimeInMillis.postValue(totalTrackingTime + trackingTimeLap)
                if (trackingTimeInMillis.value!! >= timePerSecCounter + 1000L) {
                    trackingTimeInSecs.postValue(trackingTimeInSecs.value!! + 1)
                    timePerSecCounter += 1000L
                }

                delay(TIMER_DELAY)
            }
            totalTrackingTime += trackingTimeLap
        }
    }



    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (TrackingUtility.hasPermissionLocation(this)) {
                val requestLocation = LocationRequest.create().apply {
                    interval = INTERVAL_TIME
                    fastestInterval = FASTEST_INTERVAL_TIME
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    requestLocation, locationCallback,
                    Looper.getMainLooper()
                )

            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }


    private fun updateTrackingNotification(isTracking : Boolean){
        val notificationActionText = if (isTracking)"Pause" else "Resume"
        val pendingIntent=if (isTracking){
            val pauseIntent = Intent(this,TrackingService::class.java).apply {
                action= PAUSE_SERVICE
            }
            getService(this,1,pauseIntent,FLAG_UPDATE_CURRENT)
        }else {
            val resumeIntent = Intent(this,TrackingService::class.java).apply {
                action= START_OR_RESUME_SERVICE
            }
            getService(this,2,resumeIntent, FLAG_UPDATE_CURRENT)
        }

        val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        currentNotificationBase.javaClass.getDeclaredField("mActions").apply {
            isAccessible=true
            set(currentNotificationBase,ArrayList<NotificationCompat.Action>())
        }
        if (!stopServiceAction){
            currentNotificationBase=notificationBase.addAction(R.drawable.ic_baseline_pause_24,
                notificationActionText,pendingIntent)
            notificationManager.notify(NOTIFICATION_ID_Foreground,currentNotificationBase.build())
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationManagerForAndroidQ(notificationManager: NotificationManager) {
        val notificationChannel =
            NotificationChannel(NOTIFICATION_ID, NOTIFICATION_NAME, IMPORTANCE_MIN)
        notificationManager.createNotificationChannel(notificationChannel)
    }


    private fun startForegroundService() {
        startTrackingTimer()
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationManagerForAndroidQ(notificationManager)
        }

            startForeground(NOTIFICATION_ID_Foreground, notificationBase.build())
            trackingTimeInSecs.observe(this, {
                if (!stopServiceAction) {
                val notification =
                    notificationBase.setContentText(TrackingUtility.getTimeFormatted(it * 1000L))
                notificationManager.notify(NOTIFICATION_ID_Foreground, notification.build())
                }
            })

    }


    private fun pauseService() {
        isTrackingMTD.postValue(false)
    }

    private fun stopService(){
        stopServiceAction=true
        isFirstRun=true
        pauseService()
        stopForeground(true)
        initiateObjects()
        stopSelf()
    }
}