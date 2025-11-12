package dev.ycosorio.inventariomod6.di

import dev.ycosorio.inventariomod6.data.local.ProductDao
import dev.ycosorio.inventariomod6.data.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 * Una implementación FALSA del ProductDao para usar en tests.
 * Mantiene los productos en un Flow en memoria.
 */
class FakeProductDao : ProductDao {

    private val _productsFlow = MutableStateFlow<List<Product>>(emptyList())
    private var productsList = mutableListOf<Product>()

    override fun getAllProducts(): Flow<List<Product>> {
        return _productsFlow
    }

    // --- Funciones Falsas (simulamos la BD) ---

    override suspend fun insertAllProducts(products: List<Product>) {
        productsList.addAll(products)
        _productsFlow.value = productsList
    }

    override suspend fun addProduct(product: Product) {
        productsList.add(product.copy(id = (productsList.size + 1)))
        _productsFlow.value = productsList
    }

    override suspend fun updateProduct(product: Product) {
        val index = productsList.indexOfFirst { it.id == product.id }
        if (index != -1) {
            productsList[index] = product
            _productsFlow.value = productsList
        }
    }

    override suspend fun deleteProduct(product: Product) {
        productsList.remove(product)
        _productsFlow.value = productsList
    }

    // --- No implementadas (pero requeridas por la interfaz) ---
    override fun getProductById(id: Int): Flow<Product?> {
        return MutableStateFlow(productsList.find { it.id == id })
    }

    override suspend fun getProductCount(): Int {
        return productsList.size
    }

    // Helper para nuestros tests:
    fun clearDb() {
        productsList.clear()
        _productsFlow.value = emptyList()
    }
}