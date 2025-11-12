package dev.ycosorio.inventariomod6.ui.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.ycosorio.inventariomod6.data.model.Product
import dev.ycosorio.inventariomod6.ui.view.screens.components.DeleteConfirmationDialog
import dev.ycosorio.inventariomod6.ui.view.screens.components.ProductForm
import dev.ycosorio.inventariomod6.ui.view.screens.components.ProductItem
import dev.ycosorio.inventariomod6.ui.view.screens.components.StockAdjustSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ProductViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // --- RECOLECTAR ESTADOS DEL SHEET Y DIÁLOGO ---
    val isSheetOpen by viewModel.isSheetOpen.collectAsStateWithLifecycle()
    val productToDelete by viewModel.productToDelete.collectAsStateWithLifecycle()
    val productToAdjust by viewModel.productToAdjust.collectAsStateWithLifecycle()

    // --- LÓGICA DEL BOTTOM SHEET ---
    if (isSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissSheet
        ) {
            ProductForm(
                product = null,
                onSave = viewModel::onSaveProduct,
                onCancel = viewModel::onDismissSheet,
                error = uiState.error
            )
        }
    }

    // --- LÓGICA DEL SHEET DE AJUSTE ---
    productToAdjust?.let { product ->
        ModalBottomSheet(
            onDismissRequest = viewModel::onDismissAdjustSheet
        ) {
            StockAdjustSheet(
                product = product,
                onAdjust = viewModel::onAdjustStock,
                onCancel = viewModel::onDismissAdjustSheet,
                error = uiState.error
            )
        }
    }

    // --- LÓGICA DEL DIÁLOGO DE BORRADO ---
    productToDelete?.let { product ->
        DeleteConfirmationDialog(
            productName = product.name,
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::dismissDeleteDialog
        )
    }

    // --- LÓGICA DEL SNACKBAR DE ERROR ---
    LaunchedEffect(uiState.error) {
        uiState.error?.let { errorMsg ->
            snackbarHostState.showSnackbar(message = errorMsg)
            viewModel.dismissError()
        }
    }

    // ESTRUCTURA DE LA PANTALLA
    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Gestor de Inventario") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::onAddNewProductClick) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Producto")
            }
        }
    ) { paddingValues ->

        // CONTENIDO PRINCIPAL
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            // LÓGICA DE VISUALIZACIÓN
            when {
                uiState.isLoading && uiState.products.isEmpty() -> {
                    CircularProgressIndicator()
                }
                !uiState.isLoading && uiState.products.isEmpty() -> {
                    Text("No hay productos en el inventario.")
                }
                uiState.products.isNotEmpty() -> {
                    ProductList(
                        products = uiState.products,
                        onDeleteClick = viewModel::requestDelete,
                        onEditClick = viewModel::onRequestAdjustStock
                    )
                }
            }

            // Indicador de recarga (si está cargando pero ya hay datos)
            if (uiState.isLoading && uiState.products.isNotEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                )
            }
        }
    }
}


/**
 * Composable que renderiza la lista de productos.
 */
@Composable
private fun ProductList(
    products: List<Product>,
    onDeleteClick: (Product) -> Unit,
    onEditClick: (Product) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = products,
            key = { product -> product.id }
        ) { product ->
            ProductItem(
                product = product,
                onDeleteClick = { onDeleteClick(product) },
                onEditClick = { onEditClick(product) }
            )
        }
    }
}