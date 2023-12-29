package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("token")

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val userPreference = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { userPreference.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(userPreference, apiService)
    }
}