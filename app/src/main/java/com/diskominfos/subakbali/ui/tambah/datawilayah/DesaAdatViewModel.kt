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

class DesaAdatViewModel(private val pref: UserPreference)  : ViewModel() {
    private val _desaAdatList = MutableLiveData<MutableList<DataDesaAdat>>()
    val desaAdatList: LiveData<MutableList<DataDesaAdat>> = _desaAdatList

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun getUsername(): LiveData<String> {
        return pref.getUsername().asLiveData()
    }

    fun getDesaAdat(token: String) {
        val client = ApiConfig.getApiService().getDesaAdat("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<DesaAdatResponse> {
            override fun onResponse(call: Call<DesaAdatResponse>, response: Response<DesaAdatResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _desaAdatList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<DesaAdatResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun deleteDataTempSubakDesaAdat(token: String, id: String) {
        val client = ApiConfig.getApiService().deleteDataTempSubakDesaAdat("Bearer $token", "$id")
        client.enqueue(object : Callback<DeleteTempSubakDesaAdatResponse> {
            override fun onResponse(call: Call<DeleteTempSubakDesaAdatResponse>, response: Response<DeleteTempSubakDesaAdatResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {

                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<DeleteTempSubakDesaAdatResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}