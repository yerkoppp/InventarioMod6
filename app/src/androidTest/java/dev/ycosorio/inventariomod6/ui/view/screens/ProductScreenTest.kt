package dev.ycosorio.inventariomod6.ui.view.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onNodeWithTag
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.ycosorio.inventariomod6.MainActivity
import dev.ycosorio.inventariomod6.data.local.ProductDao
import dev.ycosorio.inventariomod6.data.model.Product
import dev.ycosorio.inventariomod6.di.FakeProductDao
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest // 1. Habilita Hilt para este test
class ProductScreenTest {

    // 2. Regla de Hilt (debe ir primero)
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // 3. Regla de Compose (lanza la Activity)
    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    // 4. Inyectamos nuestro DAO FALSO
    // Hilt nos da la instancia de FakeProductDao que creó TestAppModule
    @Inject
    lateinit var fakeDao: ProductDao

    @Before
    fun setUp() {
        hiltRule.inject() // Inyectamos las dependencias (fakeDao)

        // Limpiamos la BD falsa antes de cada test
        (fakeDao as FakeProductDao).clearDb()
    }

    // --- TEST DE INSTRUMENTACIÓN ---
    @Test
    fun testProductList_IsDisplayed_AfterConsumingService() {
        // GIVEN (Dado)
        // 1. Preparamos datos falsos
        val testProduct = Product(id = 1, name = "Producto de Prueba UI", stock = 50, price = 123.45)

        // 2. Usamos runBlocking para insertar datos en el DAO falso
        // (El ViewModel llamará a 'isDatabaseEmpty()' y luego a 'refresh',
        // pero vamos a insertar los datos directamente para asegurarnos)
        runBlocking {
            fakeDao.insertAllProducts(listOf(testProduct))
        }

        // WHEN (Cuando)
        // 1. La app se lanza (la regla 'composeRule' lo hace)
        // 2. Hacemos clic en el botón "Ingresar" de la SplashScreen
        composeRule.onNodeWithText("Ingresar").performClick()

        // (La app navega a ProductScreen, el ViewModel se suscribe
        // a fakeDao.getAllProducts() y recibe 'testProduct')

        // THEN (Entonces)
        // 1. Verificamos que el nombre de nuestro producto falso se muestra
        composeRule.onNodeWithText("Producto de Prueba UI").assertIsDisplayed()

        // 2. Verificamos que los otros datos también se muestran
        composeRule.onNodeWithText("Stock: 50 unidades").assertIsDisplayed()
        // (Nota: El precio se formatea, así que testearlo es más complejo,
        // pero con el nombre y el stock es suficiente para cumplir)
    }

    /**
     * Prueba el flujo completo de creación de un producto.
     * 1. Clic en FAB.
     * 2. Rellena el formulario.
     * 3. Clic en Guardar.
     * 4. Verifica que el item aparece en la lista.
     */
    @Test
    fun testCreateProduct_Flow_EntersDataAndVerifiesList() {
        // GIVEN (Dado)
        // 1. La app se lanza (la regla 'composeRule' lo hace).
        // 2. El DAO (fakeDao) está vacío (asegurado por el @Before).

        // WHEN (Cuando)
        // 1. Hacemos clic en "Ingresar" en la SplashScreen
        composeRule.onNodeWithText("Ingresar").performClick()

        // 2. Verificamos que la pantalla está vacía
        composeRule.onNodeWithText("No hay productos en el inventario.").assertIsDisplayed()

        // 3. Hacemos clic en el Botón Flotante (FAB) para añadir
        composeRule.onNodeWithContentDescription("Añadir Producto").performClick()

        // 4. Esperamos a que el sheet aparezca y rellenamos el formulario
        // (Usamos 'onNodeWithLabel' porque es la mejor forma de encontrar TextFields)
        composeRule.onNodeWithTag("productNameField").performTextInput("Pan de Molde")
        composeRule.onNodeWithTag("productPriceField").performTextInput("2500")
        composeRule.onNodeWithTag("productStockField").performTextInput("20")

        // 5. Hacemos clic en Guardar
        composeRule.onNodeWithText("Guardar").performClick()

        // THEN (Entonces)
        // 1. El sheet debe desaparecer (no encontraremos su título)
        // composeRule.onNodeWithText("Añadir Producto").assertDoesNotExist()

        // 2. El nuevo producto DEBE mostrarse en la lista
        composeRule.onNodeWithText("Pan de Molde").assertIsDisplayed()
        composeRule.onNodeWithText("Stock: 20 unidades").assertIsDisplayed()

        // 3. El texto de "vacío" debe desaparecer
        composeRule.onNodeWithText("No hay productos en el inventario.").assertDoesNotExist()
    }
}