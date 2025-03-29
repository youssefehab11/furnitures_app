package com.example.furnitures_app

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
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

    var showExitDialogState by mutableStateOf(false)
        private set

    fun toggleExitDialogState() {
        showExitDialogState = !showExitDialogState
    }


    fun createAnchorNode(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        anchor: Anchor,
        model: String
    ) {
        val node = arHelper.createAnchorNode(engine, modelLoader, materialLoader, anchor, model)
        childNodes += node
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
            Log.d("lockedY", "lockedY: $lockedY")
            modelNode.onEditingChanged = { editingTransforms ->
                val currentPos = modelNode.position

                modelNode.position = Position(currentPos.x, lockedY, currentPos.z)
                Log.d("currentPos", "currentPos: ${modelNode.position}")
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

    private fun hideGuides(){
        guidesState = Guides.HIDE
        Log.d("hideGuides", "hideGuides: I'm in the hide method")
    }

    fun updateGuides(){
        guidesState = Guides.SHOW_SECOND
        viewModelScope.launch {
            delay(5000)
            hideGuides()
        }
    }
}

enum class Guides {
    SHOW_FIRST,
    SHOW_SECOND,
    HIDE
}