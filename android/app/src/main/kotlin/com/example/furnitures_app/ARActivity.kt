package com.example.furnitures_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.furnitures_app.components.CustomDialog
import com.example.furnitures_app.components.DialogModel
import com.google.ar.core.ArCoreApk

class ARActivity : ComponentActivity() {


    private var showUpdateARDialogState by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val message = intent?.extras?.getString("message")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            if (showUpdateARDialogState) {
                val dialogModel = DialogModel(
                    title = "Update AR Services",
                    message = "Google Play Services for AR needs update",
                    icon = Icons.Default.Warning,
                    onDismiss = { finish() },
                    confirmText = "Update",
                    onConfirm = { requestARInstall() },
                    iconDescription = "Warning Icon"
                )
                CustomDialog(dialogModel)
            } else {
                val viewModel: ARViewModel = viewModel()
                ARView(
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                    onConfirmExit = {
                        viewModel.clearAnchorsAndNodes()
                        finish()
                    }
                )
            }

        }
    }


    override fun onResume() {
        super.onResume()
        getShowUpdateARDialogState()
    }

    private fun getShowUpdateARDialogState() {
        showUpdateARDialogState = !checkARUpdateStatus()
    }
    private fun checkARUpdateStatus(): Boolean {
        val availability =
            ArCoreApk.getInstance().checkAvailability(ARApplication.getApplicationContext())
        return when (availability) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                true
            }
            else -> {
                false
            }
        }
    }

    private fun requestARInstall() {
        try {
            ArCoreApk.getInstance().requestInstall(this, true)
        } catch (e: Exception) {
            Log.d("Request AR Install", "ARCore not installed: " + e.message.toString())
            return
        }
    }
}

