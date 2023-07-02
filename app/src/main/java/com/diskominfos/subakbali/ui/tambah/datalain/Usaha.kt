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
import com.diskominfos.subakbali.adapter.UsahaAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityUsahaBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class Usaha : AppCompatActivity() {
    private lateinit var binding: ActivityUsahaBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var getIdSubak: String? = ""
    private var token: String = ""
    private lateinit var setAdapterUsaha: UsahaAdapter
    private val _usahaList = MutableLiveData<MutableList<GetAllUsaha>>()
    val usahaList: LiveData<MutableList<GetAllUsaha>> = _usahaList
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUsahaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Usaha"

        setupRecyclerUsaha()
        setupViewModel()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent
        val bundle: Bundle? = intent.extras
        Log.e("id sumber airrr", "$getIdSubak")

        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnAddUsaha.setOnClickListener {
                Log.e("id subak", "$getIdSubak")
                val intent = Intent(this, AddUsaha::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }

        if (bundle != null) {
            Log.e("id subak usaha", "$getIdSubak")
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                Log.e("data id", "$getIdSubak")
                val intent = Intent(this, AlihFungsi::class.java).apply{
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupRecyclerUsaha() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvUsaha.layoutManager = layoutManager
        binding.rvUsaha.setHasFixedSize(true)
    }

    private fun setupViewModel(){
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            token = it
            if (it != "") {
                getListUsaha(it)
                usahaList.observe(this) { usaha ->
                    getList(usaha)
                    Log.d("error", "list usaha: $usaha")
                    if (usaha != null && usaha.isNotEmpty()) {
                        binding.emptyObjectUsaha.visibility = View.GONE
                    } else {
                        binding.emptyObjectUsaha.visibility = View.VISIBLE
                    }
                    Log.d("error", "set upViewModel: $usaha")
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    private fun getListUsaha(token: String) {
        val client = ApiConfig.getApiService().getListUsaha("Bearer $token", "$getIdSubak")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<GetUsahaResponse> {
            override fun onResponse(call: Call<GetUsahaResponse>, response: Response<GetUsahaResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _usahaList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetUsahaResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getList(usaha: MutableList<GetAllUsaha>) {
        val usahaAdapter = UsahaAdapter(usaha,
            object : UsahaAdapter.OnAdapterUsahaListener {
                override fun onClick(result: GetAllUsaha) {
                    val bundle = Bundle()
                    val intent = Intent(this@Usaha, DetailUsaha::class.java)
                    bundle.putString("id", result.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, token
        )
        binding.rvUsaha.adapter = usahaAdapter
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

        setAdapterUsaha = usaha.let {
            UsahaAdapter(it,
                object : UsahaAdapter.OnAdapterUsahaListener {
                    override fun onClick(result: GetAllUsaha) {
                        val bundle = Bundle()
                        val intent = Intent(this@Usaha, DetailUsaha::class.java)
                        bundle.putString("id", result.id)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }, token)
        }
    }
}