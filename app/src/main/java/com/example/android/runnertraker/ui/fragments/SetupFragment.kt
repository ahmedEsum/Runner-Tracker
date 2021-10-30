package com.example.android.runnertraker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.runnertraker.R
import com.example.android.runnertraker.databinding.FragmentSetupBinding
import com.example.android.runnertraker.utils.Constants.FIRST_TIME_TOGGLE
import com.example.android.runnertraker.utils.Constants.NAME_PREF
import com.example.android.runnertraker.utils.Constants.WEIGHT_PREF
import com.example.android.runnertraker.utils.TrackingUtility.checkInputs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {
    private lateinit var binding: FragmentSetupBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var isFirstTimeToggle = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setup, container, false)

        if (!isFirstTimeToggle) {
            findNavController().popBackStack(R.id.setupFragment, true)
            findNavController().navigate(R.id.runFragment)
        }
        binding.tvContinue.setOnClickListener {
            if (checkInputs(binding.etName,binding.etWeight,sharedPreferences)) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            }
        }

        return binding.root
    }



}