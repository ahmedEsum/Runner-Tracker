package com.example.android.runnertraker.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.example.android.runnertraker.model.RunModel

@Dao
interface RunDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertRun(runModel: RunModel)

    @Delete
    suspend fun deleteRun(runModel: RunModel)

    @Query(
        """
        SELECT * FROM run_table
        ORDER BY 
        CASE WHEN :column = 'timestamp'  THEN timestamp END DESC,
        CASE WHEN :column = 'timeInMills' THEN timeInMills END DESC,
        CASE WHEN :column = 'calories' THEN caloriesBurned END DESC,
        CASE WHEN :column = 'speed'  THEN avgSpeedKMH END DESC,
        CASE WHEN :column = 'distance' THEN distanceInMeter END DESC
    """
    )
    fun getAllRunsFilteredBy(column: String): LiveData<List<RunModel>>

    @Query("SELECT SUM(timeInMills) FROM run_table")
    fun getTotalTime(): LiveData<Long>

    @Query("SELECT SUM(caloriesBurned) FROM run_table")
    fun getTotalCalories(): LiveData<Int>

    @Query("SELECT SUM(distanceInMeter) FROM run_table")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT SUM(avgSpeedKMH) FROM run_table")
    fun getTotalAvgSpeed(): LiveData<Float>


}