package com.example.android.runnertraker.ui.fragments


import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.runnertraker.R
import com.example.android.runnertraker.databinding.FragmentTrackingBinding
import com.example.android.runnertraker.model.RunModel
import com.example.android.runnertraker.services.TrackingService
import com.example.android.runnertraker.services.polyline
import com.example.android.runnertraker.utils.Constants.CAMERA_SPEED
import com.example.android.runnertraker.utils.Constants.COLOR
import com.example.android.runnertraker.utils.Constants.PAUSE_SERVICE
import com.example.android.runnertraker.utils.Constants.START_OR_RESUME_SERVICE
import com.example.android.runnertraker.utils.Constants.STOP_SERVICE
import com.example.android.runnertraker.utils.Constants.WIDTH
import com.example.android.runnertraker.utils.TrackingUtility
import com.example.android.runnertraker.utils.TrackingUtility.checkInternetConnectionAndGps
import com.example.android.runnertraker.utils.TrackingUtility.isInternetConnected
import com.example.android.runnertraker.viewmodels.RunViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.math.round

const val PARENT_FRAG="parent frag"
@AndroidEntryPoint
class TrackingFragment : Fragment() {

    @set:Inject
    var weight = 80f

    private var googleMap: GoogleMap? = null
    private var isTracking = false
    private var pathPoints = mutableListOf<polyline>()
    private var currentTimeMillis = 0L
    private var menu: Menu? = null
    private lateinit var binding: FragmentTrackingBinding
    private val runViewModel: RunViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tracking, container, false)
        if (savedInstanceState!=null){
            val cancelDialogState = parentFragmentManager.findFragmentByTag(PARENT_FRAG)as CancelTrackingDialog?
            cancelDialogState?.let {
                it.setListener {
                    stopRun()
                }
            }
        }
        setHasOptionsMenu(true)
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            googleMap = it
            addAllPoints()
        }
        subscribeForObservers()
        binding.btnToggleRun.setOnClickListener {
            if (checkInternetConnectionAndGps(requireContext())&& isInternetConnected(requireContext())) {
                toggleRun(isTracking)
            }
        }
        binding.btnFinishRun.setOnClickListener {
            moveCameraForScreenShot()
            endRunAndSaveToDatabase()
        }
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.cancel_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancelRun -> showCancelDialog()
        }
        return super.onOptionsItemSelected(item)

    }

    private fun showCancelDialog() {
            CancelTrackingDialog().apply {
                setListener {
                    stopRun()
                }
            }.show(parentFragmentManager,PARENT_FRAG)
    }

    private fun stopRun() {
        isTracking = false
        binding.tvTimer.text="00:00:00:00"
        setServiceAction(STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    private fun toggleRun(stillTracking: Boolean) {
        if (stillTracking) {
            setServiceAction(PAUSE_SERVICE)
            menu?.getItem(0)?.isVisible = true
        } else setServiceAction(START_OR_RESUME_SERVICE)
    }

    private fun setServiceAction(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    private fun addLastTwoElements() {
        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
            val preLatLang = pathPoints.last()[pathPoints.last().size - 2]
            val lastLatLang = pathPoints.last().last()

            val polylineOptions =
                PolylineOptions().color(COLOR).width(WIDTH).add(preLatLang).add(lastLatLang)
            googleMap?.addPolyline(polylineOptions)
        }
    }

    private fun addAllPoints() {
        for (polyline in pathPoints) {
            val polyOptions = PolylineOptions().color(COLOR).width(WIDTH).addAll(polyline)
            googleMap?.addPolyline(polyOptions)
        }
    }

    private fun moveCamera() {
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
            googleMap?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), CAMERA_SPEED)
            )
        }
    }

    private fun moveCameraForScreenShot() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathPoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }

        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()
            )
        )
    }

    private fun endRunAndSaveToDatabase() {

        googleMap?.snapshot { bmp ->
            var distanceInMeter = 0
            for (polyline in pathPoints) {
                distanceInMeter += TrackingUtility.getDistanceForRun(polyline).toInt()
            }
            val avgSpeed =
                round((distanceInMeter / 1000f) / (currentTimeMillis / 1000f / 60 / 60) * 10) / 10f

            val dateStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeter / 1000f) * weight).toInt()

            val run = RunModel(
                bmp, dateStamp, avgSpeed, distanceInMeter, currentTimeMillis, caloriesBurned
            )
            runViewModel.insertRun(run)

            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Your run saved successfully ... Good Job !",
                Snackbar.LENGTH_SHORT
            ).show()
            stopRun()
        }


    }

    private fun updateTracking(trackingState: Boolean) {
        isTracking = trackingState
        if (isTracking) {
            binding.btnToggleRun.text = getString(R.string.stop)
            menu?.getItem(0)?.isVisible = true
            binding.btnFinishRun.visibility = View.GONE
        } else if (!isTracking && currentTimeMillis>0L){
            binding.btnToggleRun.text = getString(R.string.start)
            binding.btnFinishRun.visibility = View.VISIBLE
        }
    }

    private fun subscribeForObservers() {
        TrackingService.isTrackingMTD.observe(viewLifecycleOwner, {
            updateTracking(it)
        })
        TrackingService.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLastTwoElements()
            moveCamera()
        })
        TrackingService.trackingTimeInMillis.observe(viewLifecycleOwner, {
            currentTimeMillis = it
            val timeText = TrackingUtility.getTimeFormatted(currentTimeMillis, true)
            binding.tvTimer.text = timeText
            Timber.d("current time is $currentTimeMillis")
        })
    }




    override fun onDestroyView() {
        super.onDestroyView()
        binding.mapView.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

}