package com.diskominfos.subakbali.ui.tambah.sumberdana

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diskominfos.subakbali.adapter.SumberDanaAdapter
import com.diskominfos.subakbali.adapter.TradisiSubakAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivitySumberDanaBinding
import com.diskominfos.subakbali.databinding.ActivityTradisiBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.parahyangan.AddTradisi
import com.diskominfos.subakbali.ui.tambah.sumberair.AddSumberAirSubak
import com.diskominfos.subakbali.ui.tambah.sumberair.SumberAir
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SumberDana : AppCompatActivity() {
    private lateinit var binding: ActivitySumberDanaBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var token: String = ""
    private val _sumberDanaList = MutableLiveData<MutableList<GetAllSumberDana>>()
    val sumberDanaList: LiveData<MutableList<GetAllSumberDana>> = _sumberDanaList
    private var getIdSubak: String? = ""
    private lateinit var setAdapterSumberDana: SumberDanaAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySumberDanaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Sumber Dana"

        setupRecyclerSumberDana()
        setupViewModel()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent

        val bundle: Bundle? = intent.extras
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnAddSumberDana.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                Log.e("id subak tradisi", "$getIdSubak")
                val intent = Intent(this, AddSumberDana::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }
        }

        if (bundle != null) {
            Log.e("id subak tradisi", "$getIdSubak")
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                Log.e("data id", "$getIdSubak")
                val intent = Intent(this, SumberAir::class.java).apply{
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }
            binding.btnLewati.setOnClickListener {
                finish()
            }
        }
    }

    private fun setupRecyclerSumberDana() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvSumberDana.layoutManager = layoutManager
        binding.rvSumberDana.setHasFixedSize(true)
    }

    private fun setupViewModel(){
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            token = it
            if (it != "") {
                getListSumberDana(it)
                sumberDanaList.observe(this) { sumberDana ->
                    getList(sumberDana)
                    if (sumberDana != null && sumberDana.isNotEmpty()) {
                          binding.emptyObjectSumberDana.visibility = View.GONE
                    } else {
                        binding.emptyObjectSumberDana.visibility = View.VISIBLE
                    }
                    Log.d("error", "set upViewModel: $sumberDana")
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    private fun getListSumberDana(token: String) {
        val client = ApiConfig.getApiService().getListSumberDana("Bearer $token", "$getIdSubak")
//        val client = ApiConfig.getApiService().getListTempSubakDesaAdat("Bearer $token")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<GetSumberDanaResponse> {
            override fun onResponse(call: Call<GetSumberDanaResponse>, response: Response<GetSumberDanaResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _sumberDanaList.value = response.body()?.data
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

    private fun getList(sumberDana: MutableList<GetAllSumberDana>) {
        val sumberDanaAdapter = SumberDanaAdapter(sumberDana,
            object : SumberDanaAdapter.OnAdapterSumberDanaListener {
                override fun onClick(result: GetAllSumberDana) {
                    val bundle = Bundle()
                    val intent = Intent(this@SumberDana, DetailSumberDana::class.java)
                    bundle.putString("id", result.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, token
        )
        binding.rvSumberDana.adapter = sumberDanaAdapter
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

        setAdapterSumberDana = sumberDana.let {
            SumberDanaAdapter(it,
                object : SumberDanaAdapter.OnAdapterSumberDanaListener {
                    override fun onClick(result: GetAllSumberDana) {
                        val bundle = Bundle()
                        val intent = Intent(this@SumberDana, DetailSumberDana::class.java)
                        bundle.putString("id", result.id)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }, token)
        }
    }
}