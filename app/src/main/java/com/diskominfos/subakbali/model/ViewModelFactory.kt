package com.diskominfos.subakbali.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.di.Injection
import com.diskominfos.subakbali.ui.auth.LoginViewModel
import com.diskominfos.subakbali.ui.home.HomeViewModel
import com.diskominfos.subakbali.ui.profil.ProfilViewModel

class ViewModelFactory(private val pref: UserPreference, private val context: Context) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProfilViewModel::class.java) -> {
                ProfilViewModel(pref, Injection.provideRepository(context)) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}