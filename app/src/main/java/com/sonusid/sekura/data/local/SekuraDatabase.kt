package com.sonusid.sekura.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

@Database(entities = [AccountEntity::class], version = 1, exportSchema = false)
abstract class SekuraDatabase : RoomDatabase() {
    abstract fun accountDao(): AccountDao

    companion object {
        @Volatile
        private var INSTANCE: SekuraDatabase? = null

        // In a real application, this should be retrieved from a secure storage like Keystore
        // or derived from a user's master password.
        private val DATABASE_PASSPHRASE = "sekura_secure_default_key".toByteArray()

        fun getDatabase(context: Context): SekuraDatabase {
            return INSTANCE ?: synchronized(this) {
                // Ensure native library is loaded
                System.loadLibrary("sqlcipher")
                
                val factory = SupportOpenHelperFactory(DATABASE_PASSPHRASE)
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SekuraDatabase::class.java,
                    "sekura_database"
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
