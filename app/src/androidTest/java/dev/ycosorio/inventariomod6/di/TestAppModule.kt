package dev.ycosorio.inventariomod6.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.ycosorio.inventariomod6.data.local.ProductDao
import dev.ycosorio.inventariomod6.data.remote.ApiService
import io.mockk.coEvery
import io.mockk.mockk
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class, NetworkModule::class] // ¡Reemplaza los módulos reales!
)
object TestAppModule {

    // 1. Provee el DAO FALSO
    @Provides
    @Singleton
    fun provideFakeProductDao(): ProductDao {
        return FakeProductDao()
    }

    // 2. Provee un ApiService FALSO (Mockeado)
    // (No queremos que la API se llame en NINGÚN test)
    @Provides
    @Singleton
    fun provideFakeApiService(): ApiService {
        val mockApi = mockk<ApiService>()
        // Le decimos a MockK que cuando se llame a getAllProducts(), no devuelva nada.
        // (El DAO falso se encargará de los datos)
        coEvery { mockApi.getAllProducts() } returns emptyList()
        return mockApi
    }
}