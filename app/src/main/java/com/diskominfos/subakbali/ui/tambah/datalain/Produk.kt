package com.diskominfos.subakbali.ui.tambah.datalain

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
import com.diskominfos.subakbali.DetailProduk
import com.diskominfos.subakbali.adapter.ProdukAdapter
import com.diskominfos.subakbali.adapter.SumberDanaAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityProdukBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class Produk : AppCompatActivity() {
    private lateinit var binding: ActivityProdukBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private val _produkList = MutableLiveData<MutableList<GetAllProduk>>()
    val produkList: LiveData<MutableList<GetAllProduk>> = _produkList
    private var getIdSubak: String? = ""
    private var token: String = ""
    private lateinit var setAdapterProduk: ProdukAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProdukBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Produk"

        setupRecyclerProduk()
        setupViewModel()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent
        Log.e("id sumber airrr", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnAddProduk.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                Log.e("id subak", "$getIdSubak")
                val intent = Intent(this, AddProduk::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerProduk() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvProduk.layoutManager = layoutManager
        binding.rvProduk.setHasFixedSize(true)
    }

    private fun setupViewModel(){
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            token = it
            if (it != "") {
                getListProduk(it)
                produkList.observe(this) { produk ->
                    getList(produk)
                    if (produk != null && produk.isNotEmpty()) {
                        binding.emptyObjectProduk.visibility = View.GONE
                    } else {
                        binding.emptyObjectProduk.visibility = View.VISIBLE
                    }
                    Log.d("error", "set upViewModel: $produk")
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }

    private fun getListProduk(token: String) {
        val client = ApiConfig.getApiService().getListProduk("Bearer $token", "$getIdSubak")
//        val client = ApiConfig.getApiService().getListTempSubakDesaAdat("Bearer $token")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<GetProdukResponse> {
            override fun onResponse(call: Call<GetProdukResponse>, response: Response<GetProdukResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _produkList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetProdukResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getList(produk: MutableList<GetAllProduk>) {
        val produkAdapter = ProdukAdapter(produk,
            object : ProdukAdapter.OnAdapterProdukListener {
                override fun onClick(result: GetAllProduk) {
                    val bundle = Bundle()
                    val intent = Intent(this@Produk, DetailProduk::class.java)
                    bundle.putString("id", result.id)
                    intent.putExtras(bundle)
                    startActivity(intent)
                }
            }, token
        )
        binding.rvProduk.adapter = produkAdapter
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

        setAdapterProduk = produk.let {
            ProdukAdapter(it,
                object : ProdukAdapter.OnAdapterProdukListener {
                    override fun onClick(result: GetAllProduk) {
                        val bundle = Bundle()
                        val intent = Intent(this@Produk, DetailProduk::class.java)
                        bundle.putString("id", result.id)
                        intent.putExtras(bundle)
                        startActivity(intent)
                    }
                }, token)
        }
    }
}