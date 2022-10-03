package com.diskominfos.subakbali.ui.pemetaan

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PemetaanViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is pemetaan Fragment"
    }
    val text: LiveData<String> = _text
}
