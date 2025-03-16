package com.example.furnitures_app

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CustomDialog(
    dialogModel: DialogModel
) {
    AlertDialog(
        icon = {
            dialogModel.icon?.let { Icon(it, contentDescription = dialogModel.iconDescription?: "") }
        },

        title = {
            dialogModel.title?.let { Text(it) }
        },
        text = {
            Text(text = dialogModel.message)
        },
        onDismissRequest = {
            dialogModel.onDismiss?.let { it() }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    dialogModel.onConfirm()
                }
            ) {
                Text(dialogModel.confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    dialogModel.onDismiss?.let { it() }
                }
            ) {
                Text(dialogModel.dismissText)
            }
        }
    )
}

data class DialogModel(
    val title: String?,
    val icon:  ImageVector?,
    val iconDescription: String?,
    val message: String,
    val confirmText: String,
    val dismissText: String = "Cancel",
    val onConfirm: () -> Unit,
    val onDismiss: (() -> Unit)?,
)