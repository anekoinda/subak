package com.diskominfos.subakbali.ui.tambah.parahyangan

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.adapter.PuraSubakAdapter
import com.diskominfos.subakbali.adapter.TempSubakDesaAdatAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAddDataParahyanganBinding
import com.diskominfos.subakbali.databinding.ActivityAddDataWilayahBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.profil.ProfilViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddDataWilayah
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddWilayahDesaAdat
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddWilayahDesaDinas
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddDataParahyangan : AppCompatActivity() {
    private lateinit var binding: ActivityAddDataParahyanganBinding
    private lateinit var puraViewModel: PuraViewModel
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var getIdSubak: String? = ""
    var idPuraSelected: String? = ""
    var isTextInputLayoutClicked: Boolean = false
    private val _puraSubakList = MutableLiveData<MutableList<GetAllDataPuraSubak>>()
    val puraSubakList: LiveData<MutableList<GetAllDataPuraSubak>> = _puraSubakList
    private lateinit var setAdapterPuraSubak: PuraSubakAdapter
    private var token : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataParahyanganBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Data Parahyangan"

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent
        Log.e("id subak parahyanganaft", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnTambah.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                addSubakPura("$getIdSubak")
                Log.e("id subak", "$getIdSubak")
                val intent = Intent(this, AddDataWilayah::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                setupRecyclerPuraSubak()
                setupViewModel()
            }
        }
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            Log.e("idsubakparahyangan", "$getIdSubak")

            binding.btnTambah.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                addSubakPura("$getIdSubak")
                Log.e("data id", "$getIdSubak")
                setupRecyclerPuraSubak()
                setupViewModel()
            }
        }

        if (bundle != null) {
            Log.e("id subak parahyangan", "$getIdSubak")
            val idtempsubak = getIdSubak
            binding.btnSimpan.setOnClickListener {
                Log.e("data id", "$getIdSubak")
                val intent = Intent(this, Tradisi::class.java).apply{
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }

            binding.textTambahPura.setOnClickListener {
                val intent = Intent(this, AddDataPura::class.java).apply{
                    putExtra("idsubak", idtempsubak)
                    Log.e("id subak ke pura", "$idtempsubak")
                }
                startActivity(intent)
                finish()
            }

            binding.btnLewati.setOnClickListener {
                finish()
            }
        }
        setPura()

    }

    private fun setupRecyclerPuraSubak() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvPuraSubak.layoutManager = layoutManager
        binding.rvPuraSubak.setHasFixedSize(true)
    }

    private fun setupViewModel(){
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) {
        token = it
            if (it != "") {
                getListPura(it)
                puraSubakList.observe(this) { puraSubak ->
                    getList(puraSubak)
                    if (puraSubak.isNotEmpty()) {
                        Log.e("pura subak list", "$puraSubak")
                        binding.emptyObjectPuraSubak.visibility = View.GONE
                    } else {
                        binding.emptyObjectPuraSubak.visibility = View.VISIBLE
                    }
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }
    private fun addSubakPura(id: String) {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        var getUsername: String = ""
        addDataUmumViewModel.getUsername().observe(this) { it ->
            getUsername = it
        }
        addDataUmumViewModel.getUser().observe(this) { it ->
            Log.e("token", it)
            val getPuraId = idPuraSelected
            val pura_id = getPuraId?.toRequestBody("text/plain".toMediaType())

            val service = ApiConfig.getApiService().addDataSubakPura(
                "Bearer $it",
                0,
                getIdSubak!!.toInt(),
                pura_id!!,
            )

            service.enqueue(object : Callback<SubakPuraResponse> {
                override fun onResponse(
                    call: Call<SubakPuraResponse>,
                    response: Response<SubakPuraResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddDataParahyangan,
                            "Berhasil input data pura",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddDataParahyangan,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddDataParahyangan,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SubakPuraResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddDataParahyangan,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
    }

    fun setPura() {
        puraViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[PuraViewModel::class.java]
        val idPura: MutableList<String> = ArrayList()

        puraViewModel = ViewModelProvider(this)[PuraViewModel::class.java]
        puraViewModel.getUser().observe(this) { it ->
            if (it != "") {
                puraViewModel.getPura(it)
                puraViewModel.puraList.observe(this) { pura ->
                    getListPura(pura.toString())
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()

                        pura.forEach {
                            idPura.add(it.id)
                            list.add(it.nama)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Pura"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                Toast.makeText(
                                    this@AddDataParahyangan,
                                    "${searchableSpinner.selectedItem}  ${searchableSpinner.selectedItemPosition}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (idPura.count() >= 0 && position >= 0) {
//                                    setKecamatan(idPura[position])
                                }
                                idPuraSelected = idPura[position]
                                if (isTextInputLayoutClicked)
                                    binding.textPura.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textPura.keyListener = null
                        binding.textPura.setOnClickListener {
                            isTextInputLayoutClicked = true
                            searchableSpinner.show()
                        }

                        binding.editTextSpinner.keyListener = null
                        binding.editTextSpinner.setOnClickListener {
                            searchableSpinner.highlightSelectedItem = false
                            isTextInputLayoutClicked = false
                            searchableSpinner.show()
                        }
                    }
                }
            }
//            } else {
//                startActivity(Intent(this, LoginActivity::class.java))
//                finish()
//            }
        }
    }

    private fun getListPura(token: String) {
        val client = ApiConfig.getApiService().getListPuraSubak("Bearer $token", "$getIdSubak")
//        val client = ApiConfig.getApiService().getListTempSubakDesaAdat("Bearer $token")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<GetPuraSubakResponse> {
            override fun onResponse(call: Call<GetPuraSubakResponse>, response: Response<GetPuraSubakResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _puraSubakList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetPuraSubakResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun getList(puraSubak: MutableList<GetAllDataPuraSubak>) {
        val puraSubakAdapter = PuraSubakAdapter(puraSubak,
            object : PuraSubakAdapter.OnAdapterDataSubakPuraListener {
                override fun onClick(result: GetAllDataPuraSubak) {
                    val bundle = Bundle()
//                    val intent = Intent(this@AddDataWilayah, DetailTempSubak::class.java)
//                    bundle.putString("id", data.id)
//                    intent.putExtras(bundle)
//                    startActivity(intent)
                }
            }, token
        )
        binding.rvPuraSubak.adapter = puraSubakAdapter
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


        setAdapterPuraSubak = puraSubak.let {
            PuraSubakAdapter(it,
                object : PuraSubakAdapter.OnAdapterDataSubakPuraListener {
                    override fun onClick(result: GetAllDataPuraSubak) {
                    }
                }, token)
        }
    }
}