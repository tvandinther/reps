package com.tvandinther.reps.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AppSettings(private val context: Context) {

    companion object {
        private val KEY_SESSION_GAP_MINUTES = intPreferencesKey("session_gap_minutes")
        private val KEY_LEFT_HANDED = booleanPreferencesKey("left_handed")

        const val DEFAULT_SESSION_GAP_MINUTES = 90
    }

    val sessionGapMinutes: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_SESSION_GAP_MINUTES] ?: DEFAULT_SESSION_GAP_MINUTES
    }

    val isLeftHanded: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_LEFT_HANDED] ?: false
    }

    suspend fun setSessionGapMinutes(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_SESSION_GAP_MINUTES] = minutes
        }
    }

    suspend fun setLeftHanded(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LEFT_HANDED] = enabled
        }
    }
}
