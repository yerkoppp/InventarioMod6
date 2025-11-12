package dev.ycosorio.inventariomod6.ui.view.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ycosorio.inventariomod6.data.model.Product
import dev.ycosorio.inventariomod6.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = _uiState.asStateFlow()

    private val _productToAdjust = MutableStateFlow<Product?>(null)
    val productToAdjust: StateFlow<Product?> = _productToAdjust.asStateFlow()

    private val _productToDelete = MutableStateFlow<Product?>(null)

    val productToDelete: StateFlow<Product?> = _productToDelete.asStateFlow()

    private val _isSheetOpen = MutableStateFlow(false)

    val isSheetOpen: StateFlow<Boolean> = _isSheetOpen.asStateFlow()

    private val _editingProduct = MutableStateFlow<Product?>(null)

    init {
        observeProductsFromDb()
        viewModelScope.launch {
            if (productRepository.isDatabaseEmpty()) {
                refreshProducts()
            }
        }
    }

    private fun observeProductsFromDb() {
        viewModelScope.launch {
            productRepository.allProducts.collect { productList ->
                _uiState.update { currentState ->
                    currentState.copy(products = productList)
                }
            }
        }
    }

    fun refreshProducts() {
        viewModelScope.launch {
            // Empezamos la carga
            _uiState.update { it.copy(isLoading = true) }

            try {
                productRepository.refreshProductsFromServer()

                // Si tiene éxito, `observeProductsFromDb` recibirá los datos.
                // No necesitamos hacer nada más aquí.

            } catch (e: Exception) {
                // Si falla, guardamos el mensaje de error
                _uiState.update {
                    it.copy(error = "Error al actualizar productos: ${e.message}")
                }
            } finally {
                // En cualquier caso (éxito o fallo), detenemos la carga.
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            try {
                productRepository.addProduct(product)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al añadir producto: ${e.message}") }
            }
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch {
            try {
                productRepository.updateProduct(product)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al actualizar producto: ${e.message}") }
            }
        }
    }

    fun requestDelete(product: Product) {

        if (product.stock != 0) {
            _uiState.update {
                it.copy(error = "No se puede eliminar: El stock debe ser 0.")
            }
            return
        }
        _productToDelete.value = product
    }
    fun confirmDelete() {
        _productToDelete.value?.let { product ->
            viewModelScope.launch {
                productRepository.deleteProduct(product)
            }
        }
        dismissDeleteDialog() // Limpiamos el estado
    }
    fun dismissDeleteDialog() {
        _productToDelete.value = null
    }

    fun dismissError() {
        _uiState.update { it.copy(error = null) }
    }

    fun onAddNewProductClick() {
        _editingProduct.value = null
        _isSheetOpen.value = true
    }

    fun onRequestAdjustStock(product: Product) {
        _editingProduct.value = null
        _isSheetOpen.value = false
        _productToAdjust.value = product
    }

    fun onDismissAdjustSheet() {
        _productToAdjust.value = null
        dismissError()
    }

    fun onDismissSheet() {
        _isSheetOpen.value = false
        _editingProduct.value = null
        dismissError()
    }

    fun onAdjustStock(quantityStr: String, isIncrement: Boolean) {
        val quantity = quantityStr.toIntOrNull()
        val product = _productToAdjust.value

        // 1. Validaciones
        if (product == null) return // No debería pasar
        if (quantity == null || quantity <= 0) {
            _uiState.update { it.copy(error = "Cantidad debe ser un número positivo.") }
            return
        }

        // 2. Cálculo
        val currentStock = product.stock ?: 0
        val newStock = if (isIncrement) {
            currentStock + quantity
        } else if(quantity > currentStock){
            _uiState.update { it.copy(error = "No se puede disminuir. La cantidad excede el stock actual.") }
            return
        } else {
            currentStock - quantity
        }

        // 3. Validación de existencias
        if (newStock < 0) {
            _uiState.update { it.copy(error = "No se puede disminuir. El stock no puede ser negativo.") }
            return
        }

        // 4. Guardar
        viewModelScope.launch {
            try {
                // Usamos la función de 'update'
                updateProduct(product.copy(stock = newStock))
                onDismissAdjustSheet() // Cerramos el sheet si todo sale bien
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Error al ajustar el stock: ${e.message}") }
            }
        }
    }
    fun onSaveProduct(name: String, priceStr: String, stockStr: String) {
        // --- Validación Simple ---
        val price = priceStr.toDoubleOrNull()
        val stock = stockStr.toIntOrNull()

        if (name.isBlank() || price == null || stock == null) {
            _uiState.update {
                it.copy(error = "Datos inválidos. Revisa nombre, precio y stock.")
            }
            return
        }
        viewModelScope.launch {
            try {
                val productToSave = Product(
                    name = name,
                    price = price,
                    stock = stock
                )
                addProduct(productToSave)
                onDismissSheet()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Error al guardar el producto: ${e.message}")
                }
            }
        }
    }
}
data class ProductUiState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
