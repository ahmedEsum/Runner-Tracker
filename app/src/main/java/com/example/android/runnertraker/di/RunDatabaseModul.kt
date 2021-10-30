package com.example.android.runnertraker.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.example.android.runnertraker.db.RunDatabase
import com.example.android.runnertraker.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppProviders {


    @Singleton
    @Provides
    fun getRunDatabase(@ApplicationContext app: Context) =
        Room.databaseBuilder(app, RunDatabase::class.java, "RunDatabase").build()


    @Singleton
    @Provides
    fun getRunDao (database: RunDatabase)=database.getRunDao()



    @Provides
    @Singleton
    fun getPreferences(@ApplicationContext app: Context): SharedPreferences =
        app.getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)


    @Provides
    @Singleton
    fun getName (sharedPreferences: SharedPreferences)=sharedPreferences.getString(Constants.NAME_PREF,"")?:""

    @Provides
    @Singleton
    fun getWeight (sharedPreferences: SharedPreferences)=sharedPreferences.getFloat(Constants.WEIGHT_PREF,0f)

    @Provides
    @Singleton
    fun getFirstToggle (sharedPreferences: SharedPreferences)=sharedPreferences.getBoolean(Constants.FIRST_TIME_TOGGLE,true)



}