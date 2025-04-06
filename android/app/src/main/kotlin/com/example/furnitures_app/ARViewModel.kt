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
import io.github.sceneview.node.Node
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ARViewModel : ViewModel() {

    private val arHelper = ARHelper()

    var guidesState by mutableStateOf(Guides.SHOW_FIRST)

    var manipulationState by mutableStateOf(
        ManipulationState(
            isPositionEditable = false,
            isRotationEditable = false,
            isVerticalEditable = false,
            isManipulationListExpand = false,
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

    fun changeManipulationState(
        isPositionEditable: Boolean = false,
        isRotationEditable: Boolean = false,
        isVerticalEditable: Boolean = false
    ){
        if(childNodes.isNotEmpty()){
            val modelNode = childNodes[0].childNodes.first()
            manipulationState = manipulationState.copy(
                isPositionEditable = isPositionEditable,
                isRotationEditable = isRotationEditable,
                isVerticalEditable = isVerticalEditable
            )
            modelNode.isRotationEditable = manipulationState.isRotationEditable
        }

    }
//    fun enableNodePositionTransition() {
//        if (childNodes.isNotEmpty()) {
//            val modelNode = childNodes[0].childNodes.first()
//            manipulationState = manipulationState.copy(
//                isPositionEditable = true,
//                isRotationEditable = false,
//                isVerticalEditable = false
//            )
//            modelNode.isRotationEditable = manipulationState.isRotationEditable
//        }
//    }
//
//    fun enableNodeRotation() {
//        if (childNodes.isNotEmpty()) {
//            val modelNode = childNodes[0].childNodes.first()
//            manipulationState = manipulationState.copy(
//                isPositionEditable = false,
//                isRotationEditable = true,
//            )
//            modelNode.isRotationEditable = manipulationState.isRotationEditable
//        }
//
//    }
//
//    fun enableNodeVerticalTransition(){
//        if(childNodes.isNotEmpty()){
//            val modelNode = childNodes[0].childNodes.first()
//            manipulationState = manipulationState.copy(
//                isPositionEditable = false,
//                isRotationEditable = false,
//                isVerticalEditable = true
//            )
//            modelNode.isRotationEditable = manipulationState.isRotationEditable
//        }
//    }

    fun finishManipulation() {
        manipulationState = manipulationState.copy(
            isPositionEditable = false,
            isRotationEditable = false,
            isVerticalEditable = false,
            isManipulationListExpand = false
        )
        if (childNodes.isNotEmpty()) {
            val modelNode = childNodes[0].childNodes.first()
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

    private fun hideGuides() {
        guidesState = Guides.HIDE
        Log.d("hideGuides", "hideGuides: I'm in the hide method")
    }

    fun updateGuides() {
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