package com.example.android.runnertraker.ui.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.android.runnertraker.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelTrackingDialog : DialogFragment() {

    private var yesListener:(()->Unit)?=null
    fun setListener (listener : ()-> Unit){
        yesListener=listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(
            requireContext(),
            R.style.AlertDialogTheme
        ).setTitle("Cancel The Run ?").setMessage("are you sure you want to cancel the run ? ")
            .setIcon(R.drawable.ic_baseline_delete_24)
            .setPositiveButton("Yes") { _, _ ->
                yesListener?.let {
                    it()
                }
            }.setNegativeButton("No", null).create()
    }
}