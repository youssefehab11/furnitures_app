package com.example.furnitures_app

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.ar.core.ArCoreApk

class ARViewModel: ViewModel() {
    var showUpdateARDialogState by mutableStateOf(false)
        private set

    var showExitDialogState by mutableStateOf(false)
        private set

    fun toggleExitDialogState(){
        showExitDialogState = !showExitDialogState
    }

    fun getShowUpdateARDialogState(){
        showUpdateARDialogState = !checkARUpdateStatus()
    }
    private fun checkARUpdateStatus(): Boolean {
        val availability = ArCoreApk.getInstance().checkAvailability(ARApplication.getApplicationContext())
        return when (availability) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                true
            }

            else -> {
                false
            }
        }
    }

    fun requestARInstall(activity: Activity) {
        try {
            ArCoreApk.getInstance().requestInstall(activity, true)
        } catch (e: Exception) {
            Log.d("Request AR Install", "ARCore not installed: " + e.message.toString())
            return
        }
    }
}