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
import com.diskominfos.subakbali.adapter.AwigAdapter
import com.diskominfos.subakbali.adapter.SuratKeputusanAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAwigBinding
import com.diskominfos.subakbali.databinding.ActivitySuratKeputusanBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class Awig : AppCompatActivity() {
    private lateinit var binding: ActivityAwigBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private val _awigList = MutableLiveData<MutableList<GetAllAwig>>()
    val awigList: LiveData<MutableList<GetAllAwig>> = _awigList
    private var getIdSubak: String? = ""
    private var token: String = ""
    private lateinit var setAdapterAwig: AwigAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAwigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Awig-Awig"

        setupRecyclerAwig()
        setupViewModel()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent

        val bundle: Bundle? = intent.extras
        Log.e("id sumber airrr", "$getIdSubak")

        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnAddAwig.setOnClickListener {
                Log.e("id subak sk", "$getIdSubak")
                val intent = Intent(this, AddAwig::class.java).apply {
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
                val intent = Intent(this, Perarem::class.java).apply{
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupRecyclerAwig() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvAwig.layoutManager = layoutManager
        binding.rvAwig.setHasFixedSize(true)
    }

    private fun setupViewModel(){
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            token = it
            if (it != "") {
                getListAwig(it)
                awigList.observe(this) { awig ->
                    getList(awig)
                    Log.d("error", "list awig: $awig")
                    if (awig != null && awig.isNotEmpty()) {
                        binding.emptyObjectAwig.visibility = View.GONE
                    } else {
                        binding.emptyObjectAwig.visibility = View.VISIBLE
                    }
                    Log.d("error", "set upViewModel: $awig")
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    private fun getListAwig(token: String) {
        val client = ApiConfig.getApiService().getListAwig("Bearer $token", "$getIdSubak")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<GetAwigResponse> {
            override fun onResponse(call: Call<GetAwigResponse>, response: Response<GetAwigResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _awigList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetAwigResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getList(awig: MutableList<GetAllAwig>) {
        val awigAdapter = AwigAdapter(awig,
            object : AwigAdapter.OnAdapterAwigListener {
                override fun onClick(result: GetAllAwig) {
                    val bundle = Bundle()
                    val intent = Intent(this@Awig, DetailAwig::class.java)
                    bundle.putString("id", result.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, token
        )
        binding.rvAwig.adapter = awigAdapter
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

        setAdapterAwig = awig.let {
            AwigAdapter(it,
                object : AwigAdapter.OnAdapterAwigListener {
                    override fun onClick(result: GetAllAwig) {
                        val bundle = Bundle()
                        val intent = Intent(this@Awig, DetailAwig::class.java)
                        bundle.putString("id", result.id)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }, token)
        }
    }
}