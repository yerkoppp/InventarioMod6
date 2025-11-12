package dev.ycosorio.inventariomod6.ui.view.screens

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import dev.ycosorio.inventariomod6.MainCoroutineRule
import dev.ycosorio.inventariomod6.data.model.Product
import dev.ycosorio.inventariomod6.data.repository.ProductRepository
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductViewModelTest {

    // 1. Reglas
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule() // Para StateFlow

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule() // Para viewModelScope

    // 2. Mocks y Sujeto de Pruebas
    private lateinit var viewModel: ProductViewModel
    private lateinit var fakeRepository: ProductRepository // Usamos un mock

    // 3. Datos de Prueba
    private val productConStock = Product(id = 1, name = "Producto Test", stock = 10, price = 9.99)
    private val productSinStock = Product(id = 2, name = "Producto Cero", stock = 0, price = 1.99)

    @Before
    fun setUp() {
        // Creamos un repositorio mock (falso)
        fakeRepository = mockk<ProductRepository>()

        // Programamos el mock
        // Cuando se llame a isDatabaseEmpty(), que devuelva 'true'
        coEvery { fakeRepository.isDatabaseEmpty() } returns true
        // Cuando se llame a refreshProductsFromServer(), no hagas nada
        coEvery { fakeRepository.refreshProductsFromServer() } just runs
        // Cuando se pida 'allProducts', devuelve un Flow vacío
        coEvery { fakeRepository.allProducts } returns flowOf(emptyList())

        // Inicializamos el ViewModel con el repositorio falso
        viewModel = ProductViewModel(fakeRepository)
    }

    // --- TEST 1 (Lógica de Negocio: Eliminar con Stock) ---
    @Test
    fun `requestDelete con producto que tiene stock MUESTRA error y NO pide eliminar`() = runTest {
        // GIVEN (Dado) un producto con stock (10)

        // WHEN (Cuando) se intenta eliminar
        viewModel.requestDelete(productConStock)

        // THEN (Entonces)
        // 1. El estado debe reflejar un error
        val error = viewModel.uiState.value.error
        assertNotNull(error)
        assertTrue(error!!.contains("El stock debe ser 0"))

        // 2. El estado de 'productToDelete' debe ser nulo (no se preparó para borrar)
        val productToDelete = viewModel.productToDelete.value
        assertThat(productToDelete).isNull()
    }

    // --- TEST 2 (Lógica de Negocio: Eliminar sin Stock) ---
    @Test
    fun `requestDelete con producto sin stock NO muestra error y PIDE eliminar`() = runTest {
        // GIVEN (Dado) un producto sin stock (0)

        // WHEN (Cuando) se intenta eliminar
        viewModel.requestDelete(productSinStock)

        // THEN (Entonces)
        // 1. El estado NO debe tener error
        val error = viewModel.uiState.value.error
        assertNull(error)

        // 2. El estado de 'productToDelete' debe tener el producto
        val productToDelete = viewModel.productToDelete.value
        assertEquals(productSinStock, productToDelete)
    }

    // --- TEST 3 (Lógica de Negocio: Ajuste de Stock) ---
    @Test
    fun `onAdjustStock con disminución válida ACTUALIZA el producto`() = runTest {
        // GIVEN (Dado)
        val productoOriginal = Product(id = 5, name = "Ajuste", stock = 20, price = 10.0)
        val cantidadAAjustar = "5"
        val esIncremento = false // (Disminución)

        // Preparamos el mock para que acepte la llamada de 'update'
        coEvery { fakeRepository.updateProduct(any()) } just runs

        // Creamos un "slot" de MockK para capturar el argumento
        val productSlot = slot<Product>()

        // Ponemos el producto en el sheet de ajuste
        viewModel.onRequestAdjustStock(productoOriginal)

        // WHEN (Cuando) llamamos a la función de ajuste
        viewModel.onAdjustStock(cantidadAAjustar, esIncremento)

        // THEN (Entonces)
        // Verificamos que el repositorio fue llamado para ACTUALIZAR
        // con el stock NUEVO (20 - 5 = 15)
        coVerify {
            fakeRepository.updateProduct(capture(productSlot))
        }
        // Aserción sobre el valor capturado
        val expectedStock = 15
        assertEquals(expectedStock, productSlot.captured.stock)
        assertEquals(productoOriginal.id, productSlot.captured.id)
    }
}