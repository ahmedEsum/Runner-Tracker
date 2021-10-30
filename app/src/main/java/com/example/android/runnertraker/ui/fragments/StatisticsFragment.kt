package com.example.android.runnertraker.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.runnertraker.R
import com.example.android.runnertraker.databinding.FragmentStatisticsBinding
import com.example.android.runnertraker.ui.CustomMarkerView
import com.example.android.runnertraker.utils.TrackingUtility
import com.example.android.runnertraker.viewmodels.RunViewModel
import com.example.android.runnertraker.viewmodels.StatisticViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment() {
    private val statisticsViewModel :StatisticViewModel by viewModels()
    private lateinit var binding:FragmentStatisticsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_statistics,container,false)
        observeValues()
        setBarChart()
        return binding.root
    }

    private fun setBarChart(){
        binding.barChart.xAxis.apply {
            position=XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawLabels(false)
            textColor= Color.WHITE
            axisLineColor=Color.WHITE
        }
        binding.barChart.axisLeft.apply {
            axisLineColor=Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.axisRight.apply {
            axisLineColor=Color.WHITE
            textColor=Color.WHITE
            setDrawGridLines(false)
        }
        binding.barChart.apply {
            description.text="Avg Speed over time"
            legend.isEnabled=false
        }
    }

    private fun observeValues(){
        statisticsViewModel.getTotalTime().observe(viewLifecycleOwner,{
            it?.let {
                binding.tvTotalTime.text=TrackingUtility.getTimeFormatted(it)
            }
        })
        statisticsViewModel.getTotalDistance().observe(viewLifecycleOwner,{
            it?.let {
                val totalDistanceInKM = round((it/1000f)*10f)/10f
                binding.tvTotalDistance.text="$totalDistanceInKM KM"
            }
        })
        statisticsViewModel.getTotalAvgSpeed().observe(viewLifecycleOwner,{
            it?.let {
                val avgSpeed = round(it*10f)/10f
                binding.tvAverageSpeed.text="$avgSpeed KM/H"
            }
        })
        statisticsViewModel.getTotalCalories().observe(viewLifecycleOwner,{
            it?.let {
                binding.tvTotalCalories.text="$it kcal"
            }
        })
        statisticsViewModel.getAllRunsByDate().observe(viewLifecycleOwner,{
            it?.let {
                val allAvgSpeed = it.indices.map { i->
                    BarEntry(i.toFloat(),it[i].avgSpeedKMH)
                }
                val barDataSet=BarDataSet(allAvgSpeed,"avg speed").apply {
                    valueTextColor=Color.WHITE
                    color=ContextCompat.getColor(requireContext(),R.color.colorAccent)
                }
                binding.barChart.data= BarData(barDataSet)
                binding.barChart.marker=CustomMarkerView(it.reversed(),requireContext(),R.layout.marker_view)
                binding.barChart.invalidate()

            }
        })
    }

}