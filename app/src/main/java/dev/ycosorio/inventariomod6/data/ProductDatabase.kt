package dev.ycosorio.inventariomod6.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.ycosorio.inventariomod6.data.local.ProductDao
import dev.ycosorio.inventariomod6.data.model.Product

@Database(
    entities = [Product::class],
    version = 2,
    exportSchema = false
)
abstract class ProductDatabase: RoomDatabase() {
    abstract fun productDao(): ProductDao

    companion object{

        @Volatile
        private var INSTANCE: ProductDatabase? = null

        fun getDatabase(context: android.content.Context): ProductDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = androidx.room.Room.databaseBuilder(
                    context.applicationContext,
                    ProductDatabase::class.java,
                    "app_database"
                ).setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }

}