package com.sonusid.sekura

import android.app.Application
import com.sonusid.sekura.data.local.SekuraDatabase
import com.sonusid.sekura.data.repository.AccountRepositoryImpl
import com.sonusid.sekura.domain.repository.AccountRepository

class SekuraApplication : Application() {
    
    val database by lazy { SekuraDatabase.getDatabase(this) }
    val repository by lazy { AccountRepositoryImpl(database.accountDao()) }

    override fun onCreate() {
        super.onCreate()
        // Ensure native library is loaded at app startup
        System.loadLibrary("sqlcipher")
    }
}
