package com.diskominfos.subakbali.ui.tambah.datawilayah

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

class DesaDinasViewModel(private val pref: UserPreference)  : ViewModel() {
    private val _desaDinasList = MutableLiveData<MutableList<DataDesaDinas>>()
    val desaDinasList: LiveData<MutableList<DataDesaDinas>> = _desaDinasList

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun getUsername(): LiveData<String> {
        return pref.getUsername().asLiveData()
    }

    fun getDesaDinas(token: String) {
        val client = ApiConfig.getApiService().getDesaDinas("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<DesaDinasResponse> {
            override fun onResponse(call: Call<DesaDinasResponse>, response: Response<DesaDinasResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _desaDinasList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<DesaDinasResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}