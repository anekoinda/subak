package com.diskominfos.subakbali.ui.data

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.api.DataSubak
import com.diskominfos.subakbali.api.SubakResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is data Fragment"
    }
    val text: LiveData<String> = _text

    private val _subakList = MutableLiveData<MutableList<DataSubak>>()
    val subakList: LiveData<MutableList<DataSubak>> = _subakList

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
}