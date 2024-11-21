package com.app.intermediatesubmission.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.app.intermediatesubmission.di.models.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreferences(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: UserModel){
        dataStore.edit { pref ->
            pref[NAME_KEY] = user.name
            pref[TOKEN_KEY] = user.token
            pref[IS_LOGIN_KEY] = true
        }
    }

    fun getSession(): Flow<UserModel> {
        return dataStore.data.map { pref ->
            UserModel(
                pref[NAME_KEY] ?: "",
                pref[TOKEN_KEY] ?: "",
                pref[IS_LOGIN_KEY] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { pref ->
            pref.clear()
        }
    }

    companion object {
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")
    }
}