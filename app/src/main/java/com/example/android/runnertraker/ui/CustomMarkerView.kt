package com.example.android.runnertraker.ui

import android.content.Context
import android.widget.TextView
import com.example.android.runnertraker.R
import com.example.android.runnertraker.model.RunModel
import com.example.android.runnertraker.utils.TrackingUtility
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

class CustomMarkerView(
    private val runs: List<RunModel>,
    context: Context,
    layoutId: Int
) : MarkerView(context, layoutId) {
    private lateinit var tvDate: TextView
    private lateinit var tvDuration: TextView
    private lateinit var tvAvgSpeed: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvCaloriesBurned: TextView
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)
        if (e == null) {
            return
        }
        setUpViews()
        val index = e.x.toInt()
        val run = runs[index]
        val calender = Calendar.getInstance().apply {
            timeInMillis = run.timestamp
        }
        val format = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        tvDate.text = format.format(calender.time)
        tvDuration.text= TrackingUtility.getTimeFormatted(run.timeInMills)
        tvDistance.text= "${(run.distanceInMeter / 1000f)} KM"
        tvAvgSpeed.text="${(run.avgSpeedKMH/1000f)} KMH"
        tvCaloriesBurned.text= "${run.caloriesBurned}kcal"



    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width / 2f, -height.toFloat())
    }

    private fun setUpViews() {
        tvDate = findViewById(R.id.tvDate)
        tvDistance = findViewById(R.id.tvDistance)
        tvDuration = findViewById(R.id.tvDuration)
        tvAvgSpeed = findViewById(R.id.tvAvgSpeed)
        tvCaloriesBurned = findViewById(R.id.tvCaloriesBurned)
    }
}