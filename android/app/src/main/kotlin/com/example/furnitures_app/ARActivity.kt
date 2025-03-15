package com.example.furnitures_app

import android.app.Activity
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.ar.core.ArCoreApk

class ARActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val message = intent?.extras?.getString("message")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

        }
    }

    override fun onResume() {
        super.onResume()
        val isReady = checkARUpdateStatus()
        Log.d("checkARUpdateStatus", "checkARUpdateStatus: $isReady")

    }

    private fun checkARUpdateStatus(): Boolean {
        val availability = ArCoreApk.getInstance().checkAvailability(this)
        return when (availability) {
            ArCoreApk.Availability.SUPPORTED_INSTALLED -> {
                true
            }

            ArCoreApk.Availability.SUPPORTED_NOT_INSTALLED, ArCoreApk.Availability.SUPPORTED_APK_TOO_OLD -> {
                requestARInstall()
            }

            else -> {
                false;
            }
        }
    }

    private fun requestARInstall(): Boolean {
        try {
            return when (ArCoreApk.getInstance().requestInstall(this, true)) {
                ArCoreApk.InstallStatus.INSTALL_REQUESTED -> {
                    false
                }

                ArCoreApk.InstallStatus.INSTALLED -> true
            }
        } catch (e: Exception) {
            Log.d("Request AR Install", "ARCore not installed: " + e.message.toString())
            return false
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
