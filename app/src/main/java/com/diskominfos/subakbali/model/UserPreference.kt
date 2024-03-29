package com.diskominfos.subakbali.model

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>) {
    fun getUser(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[TOKEN] ?: ""
        }
    }

    fun getUsername(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[USERNAME] ?: ""
        }
    }

    fun getName(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[NAME] ?: ""
        }
    }


    suspend fun saveUser(user: String) {
        dataStore.edit { preferences ->
            val token = user.split("|")
            preferences[TOKEN] = token[1]
            Log.e("cek token", preferences[TOKEN].toString())
        }
    }

    suspend fun saveUsername(user: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME] = user
            Log.e("cek username", preferences[USERNAME].toString())
        }
    }

    suspend fun saveName(user: String) {
        dataStore.edit { preferences ->
            preferences[NAME] = user
            Log.e("cek name", preferences[NAME].toString())
        }
    }

    fun getId(): Flow<String> {
        return dataStore.data.map { preferences ->
            preferences[ID] ?: ""
        }
    }

    suspend fun saveId(id: String) {
        dataStore.edit { preferences ->
            preferences[ID] = id
            Log.e("cek id", preferences[ID].toString())
        }
    }
    suspend fun addSubak() {
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = true
        }
    }

    suspend fun login() {
        dataStore.edit { preferences ->
            preferences[STATE_KEY] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[TOKEN] = ""
            preferences[STATE_KEY] = false
            Log.e("cek token", preferences[TOKEN].toString())
        }
    }

    suspend fun clearid() {
        dataStore.edit { preferences ->
            preferences[ID] = ""
            preferences[STATE_KEY] = false
            Log.e("cek id clear", preferences[ID].toString())
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null
        private val STATE_KEY = booleanPreferencesKey("state")
        private val TOKEN = stringPreferencesKey("token")
        private val USERNAME = stringPreferencesKey("username")
        private val NAME = stringPreferencesKey("name")
        private val ID = stringPreferencesKey("id")

        fun getInstance(dataStore: DataStore<androidx.datastore.preferences.core.Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}