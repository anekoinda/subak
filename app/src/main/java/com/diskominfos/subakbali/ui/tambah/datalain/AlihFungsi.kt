package com.diskominfos.subakbali.ui.tambah.datalain

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
import com.diskominfos.subakbali.adapter.AlihFungsiAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAlihFungsiBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AlihFungsi : AppCompatActivity() {
    private lateinit var binding: ActivityAlihFungsiBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private val _alihFungsiList = MutableLiveData<MutableList<GetAllAlihFungsi>>()
    val alihFungsiList: LiveData<MutableList<GetAllAlihFungsi>> = _alihFungsiList
    private var getIdSubak: String? = ""
    private var token: String = ""
    private lateinit var setAdapterAlihFungsi: AlihFungsiAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlihFungsiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Alih Fungsi"

        setupRecyclerAlihFungsi()
        setupViewModel()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent

        val bundle: Bundle? = intent.extras
        Log.e("id sumber airrr", "$getIdSubak")

        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnAddAlihFungsi.setOnClickListener {
                Log.e("id subak alih fungsi", "$getIdSubak")
                val intent = Intent(this, AddAlihFungsi::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }

        if (bundle != null) {
            Log.e("id subak alih fungsi", "$getIdSubak")
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                Log.e("data id", "$getIdSubak")
//                val intent = Intent(this, Posting::class.java).apply{
//                    putExtra("idsubak", idtempsubak)
//                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupRecyclerAlihFungsi() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvAlihFungsi.layoutManager = layoutManager
        binding.rvAlihFungsi.setHasFixedSize(true)
    }

    private fun setupViewModel(){
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            token = it
            if (it != "") {
                getListAlihFungsi(it)
                alihFungsiList.observe(this) { alihfungsi ->
                    getList(alihfungsi)
                    Log.d("error", "list alihfungsi: $alihfungsi")
                    if (alihfungsi != null && alihfungsi.isNotEmpty()) {
                        binding.emptyObjectAlihFungsi.visibility = View.GONE
                    } else {
                        binding.emptyObjectAlihFungsi.visibility = View.VISIBLE
                    }
                    Log.d("error", "set upViewModel: $alihfungsi")
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    private fun getListAlihFungsi(token: String) {
        val client = ApiConfig.getApiService().getListAlihFungsi("Bearer $token", "$getIdSubak")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<GetAlihFungsiResponse> {
            override fun onResponse(call: Call<GetAlihFungsiResponse>, response: Response<GetAlihFungsiResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _alihFungsiList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetAlihFungsiResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getList(alihfungsi: MutableList<GetAllAlihFungsi>) {
        val alihFungsiAdapter = AlihFungsiAdapter(alihfungsi,
            object : AlihFungsiAdapter.OnAdapterAlihFungsiListener {
                override fun onClick(result: GetAllAlihFungsi) {
                    val bundle = Bundle()
                    val intent = Intent(this@AlihFungsi, DetailAlihFungsi::class.java)
                    bundle.putString("id", result.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, token
        )
        binding.rvAlihFungsi.adapter = alihFungsiAdapter
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

        setAdapterAlihFungsi = alihfungsi.let {
            AlihFungsiAdapter(it,
                object : AlihFungsiAdapter.OnAdapterAlihFungsiListener {
                    override fun onClick(result: GetAllAlihFungsi) {
                        val bundle = Bundle()
                        val intent = Intent(this@AlihFungsi, DetailAlihFungsi::class.java)
                        bundle.putString("id", result.id)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }, token)
        }
    }
}