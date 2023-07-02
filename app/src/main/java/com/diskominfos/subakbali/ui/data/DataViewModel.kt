package com.diskominfos.subakbali.ui.data

import android.content.ContentValues
import android.provider.ContactsContract.Data
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

class DataViewModel(private val pref: UserPreference) : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is data Fragment"
    }
    val text: LiveData<String> = _text

    private val _subakList = MutableLiveData<MutableList<DataSubak>>()
    val subakList: LiveData<MutableList<DataSubak>> = _subakList

    private val _tempSubakList = MutableLiveData<MutableList<DataTempSubak>>()
    val tempSubakList: LiveData<MutableList<DataTempSubak>> = _tempSubakList

    private val _tradisiSubakList = MutableLiveData<MutableList<GetAllDataTradisi>>()
    val tradisiSubakList: LiveData<MutableList<GetAllDataTradisi>> = _tradisiSubakList

    private val _sumberDanaSubakList = MutableLiveData<MutableList<GetAllSumberDana>>()
    val sumberDanaSubakList: LiveData<MutableList<GetAllSumberDana>> = _sumberDanaSubakList

    private val _kabSubakList = MutableLiveData<MutableList<DataKabupatenTempSubak>>()
    val kabSubakList: LiveData<MutableList<DataKabupatenTempSubak>> = _kabSubakList

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun getSubak(token: String) {
        val client = ApiConfig.getApiService().getAllSubak("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<SubakResponse> {
            override fun onResponse(call: Call<SubakResponse>, response: Response<SubakResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _subakList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<SubakResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getTempSubak(token: String, id: String?) {
        val client = ApiConfig.getApiService().getDataTempSubak("Bearer $token", "$id")
        client.enqueue(object : Callback<TempSubakResponse> {
            override fun onResponse(call: Call<TempSubakResponse>, response: Response<TempSubakResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _tempSubakList.value = response.body()!!.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<TempSubakResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun deleteDataTempSubak(token: String, id: String) {
        val client = ApiConfig.getApiService().deleteDataTempSubak("Bearer $token", "$id")
        client.enqueue(object : Callback<DeleteTempSubakResponse> {
            override fun onResponse(call: Call<DeleteTempSubakResponse>, response: Response<DeleteTempSubakResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {

                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<DeleteTempSubakResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getListTempSubak(token: String) {
        val client = ApiConfig.getApiService().getListTempSubak("Bearer $token")
        client.enqueue(object : Callback<TempSubakResponse> {
            override fun onResponse(call: Call<TempSubakResponse>, response: Response<TempSubakResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _tempSubakList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<TempSubakResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getTradisi(token: String, id: String?) {
        val client = ApiConfig.getApiService().getDataTradisi("Bearer $token", "$id")
        client.enqueue(object : Callback<GetTradisiLainResponse> {
            override fun onResponse(call: Call<GetTradisiLainResponse>, response: Response<GetTradisiLainResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _tradisiSubakList.value = response.body()!!.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetTradisiLainResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getSumberDana(token: String, id: String?) {
        val client = ApiConfig.getApiService().getDataSumberDana("Bearer $token", "$id")
        client.enqueue(object : Callback<GetSumberDanaResponse> {
            override fun onResponse(call: Call<GetSumberDanaResponse>, response: Response<GetSumberDanaResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _sumberDanaSubakList.value = response.body()!!.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetSumberDanaResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun deleteDataTradisi(token: String, id: String) {
        val client = ApiConfig.getApiService().deleteDataTradisiSubak("Bearer $token", "$id")
        client.enqueue(object : Callback<TradisiResponse> {
            override fun onResponse(call: Call<TradisiResponse>, response: Response<TradisiResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {

                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<TradisiResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun deleteSumberDana(token: String, id: String) {
        val client = ApiConfig.getApiService().deleteDataSumberDana("Bearer $token", "$id")
        client.enqueue(object : Callback<GetSumberDanaResponse> {
            override fun onResponse(call: Call<GetSumberDanaResponse>, response: Response<GetSumberDanaResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {

                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetSumberDanaResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }


}