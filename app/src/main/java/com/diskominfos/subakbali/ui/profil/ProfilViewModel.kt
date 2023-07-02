package com.diskominfos.subakbali.ui.profil

import androidx.lifecycle.*
import com.diskominfos.subakbali.data.SubakRepository
import com.diskominfos.subakbali.model.UserPreference
import kotlinx.coroutines.launch

class ProfilViewModel(private val pref: UserPreference): ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is profil Fragment"
    }
    val text: LiveData<String> = _text
    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun getName(): LiveData<String> {
        return pref.getName().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            pref.logout()
        }
    }
}