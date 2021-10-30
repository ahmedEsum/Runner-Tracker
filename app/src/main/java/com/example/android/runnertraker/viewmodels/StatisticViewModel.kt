package com.example.android.runnertraker.viewmodels

import androidx.lifecycle.ViewModel
import com.example.android.runnertraker.repository.RunRepository
import com.example.android.runnertraker.utils.FilterConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatisticViewModel @Inject constructor(private val repository: RunRepository) : ViewModel() {

    fun getTotalTime() = repository.getTotalTime()
    fun getTotalAvgSpeed() = repository.getTotalAvgSpeed()
    fun getTotalCalories() = repository.getTotalCalories()
    fun getTotalDistance() = repository.getTotalDistance()

    fun getAllRunsByDate()=repository.getAllRunsFilteredBy(FilterConstant.DATE.s)

}

