package com.diskominfos.subakbali.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.auth.LoginViewModel

class ViewModelFactory (private val pref: UserPreference, private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
//                modelClass.isAssignableFrom(MainViewModel::class.java) -> {
//                    MainViewModel(pref, Injection.provideRepository(context)) as T
//                }
//                modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
//                    RegisterViewModel(pref) as T
//                }
                modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                    LoginViewModel(pref) as T
                }
                else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
            }
        }
}