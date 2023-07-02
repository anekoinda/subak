package com.diskominfos.subakbali.ui.tambah.sumberair

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

//class SumberAir : AppCompatActivity() {
//    private lateinit var binding: ActivitySumberAirBinding
//    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
//    private var token: String = ""
//    private val _sumberAirList = MutableLiveData<MutableList<GetAllSumberAir>>()
//    val sumberAirList: LiveData<MutableList<GetAllSumberAir>> = _sumberAirList
//    private var getIdSubak: String? = ""
//    private lateinit var setAdapterSumberAir: SumberAirAdapter
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySumberAirBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setupRecyclerSumberAir()
//        setupViewModel()
//
//        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent
//
//        val bundle: Bundle? = intent.extras
//        if (getIdSubak != null) {
//            val idtempsubak = getIdSubak
//            binding.btnAddSumberAir.setOnClickListener {
//                val builder = AlertDialog.Builder(this)
//                Log.e("id subak tradisi", "$getIdSubak")
//                val intent = Intent(this, AddSumberAir::class.java).apply {
//                    putExtra("idsubak", idtempsubak)
//                }
//                startActivity(intent)
//                finish()
//            }
//        }
//        binding.btnLewati.setOnClickListener {
//            finish()
//        }
//    }
//
//    private fun setupRecyclerSumberAir() {
//        val layoutManager = LinearLayoutManager(this)
//        binding.rvSumberAir.layoutManager = layoutManager
//        binding.rvSumberAir.setHasFixedSize(true)
//    }
//
//    private fun setupViewModel(){
//        addDataUmumViewModel = ViewModelProvider(
//            this,
//            ViewModelFactory(UserPreference.getInstance(dataStore))
//        )[AddDataUmumViewModel::class.java]
//        addDataUmumViewModel.getUser().observe(this) { it ->
//            token = it
//            if (it != "") {
//                getListSumberAir(it)
//                sumberAirList.observe(this) { sumberAir ->
//                    getList(sumberAir)
//                    if (it != null) {
////                        showLoading(false)
//                    }
//                    Log.d("error", "set upViewModel: $sumberAir")
//                }
//            } else {
//                val intent = Intent(this, LoginActivity::class.java)
//                this.startActivity(intent)
//            }
//        }
//    }
//
//    private fun getListSumberAir(token: String) {
//        val client = ApiConfig.getApiService().getListSumberAir("Bearer $token", "$getIdSubak")
////        val client = ApiConfig.getApiService().getListTempSubakDesaAdat("Bearer $token")
//        Log.e("data id temp pura subak", "$getIdSubak")
//        client.enqueue(object : Callback<GetSumberAirResponse> {
//            override fun onResponse(call: Call<GetSumberAirResponse>, response: Response<GetSumberAirResponse>) {
//                if (response.isSuccessful) {
//                    val responseBody = response.body()
//                    if (responseBody != null) {
//                        _sumberAirList.value = response.body()?.data
//                    }
//
//                } else {
//                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
//                }
//            }
//
//            override fun onFailure(call: Call<GetSumberAirResponse>, t: Throwable) {
//                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
//            }
//        })
//    }
//
//    private fun getList(sumberAir: MutableList<GetAllSumberAir>) {
//        val sumberAirAdapter = sumberAirAdapter(sumberAir,
//            object : sumberAirAdapter.OnAdapterSumberAirListener {
//                override fun onClick(result: GetAllSumberAir) {
//                    val bundle = Bundle()
//                    val intent = Intent(this@SumberAir, DetailSumberAir::class.java)
//                    bundle.putString("id", result.id)
//                    intent.putExtras(bundle)
//                    startActivity(intent)
//                }
//            }, token
//        )
//        binding.rvSumberAir.adapter = sumberAirAdapter
////        tempSubakAdapter.setOnClickCallBack(object : TempSubakAdapter.OnItemClickCallback {
////            override fun onClick(data: DataTempSubak) {
////                Intent(this@DraftSubak, DetailTempSubak::class.java).also {
////                    it.putExtra("id", data.id)
////                    Log.e("id frag", "$id")
////                    startActivity(it)
////                }
////                val bundle = Bundle()
////                val intent = Intent(this@DraftSubak, DetailTempSubak::class.java)
////                bundle.putString("id", data.id)
////                Log.e("id okama", data.id)
////                intent.putExtras(bundle)
////                startActivity(intent)
////            }
////        })
//
//        setAdapterSumberAir = sumberAir .let {
//            SumberAirAdapter(it,
//                object : SumberAirAdapter.OnAdapterSumberAirListener {
//                    override fun onClick(result: GetAllSumberAir) {
//                        val bundle = Bundle()
//                        val intent = Intent(this@SumberAir, DetailSumberAir::class.java)
//                        bundle.putString("id", result.id)
//                        intent.putExtras(bundle)
//                        startActivity(intent)
//                    }
//                }, token)
//        }
//    }
//}