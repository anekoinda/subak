package com.diskominfos.subakbali.ui.tambah.dataumum

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.*
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.model.SubakPreference
import com.diskominfos.subakbali.model.UserPreference
import kotlinx.coroutines.launch
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

    private val _desaAdatList = MutableLiveData<MutableList<DataDesaAdat>>()
    val desaAdatList: LiveData<MutableList<DataDesaAdat>> = _desaAdatList

    private val _jenisSumberDanaList = MutableLiveData<MutableList<DataJenisSumberDana>>()
    val jenisSumberDanaList: LiveData<MutableList<DataJenisSumberDana>> = _jenisSumberDanaList

    private val _jenisProdukList = MutableLiveData<MutableList<DataJenisProduk>>()
    val jenisProdukList: LiveData<MutableList<DataJenisProduk>> = _jenisProdukList

    private val _jenisUsahaList = MutableLiveData<MutableList<DataJenisUsaha>>()
    val jenisUsahaList: LiveData<MutableList<DataJenisUsaha>> = _jenisUsahaList

    private val _jenisPengelolaUsahaList = MutableLiveData<MutableList<DataJenisPengelolaUsaha>>()
    val jenisPengelolaUsahaList: LiveData<MutableList<DataJenisPengelolaUsaha>> = _jenisPengelolaUsahaList

    private val _sumberAirList = MutableLiveData<MutableList<DataSumberAir>>()
    val sumberAirList: LiveData<MutableList<DataSumberAir>> = _sumberAirList

    fun getUser(): LiveData<String> {
        return pref.getUser().asLiveData()
    }

    fun getUsername(): LiveData<String> {
        return pref.getUsername().asLiveData()
    }

    fun getId(): LiveData<String> {
        return pref.getId().asLiveData()
    }

    fun addSubak() {
        viewModelScope.launch {
            pref.addSubak()
        }
    }

    fun saveId(id: String) {
        viewModelScope.launch {
            pref.saveId(id)
        }
    }

    fun clearid() {
        viewModelScope.launch {
            pref.clearid()
        }
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

    fun getKecamatan(token: String, district_id : String) {
        val client = ApiConfig.getApiService().getKecamatanByKabupaten("Bearer $token", "$district_id")
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

    fun getDesDinas(token: String, id : String) {
        val client = ApiConfig.getApiService().getDesaByKecamatan("Bearer $token", "$id")
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

    fun getDesaAdat(token: String, id : String) {
        val client = ApiConfig.getApiService().getDesaAdatByKecamatan("Bearer $token", "$id")
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

    fun getJenisSumberDana(token: String) {
        val client = ApiConfig.getApiService().getAllJenisSumberDana("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<JenisSumberDanaResponse> {
            override fun onResponse(call: Call<JenisSumberDanaResponse>, response: Response<JenisSumberDanaResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _jenisSumberDanaList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<JenisSumberDanaResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getSumberAir(token: String) {
        val client = ApiConfig.getApiService().getSumberAir("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<SumberAirResponse> {
            override fun onResponse(call: Call<SumberAirResponse>, response: Response<SumberAirResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _sumberAirList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<SumberAirResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getJenisProduk(token: String) {
        val client = ApiConfig.getApiService().getAllJenisProduk("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<JenisProdukResponse> {
            override fun onResponse(call: Call<JenisProdukResponse>, response: Response<JenisProdukResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _jenisProdukList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<JenisProdukResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getJenisUsaha(token: String) {
        val client = ApiConfig.getApiService().getAllJenisUsaha("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<JenisUsahaResponse> {
            override fun onResponse(call: Call<JenisUsahaResponse>, response: Response<JenisUsahaResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _jenisUsahaList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<JenisUsahaResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getJenisPengelolaUsaha(token: String) {
        val client = ApiConfig.getApiService().getAllJenisPengelolaUsaha("Bearer $token")
        Log.e("token", token)
        client.enqueue(object : Callback<JenisPengelolaUsahaResponse> {
            override fun onResponse(call: Call<JenisPengelolaUsahaResponse>, response: Response<JenisPengelolaUsahaResponse>) {

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _jenisPengelolaUsahaList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<JenisPengelolaUsahaResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}