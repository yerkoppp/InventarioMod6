package dev.ycosorio.inventariomod6.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.ycosorio.inventariomod6.data.ProductDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideProductDatabase(
        @ApplicationContext context: Context
    ) = ProductDatabase.getDatabase(context)

    @Provides
    fun provideProductDao(database: ProductDatabase) = database.productDao()

}