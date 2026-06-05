package com.sonusid.sekura.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {

    companion object {
        val USER_NAME = stringPreferencesKey("user_name")
        val SETUP_COMPLETE = booleanPreferencesKey("setup_complete")
        val LAST_SYNC_TIME = stringPreferencesKey("last_sync_time")
    }

    val userName: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[USER_NAME]
        }

    val isSetupComplete: Flow<Boolean> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[SETUP_COMPLETE] ?: false
        }

    val lastSyncTime: Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[LAST_SYNC_TIME]
        }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    suspend fun completeSetup() {
        context.dataStore.edit { preferences ->
            preferences[SETUP_COMPLETE] = true
        }
    }

    suspend fun updateLastSyncTime(time: String) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SYNC_TIME] = time
        }
    }
}
