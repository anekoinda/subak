package com.diskominfos.subakbali.di

import android.content.Context
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.data.SubakRepository

object Injection {
    fun provideRepository(context: Context): SubakRepository {
        val apiService = ApiConfig.getApiService()
        return SubakRepository(apiService)
    }
}