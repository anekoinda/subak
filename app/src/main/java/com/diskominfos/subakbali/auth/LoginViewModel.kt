package com.diskominfos.subakbali.auth

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
}