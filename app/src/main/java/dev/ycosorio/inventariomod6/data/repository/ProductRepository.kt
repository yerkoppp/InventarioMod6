package dev.ycosorio.inventariomod6.data.repository

import dev.ycosorio.inventariomod6.data.local.ProductDao
import dev.ycosorio.inventariomod6.data.model.Product
import dev.ycosorio.inventariomod6.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepository @Inject constructor (
    private val productDao: ProductDao,
    private val apiService: ApiService
){
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    suspend fun addProduct(product: Product) {
        productDao.addProduct(product)
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

    //Funciones de actualización desde API
    suspend fun refreshProductsFromServer(){

        val response = apiService.getAllProducts()

        val productsList: List<Product> = response.map {product ->
            Product(
                id = product.id,
                name = product.name,
                price = product.price,
                stock = (10..100).random()
            )
        }
        productDao.insertAllProducts(productsList)
    }

    suspend fun isDatabaseEmpty(): Boolean {
        return productDao.getProductCount() == 0
    }
}