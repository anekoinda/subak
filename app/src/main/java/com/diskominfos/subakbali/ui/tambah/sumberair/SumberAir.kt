package com.diskominfos.subakbali.ui.tambah.sumberair

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
import com.diskominfos.subakbali.adapter.SumberAirAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivitySumberAirBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.datalain.Produk
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class SumberAir : AppCompatActivity() {
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private lateinit var binding: ActivitySumberAirBinding
    var isTextInputLayoutClicked: Boolean = false
    private var token: String = ""
    private var getIdSubak: String? = ""
    private val _sumberAirList = MutableLiveData<MutableList<DataSumberAir>>()
    val sumberAirList: LiveData<MutableList<DataSumberAir>> = _sumberAirList
    private lateinit var setAdapterSumberAir: SumberAirAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySumberAirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Sumber Air"

        setupRecyclerSumberAir()
        setupViewModel()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent

        val bundle: Bundle? = intent.extras
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnAddSumberAirSubak.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                Log.e("id subak sumber air", "$getIdSubak")
                val intent = Intent(this, AddSumberAirSubak::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }
        }
        if (bundle != null) {
            Log.e("id subak sumber air", "$getIdSubak")
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                Log.e("data id", "$getIdSubak")
                val intent = Intent(this, Produk::class.java).apply {
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

    private fun setupRecyclerSumberAir() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvSumberAir.layoutManager = layoutManager
        binding.rvSumberAir.setHasFixedSize(true)
    }

    private fun setupViewModel() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            token = it
            if (it != "") {
                getListSumberAir(it)
                sumberAirList.observe(this) { sumberAir ->
                    getList(sumberAir)
                    if (sumberAir != null && sumberAir.isNotEmpty()) {
                        binding.emptyObjectSumberAir.visibility = View.GONE
                    } else {
                        binding.emptyObjectSumberAir.visibility = View.VISIBLE
                    }
                    Log.d("error", "set upViewModel: $sumberAir")
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    private fun getListSumberAir(token: String) {
        val client = ApiConfig.getApiService().getListSumberAir("Bearer $token", "$getIdSubak")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<SumberAirResponse> {
            override fun onResponse(
                call: Call<SumberAirResponse>,
                response: Response<SumberAirResponse>
            ) {
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

    private fun getList(sumberAir: MutableList<DataSumberAir>) {
        val sumberAirAdapter = SumberAirAdapter(
            sumberAir,
            object : SumberAirAdapter.OnAdapterSumberAirListener {
                override fun onClick(result: DataSumberAir) {
                    val bundle = Bundle()
                    val intent = Intent(this@SumberAir, DetailSumberAir::class.java)
                    bundle.putString("id", result.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, token
        )
        binding.rvSumberAir.adapter = sumberAirAdapter
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

        setAdapterSumberAir = sumberAir.let {
            SumberAirAdapter(it,
                object : SumberAirAdapter.OnAdapterSumberAirListener {
                    override fun onClick(result: DataSumberAir) {
                        val bundle = Bundle()
                        val intent = Intent(this@SumberAir, DetailSumberAir::class.java)
                        bundle.putString("id", result.id)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }, token
            )
        }
    }
}