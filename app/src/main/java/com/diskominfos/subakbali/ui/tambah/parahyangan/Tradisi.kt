package com.diskominfos.subakbali.ui.tambah.parahyangan

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
import com.diskominfos.subakbali.adapter.PuraSubakAdapter
import com.diskominfos.subakbali.adapter.TradisiSubakAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAddDataWilayahBinding
import com.diskominfos.subakbali.databinding.ActivityTradisiBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.DetailTempSubak
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddDataWilayah
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddWilayahDesaDinas
import com.diskominfos.subakbali.ui.tambah.parahyangan.AddTradisi
import com.diskominfos.subakbali.ui.tambah.parahyangan.PuraViewModel
import com.diskominfos.subakbali.ui.tambah.sumberdana.SumberDana
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Tradisi : AppCompatActivity() {
    private lateinit var binding: ActivityTradisiBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var getIdSubak: String? = ""
    var idPuraSelected: String? = ""
    var isTextInputLayoutClicked: Boolean = false
    private val _tradisiSubakList = MutableLiveData<MutableList<GetAllDataTradisi>>()
    val tradisiSubakList: LiveData<MutableList<GetAllDataTradisi>> = _tradisiSubakList
    private lateinit var setAdapterTradisiSubak: TradisiSubakAdapter
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTradisiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tradisi Subak"

        setupRecyclerPuraSubak()
        setupViewModel()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent

        val bundle: Bundle? = intent.extras
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnAddTradisi.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                Log.e("id subak tradisi", "$getIdSubak")
                val intent = Intent(this, AddTradisi::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }
        }

        if (bundle != null) {
            Log.e("id subak tradisi", "$getIdSubak")
            val idtempsubak = getIdSubak
            binding.btnSimpan.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                Log.e("data id", "$getIdSubak")
                val intent = Intent(this, SumberDana::class.java).apply {
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

    private fun setupRecyclerPuraSubak() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvTradisi.layoutManager = layoutManager
        binding.rvTradisi.setHasFixedSize(true)
    }

    private fun getListTradisi(token: String) {
        val client = ApiConfig.getApiService().getListTradisi("Bearer $token", "$getIdSubak")
//        val client = ApiConfig.getApiService().getListTempSubakDesaAdat("Bearer $token")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<GetTradisiLainResponse> {
            override fun onResponse(
                call: Call<GetTradisiLainResponse>,
                response: Response<GetTradisiLainResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _tradisiSubakList.value = response.body()?.data
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

    private fun getList(tradisiSubak: MutableList<GetAllDataTradisi>) {
        val tradisiSubakAdapter = TradisiSubakAdapter(
            tradisiSubak,
            object : TradisiSubakAdapter.OnAdapterDataTradisiListener {
                override fun onClick(result: GetAllDataTradisi) {
                    val bundle = Bundle()
                    val intent = Intent(this@Tradisi, DetailTradisi::class.java)
                    bundle.putString("id", result.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, token
        )
        binding.rvTradisi.adapter = tradisiSubakAdapter
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

        setAdapterTradisiSubak = tradisiSubak.let {
            TradisiSubakAdapter(it,
                object : TradisiSubakAdapter.OnAdapterDataTradisiListener {
                    override fun onClick(result: GetAllDataTradisi) {
                        val bundle = Bundle()
                        val intent = Intent(this@Tradisi, DetailTradisi::class.java)
                        bundle.putString("id", result.id)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }, token
            )
        }
    }

    private fun setupViewModel() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            token = it
            if (it != "") {
                getListTradisi(it)
                tradisiSubakList.observe(this) { tradisiSubak ->
                    getList(tradisiSubak)
                    if (tradisiSubak != null && tradisiSubak.isNotEmpty()) {
                        binding.emptyObjectTradisi.visibility = View.GONE
                    } else {
                        binding.emptyObjectTradisi.visibility = View.VISIBLE
                    }
                    Log.d("error", "set upViewModel: $tradisiSubak")
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }
}