package com.example.furnitures_app

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.node.Node
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ARViewModel : ViewModel() {

    private val arHelper = ARHelper()

    var guidesState by mutableStateOf(Guides.SHOW_FIRST)

    var manipulationState by mutableStateOf(
        ManipulationState(
            isPositionEditable = true,
            isRotationEditable = false,
            isManipulationListExpand = false,
            isNodeTransitionSelected = false,
            isNodeRotationSelected = false
        )
    )

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
            manipulationState = manipulationState.copy(
                isPositionEditable = false,
                isRotationEditable = false,
                isNodeTransitionSelected = true,
                isNodeRotationSelected = false
            )
            modelNode.isPositionEditable = manipulationState.isPositionEditable
            modelNode.isRotationEditable = manipulationState.isRotationEditable
            val lockedY = modelNode.position.y
            modelNode.onEditingChanged = { editingTransforms ->
                val currentPos = modelNode.position
                modelNode.position = Position(currentPos.x, lockedY, currentPos.z)
            }
        }
    }

    fun enableNodeRotation() {
        if (childNodes.isNotEmpty()) {
            val modelNode = childNodes[0].childNodes.first()
            manipulationState = manipulationState.copy(
                isPositionEditable = true,
                isRotationEditable = true,
                isNodeTransitionSelected = false,
                isNodeRotationSelected = true
            )
            modelNode.isRotationEditable = manipulationState.isRotationEditable
            modelNode.isPositionEditable = manipulationState.isPositionEditable
        }

    }

    fun finishManipulation() {
        manipulationState = manipulationState.copy(
            isPositionEditable = true,
            isRotationEditable = false,
            isNodeTransitionSelected = false,
            isNodeRotationSelected = false,
            isManipulationListExpand = false
        )
        if (childNodes.isNotEmpty()) {
            val modelNode = childNodes[0].childNodes.first()
            modelNode.isPositionEditable = manipulationState.isPositionEditable
            modelNode.isRotationEditable = manipulationState.isRotationEditable
        }
    }

    fun toggleManipulationListExpandState() {
        if (manipulationState.isManipulationListExpand) {
            finishManipulation()
        } else {
            manipulationState = manipulationState.copy(
                isManipulationListExpand = !manipulationState.isManipulationListExpand
            )
        }
    }

    fun hideGuides(){
        guidesState = Guides.HIDE
        Log.d("hideGuides", "hideGuides: I'm in the hide method")
    }

    fun updateGuides(){
        guidesState = Guides.SHOW_SECOND

    }
}

enum class Guides {
    SHOW_FIRST,
    SHOW_SECOND,
    HIDE
}