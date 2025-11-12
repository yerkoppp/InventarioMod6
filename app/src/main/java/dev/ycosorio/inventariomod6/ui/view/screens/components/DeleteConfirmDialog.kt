package dev.ycosorio.inventariomod6.ui.view.screens.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun DeleteConfirmationDialog(
    productName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,

        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Advertencia",
                tint = MaterialTheme.colorScheme.error
            )
        },

        title = {
            Text("Confirmar Eliminación")
        },

        text = {
            Text("¿Estás seguro de que deseas eliminar el producto \"$productName\"" +
                    "? Esta acción no se puede deshacer.")
        },

        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                }
            ) {
                Text(
                    "Eliminar",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        },

        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                }
            ) {
                Text("Cancelar")
            }
        }
    )
}