package com.example.furnitures_app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.ArCoreApk
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.scene.destroy
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberView


private const val kModelFile = "chair.glb"

class ARActivity : ComponentActivity() {

    //    private var showUpdateARDialogState by mutableStateOf(false)
    private val viewModel: ARViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        val message = intent?.extras?.getString("message")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ARView(
                viewModel = viewModel,
                onDismissUpdate = { finish() },
                onConfirmUpdate = { viewModel.requestARInstall(this) },
                onConfirmExit = { anchor, childNodes ->
                    clearAnchorsAndNodes(anchor, childNodes)
                    finish()
                }
            )
        }
    }

    private fun clearAnchorsAndNodes(anchor: Anchor?, childNodes: MutableList<Node>) {
        childNodes.clear()
        if (anchor != null) {
            anchor.detach()
            anchor.destroy()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getShowUpdateARDialogState()
    }


//    private fun requestARInstall() {
//        try {
//            ArCoreApk.getInstance().requestInstall(this, true)
//        } catch (e: Exception) {
//            Log.d("Request AR Install", "ARCore not installed: " + e.message.toString())
//            return
//        }
//    }
}

@Composable
fun ARView(
    onConfirmExit: (Anchor?, MutableList<Node>) -> Unit,
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
        Box {
            ARCameraView(viewModel = viewModel) { anchor, childNodes ->
                onConfirmExit(anchor, childNodes)
            }
        }

    }
}

@Composable
fun ARCameraView(
    viewModel: ARViewModel,
    onConfirm: (Anchor?, MutableList<Node>) -> Unit,
) {

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)

    var planeRenderer by remember { mutableStateOf(true) }

    var frame by remember { mutableStateOf<Frame?>(null) }

    val childNodes = rememberNodes()

    val view = rememberView(engine)
    val collisionSystem = rememberCollisionSystem(view)
    var myAnchor: Anchor? = null
    BackHandler {
        viewModel.toggleExitDialogState()
    }
    ARScene(
        modifier = Modifier.fillMaxSize(),
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
            if (childNodes.isEmpty()) {
                updatedFrame.getUpdatedPlanes()
                    .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                    ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                        myAnchor = anchor
                        childNodes += createAnchorNode(
                            engine = engine,
                            modelLoader = modelLoader,
                            materialLoader = materialLoader,
                            anchor = anchor
                        )
                    }
            }
        },
    ) {

    }
    if (viewModel.showExitDialogState) {
        val dialogModel = DialogModel(
            message = "Are you sure you want to exit AR session?",
            icon = Icons.Default.Info,
            onDismiss = { viewModel.toggleExitDialogState() },
            confirmText = "Exit",
            onConfirm = { onConfirm(myAnchor, childNodes) },
            iconDescription = "Info Icon"
        )
        CustomDialog(
            dialogModel
        )
    }

}

fun createAnchorNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    anchor: Anchor
): AnchorNode {
    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
    val modelNode = ModelNode(
        modelInstance = modelLoader.createModelInstance(kModelFile),
        // Scale to fit in a 0.5 meters cube
        //scaleToUnits = 0.5f

    ).apply {
        // Model Node needs to be editable for independent rotation from the anchor rotation
        isEditable = true
        isScaleEditable = false
    }
//    val boundingBoxNode = CubeNode(
//        engine,
//        size = modelNode.extents,
//        center = modelNode.center,
//        materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
//    ).apply {
//        isVisible = false
//    }
//    modelNode.addChildNode(boundingBoxNode)
    anchorNode.addChildNode(modelNode)
//
//    listOf(modelNode, anchorNode).forEach {
//        it.onEditingChanged = { editingTransforms ->
//            boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
//        }
//    }
    return anchorNode
}