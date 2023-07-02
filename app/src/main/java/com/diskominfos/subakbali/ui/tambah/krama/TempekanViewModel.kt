package com.diskominfos.subakbali.ui.tambah.krama

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.model.UserPreference
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TempekanViewModel(private val pref: UserPreference)  : ViewModel() {
    private val _tempekanList = MutableLiveData<MutableList<DataTempekan>>()
    val tempekanList: LiveData<MutableList<DataTempekan>> = _tempekanList

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun getUsername(): LiveData<String> {
        return pref.getUsername().asLiveData()
    }

    fun getTempekan(token: String) {
        val client = ApiConfig.getApiService().getTempekan("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<TempekanResponse> {
            override fun onResponse(call: Call<TempekanResponse>, response: Response<TempekanResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _tempekanList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<TempekanResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}