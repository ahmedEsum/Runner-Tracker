package com.example.android.runnertraker.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.runnertraker.repository.RunRepository
import com.example.android.runnertraker.model.RunModel
import com.example.android.runnertraker.utils.FilterConstant
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RunViewModel @Inject constructor(private val repository: RunRepository) : ViewModel() {
    var filterBy = FilterConstant.DATE
    var allRunLiveData = MediatorLiveData<List<RunModel>>()

    private var runsSortedByDate = getAllRunsFilteredBy(FilterConstant.DATE)
    private var runsSortedByDistance = getAllRunsFilteredBy(FilterConstant.DISTANCE)
    private var runsSortedByTime = getAllRunsFilteredBy(FilterConstant.TIME_IN_MILLS)
    private var runsSortedBySpeed = getAllRunsFilteredBy(FilterConstant.SPEED)
    private var runsSortedByCalories = getAllRunsFilteredBy(FilterConstant.CALORIES)

    init {
        allRunLiveData.addSource(runsSortedByDate) { result ->
            if (filterBy == FilterConstant.DATE) {
                result?.let {
                    allRunLiveData.value = it
                }
            }
        }
        allRunLiveData.addSource(runsSortedByTime) { result ->
            if (filterBy == FilterConstant.TIME_IN_MILLS) {
                result?.let {
                    allRunLiveData.value = it
                }
            }
        }
        allRunLiveData.addSource(runsSortedByDistance) { result ->
            if (filterBy == FilterConstant.DISTANCE) {
                result?.let {
                    allRunLiveData.value = it
                }
            }
        }
        allRunLiveData.addSource(runsSortedBySpeed) { result ->
            if (filterBy == FilterConstant.SPEED) {
                result?.let {
                    allRunLiveData.value = it
                }
            }
        }
        allRunLiveData.addSource(runsSortedByCalories) { result ->
            if (filterBy == FilterConstant.CALORIES) {
                result?.let {
                    allRunLiveData.value = it
                }
            }
        }
    }

    private fun getAllRunsFilteredBy(columnName: FilterConstant) =
        repository.getAllRunsFilteredBy(columnName.s)

    fun insertRun(run: RunModel) = viewModelScope.launch {
        repository.insertRun(run)
    }

    fun deleteRun(run: RunModel) = viewModelScope.launch {
        repository.deleteRun(run)
    }

    fun sortBy(columnName: FilterConstant) = when (columnName) {
        FilterConstant.DATE -> runsSortedByDate.value?.let {
            allRunLiveData.value = it
        }
        FilterConstant.DISTANCE -> runsSortedByDistance.value?.let {
            allRunLiveData.value = it
        }
        FilterConstant.SPEED -> runsSortedBySpeed.value?.let {
            allRunLiveData.value = it
        }
        FilterConstant.CALORIES -> runsSortedByCalories.value?.let {
            allRunLiveData.value = it
        }
        FilterConstant.TIME_IN_MILLS -> runsSortedByTime.value?.let {
            allRunLiveData.value = it
        }

    }.also {
        filterBy = columnName
    }
}