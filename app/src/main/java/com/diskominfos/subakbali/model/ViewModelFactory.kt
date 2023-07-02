package com.diskominfos.subakbali.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.di.Injection
import com.diskominfos.subakbali.ui.auth.LoginViewModel
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.home.HomeViewModel
import com.diskominfos.subakbali.ui.profil.ProfilViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.datawilayah.DesaAdatViewModel
import com.diskominfos.subakbali.ui.tambah.datawilayah.DesaDinasViewModel
import com.diskominfos.subakbali.ui.tambah.krama.TempekanViewModel
import com.diskominfos.subakbali.ui.tambah.parahyangan.PuraViewModel

class ViewModelFactory(private val pref: UserPreference):
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ProfilViewModel::class.java) -> {
                ProfilViewModel(pref) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(pref) as T
            }
            modelClass.isAssignableFrom(AddDataUmumViewModel::class.java) -> {
                AddDataUmumViewModel(pref) as T
            }
            modelClass.isAssignableFrom(DataViewModel::class.java) -> {
                DataViewModel(pref) as T
            }
            modelClass.isAssignableFrom(DesaAdatViewModel::class.java) -> {
                DesaAdatViewModel(pref) as T
            }
            modelClass.isAssignableFrom(DesaDinasViewModel::class.java) -> {
                DesaDinasViewModel(pref) as T
            }
            modelClass.isAssignableFrom(TempekanViewModel::class.java) -> {
                TempekanViewModel(pref) as T
            }
            modelClass.isAssignableFrom(PuraViewModel::class.java) -> {
                PuraViewModel(pref) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}