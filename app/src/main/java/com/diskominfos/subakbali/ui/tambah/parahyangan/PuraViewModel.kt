package com.diskominfos.subakbali.ui.tambah.parahyangan

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

class PuraViewModel(private val pref: UserPreference) : ViewModel() {

    private val _puraList = MutableLiveData<MutableList<DataPura>>()
    val puraList: LiveData<MutableList<DataPura>> = _puraList

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun getUsername(): LiveData<String> {
        return pref.getUsername().asLiveData()
    }

    fun getPura(token: String) {
        val client = ApiConfig.getApiService().getPura("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<PuraResponse> {
            override fun onResponse(call: Call<PuraResponse>, response: Response<PuraResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _puraList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<PuraResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

}