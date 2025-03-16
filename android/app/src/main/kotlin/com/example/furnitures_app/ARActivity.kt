package com.example.furnitures_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.ar.core.ArCoreApk

class ARActivity : ComponentActivity() {

    //private var userRequestedInstall = true
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
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!checkARUpdateStatus()) {
            showUpdateARDialogState = true
        } else {
            showUpdateARDialogState = false
            //startARSession() // Optional: Start AR experience now
        }
    }

    private fun checkARUpdateStatus(): Boolean {
        val availability = ArCoreApk.getInstance().checkAvailability(this)
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

@Composable
fun ARView(message: String, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Second Activity")
            Text(message)
            Button(onClick = { onClick() }) {
                Text("End")
            }
        }
    }
}

@Preview
@Composable
fun ARPreview() {
    ARView("Message") { }
}
