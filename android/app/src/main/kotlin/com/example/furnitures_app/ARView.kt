package com.example.furnitures_app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.example.furnitures_app.components.CircularIconButton
import com.example.furnitures_app.components.CustomDialog
import com.example.furnitures_app.components.CustomGuide
import com.example.furnitures_app.components.DialogModel
import com.google.ar.core.Config
import com.google.ar.core.Frame
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.math.Position
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView


private const val kModelFile = "chair.glb"

@Composable
fun ARView(
    onConfirmExit: () -> Unit,
    modifier: Modifier,
    viewModel: ARViewModel
) {
    Box(modifier = modifier) {
        ARCameraView(
            viewModel = viewModel,
            modifier = modifier
        ) {
            onConfirmExit()
        }
        if (viewModel.guidesState != Guides.HIDE)
            ARGuide(modifier, viewModel)
        Controls(viewModel = viewModel, modifier = modifier)
    }
}

@Composable
fun Controls(viewModel: ARViewModel, modifier: Modifier) {
    AnimatedVisibility(viewModel.isControlsVisible, enter = fadeIn(), exit = fadeOut()) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                DoneButton(viewModel)
                SpeedDialObjManipulation(viewModel)
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterEnd
            ) {
                CircularIconButton(
                    icon = R.drawable.help,
                    color = Color.White,
                    contentDescription = "help icon",
                    padding = 4.dp
                ) {

                }
            }
            AnimatedVisibility(viewModel.isDeleteButtonVisible, enter = fadeIn(), exit = fadeOut()) {
                Box(
                    modifier = modifier,
                    contentAlignment = Alignment.BottomStart
                ) {
                    ExtendedFloatingActionButton(
                        onClick = {
                            if (viewModel.childNodes.isNotEmpty()) {
                                viewModel.deleteModel()
                            }
                        },
                        containerColor = Color.White,
                        contentColor = Color.Red,
                        shape = CircleShape
                    ) {
                        Icon(imageVector = Icons.Outlined.Delete, contentDescription = "delete icon")
                        Text("Delete")
                    }
                }
            }
        }
    }
}

@Composable
fun ARGuide(modifier: Modifier, viewModel: ARViewModel) {
    if (viewModel.guidesState == Guides.SHOW_FIRST) {
        CustomGuide(
            modifier = modifier,
            lottieResId = R.raw.ar_scan,
            message = "Move your device around the room where you want to place the furniture"
        )
    } else if (viewModel.guidesState == Guides.SHOW_SECOND) {
        CustomGuide(
            modifier = modifier,
            lottieResId = R.raw.tap,
            message = "Tap the area you to place the furniture",
            animationSize = 250.dp,
//            onConfirm = {
//                viewModel.hideGuides()
//            }
        )
    }
}

@Composable
fun DoneButton(viewModel: ARViewModel) {
    Row {
        AnimatedVisibility(visible = viewModel.manipulationState.isManipulationListExpand) {
            Button(onClick = {
                viewModel.finishManipulation()
            }) {
                Text("Done")
            }
        }
    }
}

@Composable
fun SpeedDialObjManipulation(
    viewModel: ARViewModel
) {

    Row {
        AnimatedVisibility(visible = viewModel.manipulationState.isManipulationListExpand) {
            Row {
                CircularIconButton(
                    icon = R.drawable.transition_cube,
                    color = if (viewModel.manipulationState.isPositionEditable) Color.Green else Color.White,
                    contentDescription = "position transition icon",
                ) {
                    viewModel.changeManipulationState(isPositionEditable = true)
                }
                CircularIconButton(
                    icon = R.drawable.rotation_cube,
                    color = if (viewModel.manipulationState.isRotationEditable) Color.Green else Color.White,
                    contentDescription = "rotation icon",
                    padding = 4.dp
                ) {
                    viewModel.changeManipulationState(isRotationEditable = true)
                }
                CircularIconButton(
                    icon = R.drawable.vertical_transition,
                    color = if (viewModel.manipulationState.isVerticalEditable) Color.Green else Color.White,
                    contentDescription = "vertical transition icon",
                    padding = 8.dp
                ) {
                    viewModel.changeManipulationState(isVerticalEditable = true)
                }
            }
        }
        CircularIconButton(
            icon = R.drawable.cube,
            color = Color.White,
            contentDescription = "cube icon"
        ) {
            viewModel.toggleManipulationListExpandState()
        }
    }
}

@Composable
fun ARCameraView(
    viewModel: ARViewModel,
    modifier: Modifier,
    onConfirm: () -> Unit,
) {

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)

    val planeRenderer by remember { mutableStateOf(true) }

    var frame by remember { mutableStateOf<Frame?>(null) }

    val childNodes = viewModel.childNodes

    val view = rememberView(engine)
    val collisionSystem = rememberCollisionSystem(view)
    var isPlaneDetected by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.toggleExitDialogState()
    }
    ARScene(
        modifier = modifier
            .pointerInput(viewModel.manipulationState) {
                if (childNodes.isNotEmpty() && viewModel.manipulationState.isPositionEditable) {
                    val modelNode = childNodes[0].childNodes.first()
                    detectDragGestures { _, dragAmount ->
                        horizontalTransition(modelNode, dragAmount)
                    }
                } else if (childNodes.isNotEmpty() && viewModel.manipulationState.isVerticalEditable) {
                    val modelNode = childNodes[0].childNodes.first()
                    detectVerticalDragGestures { _, dragAmount ->
                        verticalTransition(modelNode, dragAmount)
                    }
                }
            },
        engine = engine,
        modelLoader = modelLoader,
        materialLoader = materialLoader,
        collisionSystem = collisionSystem,
        view = view,
        childNodes = childNodes,
        sessionConfiguration = { session, config ->
            config.planeFindingMode = Config.PlaneFindingMode.HORIZONTAL
            config.lightEstimationMode = Config.LightEstimationMode.ENVIRONMENTAL_HDR
            config.depthMode = when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                true -> Config.DepthMode.AUTOMATIC
                else -> Config.DepthMode.DISABLED
            }
        },

        planeRenderer = planeRenderer,
        onSessionUpdated = { _, updatedFrame ->
            frame = updatedFrame
            if (!isPlaneDetected) {
                val detectedPlanes = updatedFrame.getUpdatedPlanes()
                    .firstOrNull()
                if (detectedPlanes != null) {
                    viewModel.updateGuides()
                    isPlaneDetected = true
                }
            }
        },
        onGestureListener = rememberOnGestureListener(
            onSingleTapConfirmed = { motionEvent, node ->
                if (node == null && childNodes.isEmpty() && viewModel.guidesState == Guides.HIDE) {
                    val hitTest = frame?.hitTest(motionEvent.x, motionEvent.y)
                    hitTest?.firstOrNull {
                        it.isValid(
                            depthPoint = false,
                            point = false
                        )
                    }?.createAnchorOrNull()?.let { anchor ->
                        viewModel.anchor = anchor
                        viewModel.createAnchorNode(
                            engine = engine,
                            modelLoader = modelLoader,
                            materialLoader = materialLoader,
                            model = kModelFile,
                            anchor = anchor
                        )
                    }
                }
                else if(node != null){
                    viewModel.toggleDeleteButtonVisibility()
                }
            },
            onDoubleTap = { _, _ ->
                viewModel.toggleControlsVisibility()
            }
        )
    ) {

    }
    if (viewModel.showExitDialogState) {
        val dialogModel = DialogModel(
            message = "Are you sure you want to exit AR session?",
            icon = Icons.Default.Info,
            onDismiss = { viewModel.toggleExitDialogState() },
            confirmText = "Exit",
            onConfirm = { onConfirm() },
            iconDescription = "Info Icon"
        )
        CustomDialog(
            dialogModel
        )
    }
}

fun horizontalTransition(modelNode: Node, dragAmount: Offset) {
    val currentPos = modelNode.position
    modelNode.position = Position(
        x = currentPos.x + dragAmount.x * 0.004f,
        y = currentPos.y,
        z = currentPos.z + dragAmount.y * 0.004f
    )
}

fun verticalTransition(modelNode: Node, dragAmount: Float) {
    val currentPos = modelNode.position
    modelNode.position = Position(
        x = currentPos.x,
        y = currentPos.y - dragAmount * 0.004f,
        z = currentPos.z
    )
}
