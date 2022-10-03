package com.diskominfos.subakbali.ui.tambah

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class JenisViewModel: ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is tambah jenis Fragment"
    }
    val text: LiveData<String> = _text
}