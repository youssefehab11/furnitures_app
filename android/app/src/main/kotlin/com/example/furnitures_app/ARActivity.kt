package com.example.furnitures_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.ar.core.Config
import com.google.ar.core.Frame
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView


private const val kModelFile = "chair.glb"

class ARActivity : ComponentActivity() {

    private val viewModel: ARViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val message = intent?.extras?.getString("message")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ARView(
                viewModel = viewModel,
                modifier = Modifier.fillMaxSize(),
                onDismissUpdate = { finish() },
                onConfirmUpdate = { viewModel.requestARInstall(this) },
                onConfirmExit = {
                    viewModel.clearAnchorsAndNodes()
                    finish()
                }
            )
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.getShowUpdateARDialogState()
    }
}

@Composable
fun ARView(
    onConfirmExit: () -> Unit,
    modifier: Modifier,
    onDismissUpdate: () -> Unit,
    onConfirmUpdate: () -> Unit,
    viewModel: ARViewModel
) {
    if (viewModel.showUpdateARDialogState) {
        val dialogModel = DialogModel(
            title = "Update AR Services",
            message = "Google Play Services for AR needs update",
            icon = Icons.Default.Warning,
            onDismiss = { onDismissUpdate() },
            confirmText = "Update",
            onConfirm = { onConfirmUpdate() },
            iconDescription = "Warning Icon"
        )
        CustomDialog(dialogModel)
    } else {
        Box(modifier = modifier) {
            ARCameraView(
                viewModel = viewModel,
                modifier = modifier
            ) {
                onConfirmExit()
            }
            if(viewModel.guidesState != Guides.HIDE)
                ARGuide(modifier, viewModel)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(8.dp)
            ) {
                DoneButton(viewModel)
                SpeedDialObjManipulation(viewModel)
            }
        }

    }
}

@Composable
fun ARGuide(modifier: Modifier, viewModel: ARViewModel) {
    if(viewModel.guidesState == Guides.SHOW_FIRST){
        CustomGuide(
            modifier = modifier,
            lottieResId = R.raw.ar_scan,
            message = "Move your device around the room where you want to place the furniture"
        )
//        val preloaderLottieComposition by rememberLottieComposition(
//            LottieCompositionSpec.RawRes(
//                R.raw.ar_scan
//            )
//        )
//
//        val preloaderProgress by animateLottieCompositionAsState(
//            preloaderLottieComposition,
//            iterations = LottieConstants.IterateForever,
//            isPlaying = true
//        )
//
//        Box(modifier = modifier, contentAlignment = Alignment.Center) {
//            Box(
//                modifier = Modifier
//                    .width(450.dp)
//                    .height(450.dp)
//                    .clip(shape = RoundedCornerShape(16.dp))
//                    .background(color = colorResource(R.color.black_transparent))
//            ) {
//                Column {
//
//                    LottieAnimation(
//                        composition = preloaderLottieComposition,
//                        progress = preloaderProgress,
//                        modifier = Modifier.weight(0.8f)
//                    )
//                    Text(
//                        "",
//                        textAlign = TextAlign.Center,
//                        color = Color.White,
//                        fontSize = 24.sp,
//                        modifier = Modifier.padding(8.dp).weight(0.2f)
//                    )
//                }
//
//            }
//        }
    }
    else if(viewModel.guidesState == Guides.SHOW_SECOND){
        CustomGuide(
            modifier = modifier,
            lottieResId = R.raw.tap,
            message = "Tap the area you to place the furniture"
        )

//        val preloaderLottieComposition by rememberLottieComposition(
//            LottieCompositionSpec.RawRes(
//                R.raw.tap
//            )
//        )
//
//        val preloaderProgress by animateLottieCompositionAsState(
//            preloaderLottieComposition,
//            iterations = LottieConstants.IterateForever,
//            isPlaying = true
//        )
//
//        Box(modifier = modifier, contentAlignment = Alignment.Center) {
//            Box(
//                modifier = Modifier
//                    .width(450.dp)
//                    .height(450.dp)
//                    .clip(shape = RoundedCornerShape(16.dp))
//                    .background(color = colorResource(R.color.black_transparent))
//            ) {
//                Column {
//
//                    LottieAnimation(
//                        composition = preloaderLottieComposition,
//                        progress = preloaderProgress,
//                        modifier = Modifier.weight(0.8f)
//                    )
//                    Text(
//                        "Tap the area you to place the furniture",
//                        textAlign = TextAlign.Center,
//                        color = Color.White,
//                        fontSize = 24.sp,
//                        modifier = Modifier.padding(8.dp).weight(0.2f)
//                    )
//                }
//
//            }
//        }
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

    Row(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(50.dp))
            .background(color = colorResource(R.color.black_transparent))
    ) {
        AnimatedVisibility(visible = viewModel.manipulationState.isManipulationListExpand) {
            Row {
                IconButton(
                    onClick = {
                        viewModel.enableNodeTransition()
                    },
                ) {
                    Icon(
                        painterResource(R.drawable.transition_cube),
                        contentDescription = "transition icon",
                        tint = if (viewModel.manipulationState.isNodeTransitionSelected) Color.Green else Color.White
                    )
                }
                IconButton(
                    onClick = {
                        viewModel.enableNodeRotation()
                    },
                ) {
                    Icon(
                        painterResource(R.drawable.rotation_cube),
                        contentDescription = "rotation icon",
                        tint = if (viewModel.manipulationState.isNodeRotationSelected) Color.Green else Color.White
                    )
                }
            }
        }
        IconButton(
            onClick = { viewModel.toggleManipulationListExpandState() },
        ) {
            Icon(painterResource(R.drawable.cube), contentDescription = "", tint = Color.White)
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

    var planeRenderer by remember { mutableStateOf(true) }

    var frame by remember { mutableStateOf<Frame?>(null) }

    val childNodes = viewModel.childNodes

    val view = rememberView(engine)
    val collisionSystem = rememberCollisionSystem(view)
    var isPlaneDetected by remember { mutableStateOf(false) }

    BackHandler {
        viewModel.toggleExitDialogState()
    }
    ARScene(
        modifier = modifier,
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
        onSessionUpdated = { session, updatedFrame ->
            frame = updatedFrame
            if(!isPlaneDetected){
                val detectedPlanes = updatedFrame.getUpdatedPlanes()
                    .firstOrNull()
                if(detectedPlanes != null){
                    viewModel.updateGuides()
                }
            }
        },
        onGestureListener = rememberOnGestureListener(
            onSingleTapConfirmed = { motionEvent, node ->
                if (node == null && childNodes.isEmpty()) {
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