package com.example.android.runnertraker.utils

//room database columns
enum class FilterConstant( var s: String) {
    DATE("timestamp"),
    TIME_IN_MILLS("timeInMills"),
    CALORIES("calories"),
    SPEED("speed"),
    DISTANCE("distance")
}
