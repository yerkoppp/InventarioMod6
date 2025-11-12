package dev.ycosorio.inventariomod6.ui.view.screens.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.ycosorio.inventariomod6.data.model.Product

/**
 * Sheet para Aumentar o Disminuir el stock de un producto.
 *
 * @param product El producto que se está ajustando.
 * @param onAdjust Función a llamar (pasa la cantidad y si es incremento).
 * @param onCancel Función a llamar si se cancela.
 */
@Composable
fun StockAdjustSheet(
    product: Product,
    onAdjust: (quantity: String, isIncrement: Boolean) -> Unit,
    onCancel: () -> Unit,
    error: String?
) {
    var quantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // --- Información del Producto ---
        Text("Ajustar Stock", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Producto: ${product.name}",
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            "Stock Actual: ${product.stock ?: 0} unidades",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(Modifier.height(8.dp))

        // --- BLOQUE DE ERROR ---
        if (error != null) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // --- Campo de Cantidad ---
        OutlinedTextField(
            value = quantity,
            onValueChange = { quantity = it },
            label = { Text("Cantidad a ajustar") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            singleLine = true
        )

        Spacer(Modifier.height(8.dp))

        // --- Botones de Acción ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Botón DISMINUIR
            Button(
                onClick = { onAdjust(quantity, false) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Disminuir")
            }

            // Botón AUMENTAR
            Button(
                onClick = { onAdjust(quantity, true) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Aumentar")
            }
        }
    }
}