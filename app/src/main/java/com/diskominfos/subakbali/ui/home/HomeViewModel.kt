package com.diskominfos.subakbali.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diskominfos.subakbali.data.SubakRepository
import com.diskominfos.subakbali.model.UserPreference

class HomeViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is pemetaan Fragment"
    }
    val text: LiveData<String> = _text
}