package com.example.android.runnertraker.utils

import android.graphics.Color

object Constants {
    //actions
    const val START_OR_RESUME_SERVICE = "StartServiceOrResume"
    const val PAUSE_SERVICE = "PAUSE_SERVICE"
    const val STOP_SERVICE = "STOP_SERVICE"
    const val START_TRACKING_FRAGMENT = "StartTrackingFragment"

    // notification channel properties
    const val NOTIFICATION_ID = "TrackingNotificationID"
    const val NOTIFICATION_NAME= "TrackingNotification"
    //notification id
    const val NOTIFICATION_ID_Foreground= 1

    //request location properties
    const val INTERVAL_TIME=5000L
    const val FASTEST_INTERVAL_TIME=2000L
    //easy permissions request
    const val REQUEST_CODE = 0
    //polyline properties
    const val WIDTH =8f
    const val COLOR =Color.RED
    //camera speed animation
    const val CAMERA_SPEED=16f
    //timer update delay
    const val TIMER_DELAY=50L

    //SharedPreferences properties
    const val SHARED_PREFERENCES_NAME="Shared_pref"
    const val FIRST_TIME_TOGGLE="first_time_toggle"
    const val NAME_PREF="name_pref"
    const val WEIGHT_PREF="weight_pref"

}