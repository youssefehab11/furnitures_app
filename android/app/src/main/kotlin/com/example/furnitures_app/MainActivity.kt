package com.example.furnitures_app

import android.content.Intent
import android.util.Log
import com.google.ar.core.ArCoreApk
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {
    private val channel = "flutter_android_channel"
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channel).setMethodCallHandler {
                call: MethodCall, result: MethodChannel.Result ->
            when (call.method) {
                "checkARSupport" -> {
                    checkARSupport(){ isSupported ->
                        Log.d("method channel", "methodChannelARSupportResult: $isSupported")
                        result.success(isSupported)
                    }
                }
                "launchAR" -> {
                    try {
                        // Takes an object, in this case a String.
                        val message = call.arguments
                        val intent = Intent(this@MainActivity, ARActivity::class.java)
                        intent.putExtra("message", message.toString())
                        startActivity(intent)
                    } catch (_: Exception){}
                    result.success(true)
                }
                else -> result.notImplemented()
            }
        }
    }
    private fun checkARSupport(result: (Boolean)-> Unit) {
        ArCoreApk.getInstance().checkAvailabilityAsync(this){availability ->
            Log.d("check AR Support", "checkARSupportAvailability: $availability")
            if(availability.isSupported){
                result(true)
            }
            else{
                result(false)
            }
        }

    }
}
