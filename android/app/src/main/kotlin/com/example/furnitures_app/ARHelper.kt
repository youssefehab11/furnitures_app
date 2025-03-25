package com.example.furnitures_app

import androidx.compose.ui.graphics.Color
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.scene.destroy
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.node.Node

class ARHelper {
    fun createAnchorNode(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        anchor: Anchor,
        model: String
    ): AnchorNode {
        val anchorNode = AnchorNode(engine = engine, anchor = anchor)
        val modelNode = createModelNode(modelLoader = modelLoader, model = model)
        //createCubeNode(engine,modelNode,materialLoader, anchorNode)
        anchorNode.addChildNode(modelNode)
        return anchorNode
    }

    private fun createModelNode(modelLoader: ModelLoader, model: String): ModelNode {
        return ModelNode(
            modelInstance = modelLoader.createModelInstance(model),
            // Scale to fit in a 0.5 meters cube
            //scaleToUnits = 0.5f

        ).apply {
            // Model Node needs to be editable for independent rotation from the anchor rotation
            //Fixed Model
            isEditable = true
            isPositionEditable = true
            isRotationEditable = false
            isScaleEditable = false
        }
    }

    fun clearAnchorsAndNodes(childNodes:MutableList<Node>, anchor: Anchor?) {
        if (childNodes.isNotEmpty()) {
            childNodes.clear()
            anchor?.detach()
            anchor?.destroy()
        }
    }

    fun createCubeNode(
        engine: Engine,
        modelNode: ModelNode,
        materialLoader: MaterialLoader,
        anchorNode: AnchorNode

    ){
        val boundingBoxNode = CubeNode(
            engine,
            size = modelNode.extents,
            center = modelNode.center,
            materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
        ).apply {
            isVisible = false
        }
        modelNode.addChildNode(boundingBoxNode)


        listOf(modelNode, anchorNode).forEach {
            it.onEditingChanged = { editingTransforms ->
                boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
            }
        }
    }
}