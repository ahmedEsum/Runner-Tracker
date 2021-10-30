package com.example.android.runnertraker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.runnertraker.R
import com.example.android.runnertraker.databinding.FragmentSettingBinding
import com.example.android.runnertraker.utils.Constants
import com.example.android.runnertraker.utils.TrackingUtility.checkInputs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        setValuesForViews()
        binding.btnApplyChanges.setOnClickListener {
            if (checkInputs(binding.etName, binding.etWeight, sharedPreferences)) {
                Toast.makeText(requireContext(), "changes saved successfully ", Toast.LENGTH_SHORT)
                    .show()

            } else {
                Toast.makeText(requireContext(), "please check inputs ", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root

    }

    private fun setValuesForViews() {
        binding.etName.setText(sharedPreferences.getString(Constants.NAME_PREF, ""))
        binding.etWeight.setText(sharedPreferences.getFloat(Constants.WEIGHT_PREF, 0f).toString())
    }
}