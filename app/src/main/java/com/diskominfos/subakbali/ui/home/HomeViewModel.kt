package com.diskominfos.subakbali.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diskominfos.subakbali.data.SubakRepository
import com.diskominfos.subakbali.model.UserPreference

class HomeViewModel(private val pref: UserPreference)  : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is pemetaan Fragment"
    }
    val text: LiveData<String> = _text

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun getUsername(): LiveData<String> {
        return pref.getUsername().asLiveData()
    }
}