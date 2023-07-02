package com.diskominfos.subakbali.model

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubakPreference private constructor(private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>) {
    fun getId(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ID] ?: ""
        }
    }

    suspend fun saveId(id: String) {
        dataStore.edit { preferences ->
            val id = id.split("|")
            preferences[ID] = id[1]
            Log.e("cek id", preferences[ID].toString())
        }
    }
    suspend fun addSubak() {
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = true
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SubakPreference? = null
        private val STATE_KEY = booleanPreferencesKey("state")
        private val ID = stringPreferencesKey("id")

        fun getInstance(dataStore: DataStore<androidx.datastore.preferences.core.Preferences>): SubakPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = SubakPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}