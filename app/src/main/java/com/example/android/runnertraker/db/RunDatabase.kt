package com.example.android.runnertraker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android.runnertraker.model.RunModel


@Database(entities = [RunModel::class],version = 1)
@TypeConverters(Converters::class)
abstract class RunDatabase:RoomDatabase() {
    abstract fun getRunDao():RunDao
}