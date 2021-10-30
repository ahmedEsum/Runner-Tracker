package com.example.android.runnertraker.di

import android.app.PendingIntent
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import androidx.core.app.NotificationCompat
import com.example.android.runnertraker.R
import com.example.android.runnertraker.ui.MainActivity
import com.example.android.runnertraker.utils.Constants
import com.example.android.runnertraker.utils.Constants.FIRST_TIME_TOGGLE
import com.example.android.runnertraker.utils.Constants.NAME_PREF
import com.example.android.runnertraker.utils.Constants.SHARED_PREFERENCES_NAME
import com.example.android.runnertraker.utils.Constants.WEIGHT_PREF
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import javax.inject.Singleton

@Module
@InstallIn(ServiceComponent::class)
object ServiceProvider {
    @ServiceScoped
    @Provides
    fun getFusedLocationProvide(
        @ApplicationContext app: Context
    ) = FusedLocationProviderClient(app)


    @ServiceScoped
    @Provides
    fun getPendingIntent(
        @ApplicationContext app: Context
    ): PendingIntent =
        PendingIntent.getActivity(app, 0, Intent(app, MainActivity::class.java).also {
            it.action = Constants.START_TRACKING_FRAGMENT
        }, PendingIntent.FLAG_UPDATE_CURRENT)


    @ServiceScoped
    @Provides
    fun getNotificationBuilder(
        @ApplicationContext app: Context,
        pendingIntent: PendingIntent
    ) = NotificationCompat.Builder(app, Constants.NOTIFICATION_ID).setAutoCancel(false)
        .setOngoing(true)
        .setSmallIcon(
            R.drawable.ic_baseline_run_circle_24
        ).setContentText("Running App").setContentText("00:00:00")
        .setContentIntent(pendingIntent)


}