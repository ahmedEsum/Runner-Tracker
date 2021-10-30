package com.example.android.runnertraker.repository

import com.example.android.runnertraker.db.RunDao
import com.example.android.runnertraker.model.RunModel
import javax.inject.Inject

class RunRepository @Inject constructor(private val database: RunDao) {

    suspend fun insertRun(run: RunModel) = database.insertRun(run)
    suspend fun deleteRun(run: RunModel) = database.deleteRun(run)
    fun getAllRunsFilteredBy(columnName: String) =
        database.getAllRunsFilteredBy(columnName)

    fun getTotalTime() = database.getTotalTime()
    fun getTotalCalories() = database.getTotalCalories()
    fun getTotalDistance() = database.getTotalDistance()
    fun getTotalAvgSpeed() = database.getTotalAvgSpeed()

}