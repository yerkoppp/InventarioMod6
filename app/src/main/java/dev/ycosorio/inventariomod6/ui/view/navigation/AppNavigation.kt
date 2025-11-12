package dev.ycosorio.inventariomod6.ui.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.ycosorio.inventariomod6.ui.view.screens.ProductScreen
import dev.ycosorio.inventariomod6.ui.view.screens.SplashScreen

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AppDestinations.Splash.route
    ) {

        composable(AppDestinations.Splash.route) {
            SplashScreen(navController = navController)
        }

        // Ruta 2: Pantalla de Inventario
        composable(AppDestinations.Inventory.route) {
            ProductScreen()
        }

    }
}


sealed class AppDestinations(val route: String) {
    /** Pantalla de Bienvenida (Splash) */
    object Splash : AppDestinations("splash_screen")

    /** Pantalla de Inventario (Productos) */
    object Inventory : AppDestinations("inventory_screen")
}