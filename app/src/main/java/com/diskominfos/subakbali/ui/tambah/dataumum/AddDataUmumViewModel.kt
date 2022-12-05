package com.diskominfos.subakbali.ui.tambah.dataumum

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

class AddDataUmumViewModel(private val pref: UserPreference)  : ViewModel() {
    private val _kabupatenList = MutableLiveData<MutableList<DataKabupaten>>()
    val kabupatenList: LiveData<MutableList<DataKabupaten>> = _kabupatenList

    private val _kecamatanList = MutableLiveData<MutableList<DataKecamatan>>()
    val kecamataList: LiveData<MutableList<DataKecamatan>> = _kecamatanList

    private val _desaList = MutableLiveData<MutableList<DataDesaDinas>>()
    val desaList: LiveData<MutableList<DataDesaDinas>> = _desaList

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun getKabupaten(token: String) {
        val client = ApiConfig.getApiService().getAllKabupaten("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<KabupatenResponse> {
            override fun onResponse(call: Call<KabupatenResponse>, response: Response<KabupatenResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _kabupatenList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<KabupatenResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getKecamatan(token: String) {
        val client = ApiConfig.getApiService().getAllKecamatan("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<KecamatanResponse> {
            override fun onResponse(call: Call<KecamatanResponse>, response: Response<KecamatanResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _kecamatanList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<KecamatanResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getDesDinas(token: String) {
        val client = ApiConfig.getApiService().getAllDesaDinas("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<DesaDinasResponse> {
            override fun onResponse(call: Call<DesaDinasResponse>, response: Response<DesaDinasResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _desaList.value = response.body()?.data
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