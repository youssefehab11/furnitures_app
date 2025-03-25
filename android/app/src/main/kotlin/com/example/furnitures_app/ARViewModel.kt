package com.example.furnitures_app

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.Node

class ARViewModel : ViewModel() {

    private val arHelper = ARHelper()

    var isNodeTransitionSelected by mutableStateOf(false)

    var isNodeRotationSelected by mutableStateOf(false)

    var manipulationListExpandState by  mutableStateOf(false)

    var anchor: Anchor? by mutableStateOf(null)

    var childNodes = mutableStateListOf<Node>()

    var showUpdateARDialogState by mutableStateOf(false)
        private set

    var showExitDialogState by mutableStateOf(false)
        private set

    fun toggleExitDialogState() {
        showExitDialogState = !showExitDialogState
    }

    fun getShowUpdateARDialogState() {
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

    fun requestARInstall(activity: Activity) {
        try {
            ArCoreApk.getInstance().requestInstall(activity, true)
        } catch (e: Exception) {
            Log.d("Request AR Install", "ARCore not installed: " + e.message.toString())
            return
        }
    }

    fun createAnchorNode(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        anchor: Anchor,
        model: String
    ) {
        childNodes += arHelper.createAnchorNode(engine, modelLoader, materialLoader, anchor, model)
    }

    fun clearAnchorsAndNodes() {
        arHelper.clearAnchorsAndNodes(childNodes, anchor)
    }

    fun enableNodeTransition() {
        if (childNodes.isNotEmpty()) {
            val modelNode = childNodes[0].childNodes.first()
            modelNode.isPositionEditable = false
            modelNode.isRotationEditable = false
            isNodeTransitionSelected = true
            isNodeRotationSelected = false
            val lockedY = modelNode.position.y
            modelNode.onEditingChanged = {editingTransforms ->
                val currentPos = modelNode.position
                modelNode.position = Position(currentPos.x,lockedY,currentPos.z)
            }
        }
    }

    fun enableNodeRotation() {
        if (childNodes.isNotEmpty()) {
            val modelNode = childNodes[0].childNodes.first()
            modelNode.isRotationEditable = true
            modelNode.isPositionEditable = true
            isNodeTransitionSelected = false
            isNodeRotationSelected = true
        }

    }

    fun finishManipulation() {
        val modelNode = childNodes[0].childNodes.first()
        modelNode.isPositionEditable = true
        modelNode.isRotationEditable = false
        manipulationListExpandState = false
        isNodeTransitionSelected = false
        isNodeRotationSelected = false
    }

    fun toggleManipulationListExpandState(){
        manipulationListExpandState = !manipulationListExpandState
    }
}