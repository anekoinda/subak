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
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.adapter.PeraremAdapter
import com.diskominfos.subakbali.adapter.SuratKeputusanAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityPeraremBinding
import com.diskominfos.subakbali.databinding.ActivitySuratKeputusanBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.sumberdana.SumberDana
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class Perarem : AppCompatActivity() {
    private lateinit var binding: ActivityPeraremBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private val _peraremList = MutableLiveData<MutableList<GetAllPerarem>>()
    val peraremList: LiveData<MutableList<GetAllPerarem>> = _peraremList
    private var getIdSubak: String? = ""
    private var token: String = ""
    private lateinit var setAdapterPerarem: PeraremAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPeraremBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Surat Keputusan"

        setupRecyclerPerarem()
        setupViewModel()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent

        val bundle: Bundle? = intent.extras
        Log.e("id sumber airrr", "$getIdSubak")

        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnAddPerarem.setOnClickListener {
                Log.e("id subak perarem", "$getIdSubak")
                val intent = Intent(this, AddPerarem::class.java).apply {
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
                val intent = Intent(this, SumberDana::class.java).apply{
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupRecyclerPerarem() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvPerarem.layoutManager = layoutManager
        binding.rvPerarem.setHasFixedSize(true)
    }

    private fun setupViewModel(){
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            token = it
            if (it != "") {
                getListPerarem(it)
                peraremList.observe(this) { perarem ->
                    getList(perarem)
                    Log.d("error", "list perarem: $perarem")
                    if (perarem != null && perarem.isNotEmpty()) {
                        binding.emptyObjectPerarem.visibility = View.GONE
                    } else {
                        binding.emptyObjectPerarem.visibility = View.VISIBLE
                    }
                    Log.d("error", "set upViewModel: $perarem")
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    private fun getListPerarem(token: String) {
        val client = ApiConfig.getApiService().getListPerarem("Bearer $token", "$getIdSubak")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<GetPeraremResponse> {
            override fun onResponse(call: Call<GetPeraremResponse>, response: Response<GetPeraremResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _peraremList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetPeraremResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getList(perarem: MutableList<GetAllPerarem>) {
        val peraremAdapter = PeraremAdapter(perarem,
            object : PeraremAdapter.OnAdapterPeraremListener {
                override fun onClick(result: GetAllPerarem) {
                    val bundle = Bundle()
                    val intent = Intent(this@Perarem, DetailPerarem::class.java)
                    bundle.putString("id", result.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, token
        )
        binding.rvPerarem.adapter = peraremAdapter
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

        setAdapterPerarem = perarem.let {
            PeraremAdapter(it,
                object : PeraremAdapter.OnAdapterPeraremListener {
                    override fun onClick(result: GetAllPerarem) {
                        val bundle = Bundle()
                        val intent = Intent(this@Perarem, DetailPerarem::class.java)
                        bundle.putString("id", result.id)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }, token)
        }
    }
}