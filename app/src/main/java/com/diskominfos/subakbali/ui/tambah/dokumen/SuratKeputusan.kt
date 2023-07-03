package com.diskominfos.subakbali.ui.tambah.dokumen

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diskominfos.subakbali.adapter.SuratKeputusanAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivitySuratKeputusanBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SuratKeputusan : AppCompatActivity() {
    private lateinit var binding: ActivitySuratKeputusanBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private val _suratKeputusanList = MutableLiveData<MutableList<GetAllSuratKeputusan>>()
    val suratKeputusanList: LiveData<MutableList<GetAllSuratKeputusan>> = _suratKeputusanList
    private var getIdSubak: String? = ""
    private var token: String = ""
    private lateinit var setAdapterSuratKeputusan: SuratKeputusanAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuratKeputusanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Surat Keputusan"

        setupRecyclerSuratKeputusan()
        setupViewModel()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent

        val bundle: Bundle? = intent.extras
        Log.e("id sumber airrr", "$getIdSubak")

        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnAddSuratKeputusan.setOnClickListener {
                Log.e("id subak sk", "$getIdSubak")
                val intent = Intent(this, AddSuratKeputusan::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }

        if (bundle != null) {
            Log.e("id subak sk", "$getIdSubak")
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                Log.e("data id", "$getIdSubak")
                val intent = Intent(this, Awig::class.java).apply{
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupRecyclerSuratKeputusan() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvSk.layoutManager = layoutManager
        binding.rvSk.setHasFixedSize(true)
    }

    private fun setupViewModel(){
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            token = it
            if (it != "") {
                getListSuratKeputusan(it)
                suratKeputusanList.observe(this) { sk ->
                    getList(sk)
                    Log.d("error", "list sk: $sk")
                    if (sk != null && sk.isNotEmpty()) {
                        binding.emptyObjectSuratKeputusan.visibility = View.GONE
                    } else {
                        binding.emptyObjectSuratKeputusan.visibility = View.VISIBLE
                    }
                    Log.d("error", "set upViewModel: $sk")
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    private fun getListSuratKeputusan(token: String) {
        val client = ApiConfig.getApiService().getListSuratKeputusan("Bearer $token", "$getIdSubak")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<GetSuratKeputusanResponse> {
            override fun onResponse(call: Call<GetSuratKeputusanResponse>, response: Response<GetSuratKeputusanResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _suratKeputusanList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetSuratKeputusanResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getList(sk: MutableList<GetAllSuratKeputusan>) {
        val suratKeputusanAdapter = SuratKeputusanAdapter(sk,
            object : SuratKeputusanAdapter.OnAdapterSuratKeputusanListener {
                override fun onClick(result: GetAllSuratKeputusan) {
                    val bundle = Bundle()
                    val intent = Intent(this@SuratKeputusan, DetailSuratKeputusan::class.java)
                    bundle.putString("id", result.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, token
        )
        binding.rvSk.adapter = suratKeputusanAdapter
//        tempSubakAdapter.setOnClickCallBack(object : TempSubakAdapter.OnItemClickCallback {
//            override fun onClick(data: DataTempSubak) {
//                Intent(this@DraftSubak, DetailTempSubak::class.java).also {
//                    it.putExtra("id", data.id)
//                    Log.e("id frag", "$id")
//                    startActivity(it)
//                }
//                val bundle = Bundle()
//                val intent = Intent(this@DraftSubak, DetailTempSubak::class.java)
//                bundle.putString("id", data.id)
//                Log.e("id okama", data.id)
//                intent.putExtras(bundle)
//                startActivity(intent)
//            }
//        })

        setAdapterSuratKeputusan = sk.let {
            SuratKeputusanAdapter(it,
                object : SuratKeputusanAdapter.OnAdapterSuratKeputusanListener {
                    override fun onClick(result: GetAllSuratKeputusan) {
                        val bundle = Bundle()
                        val intent = Intent(this@SuratKeputusan, DetailSuratKeputusan::class.java)
                        bundle.putString("id", result.id)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }, token)
        }
    }
}