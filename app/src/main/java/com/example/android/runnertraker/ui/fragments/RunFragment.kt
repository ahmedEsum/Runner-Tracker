package com.example.android.runnertraker.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.runnertraker.R
import com.example.android.runnertraker.adpters.RunsAdapter
import com.example.android.runnertraker.databinding.FragmentRunBinding
import com.example.android.runnertraker.utils.Constants.REQUEST_CODE
import com.example.android.runnertraker.utils.FilterConstant
import com.example.android.runnertraker.utils.TrackingUtility
import com.example.android.runnertraker.viewmodels.RunViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private val runViewModel: RunViewModel by viewModels()
    private var binding: FragmentRunBinding? = null
    private lateinit var runsAdapter: RunsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_run, container, false)
        requestPermission()
        setupViews()

        runViewModel.allRunLiveData.observe(viewLifecycleOwner, {
            runsAdapter.setList(it)
        })

        binding?.fab?.setOnClickListener {
            findNavController().navigate(R.id.action_runFragment_to_trackingFragment2)
        }
        return binding?.root!!
    }

    private fun requestPermission() {
        if (TrackingUtility.hasPermissionLocation(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.requestPermission),

                REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.requestPermission),
                REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
//                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Toast.makeText(requireContext(), "great ", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        Timber.d("request code is $requestCode")
    }

    private fun setupViews() {
        binding?.rvRuns.apply {
            runsAdapter = RunsAdapter()
            this!!.adapter = runsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }


        when (runViewModel.filterBy) {
            FilterConstant.DATE -> binding?.spFilter?.setSelection(0)
            FilterConstant.TIME_IN_MILLS -> binding?.spFilter?.setSelection(1)
            FilterConstant.DISTANCE -> binding?.spFilter?.setSelection(2)
            FilterConstant.SPEED -> binding?.spFilter?.setSelection(3)
            FilterConstant.CALORIES -> binding?.spFilter?.setSelection(4)
        }

        binding?.spFilter?.onItemSelectedListener =object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                when (p2) {
                    0 -> runViewModel.sortBy(FilterConstant.DATE)
                    1 -> runViewModel.sortBy(FilterConstant.TIME_IN_MILLS)
                    2 -> runViewModel.sortBy(FilterConstant.DISTANCE)
                    3 -> runViewModel.sortBy(FilterConstant.SPEED)
                    4 -> runViewModel.sortBy(FilterConstant.CALORIES)
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }
}