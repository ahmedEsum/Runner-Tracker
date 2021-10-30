package com.example.android.runnertraker.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_table")
data class RunModel(
    var bitmap: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeedKMH: Float = 0f,
    var distanceInMeter: Int = 0,
    var timeInMills: Long = 0L,
    var caloriesBurned: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}