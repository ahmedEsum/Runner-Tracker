package com.example.android.runnertraker.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.android.runnertraker.R
import com.example.android.runnertraker.databinding.ActivityMainBinding
import com.example.android.runnertraker.utils.Constants.START_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        checkNavigationIntent(intent)
        setSupportActionBar(binding.toolbar)
        binding.bottomNavigationView.setupWithNavController(findNavController(R.id.navHostFragment))
        findNavController(R.id.navHostFragment).addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingFragment, R.id.runFragment, R.id.statisticsFragment -> binding.bottomNavigationView.visibility =
                    View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkNavigationIntent(intent)
    }

    private fun checkNavigationIntent(intent: Intent?) {
        if (intent?.action == START_TRACKING_FRAGMENT) {
            findNavController(R.id.navHostFragment).navigate(R.id.actionTrackingService)
        }
    }
}