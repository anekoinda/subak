package com.diskominfos.subakbali.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diskominfos.subakbali.model.UserPreference
import kotlinx.coroutines.launch

class LoginViewModel(private val pref: UserPreference) : ViewModel(){
    fun login() {
        viewModelScope.launch {
            pref.login()
        }
    }

    fun saveUser(user: String) {
        viewModelScope.launch {
            pref.saveUser(user)
        }
    }

    fun saveUsername(user: String) {
        viewModelScope.launch {
            pref.saveUsername(user)
        }
    }

    fun saveName(user: String) {
        viewModelScope.launch {
            pref.saveName(user)
        }
    }
}