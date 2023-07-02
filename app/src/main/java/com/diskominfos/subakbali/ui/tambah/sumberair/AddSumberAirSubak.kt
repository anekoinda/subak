package com.diskominfos.subakbali.ui.tambah.sumberair

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.ui.tambah.datalain.Produk
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAddSumberAirSubakBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddSumberAirSubak : AppCompatActivity() {
    private lateinit var binding: ActivityAddSumberAirSubakBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var getIdSubak: String? = ""
    var isTextInputLayoutClicked: Boolean = false
    var idSumberAirSelected: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSumberAirSubakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Sumber Air Subak"

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent
        Log.e("id sumber airrr", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnSimpan.setOnClickListener {
                addSubakSumberAir("$getIdSubak")
                Log.e("id subak", "$getIdSubak")
                val intent = Intent(this, SumberAir::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }

            binding.textTambahSumberAir.setOnClickListener {
                val intent = Intent(this, AddSumberAir::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
        setSumberAir()
    }


    private fun getListSumberAir(token: String) {
        val client = ApiConfig.getApiService().getListSumberAir("Bearer $token", "$getIdSubak")
//        val client = ApiConfig.getApiService().getListTempSubakDesaAdat("Bearer $token")
        Log.e("data id temp pura subak", "$getIdSubak")
        client.enqueue(object : Callback<SumberAirResponse> {
            override fun onResponse(call: Call<SumberAirResponse>, response: Response<SumberAirResponse>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
//                        _sumberAirList.value = response.body()?.data
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

    fun setSumberAir() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        val idSumberAir: MutableList<String> = ArrayList()

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getSumberAir(it)
                addDataUmumViewModel.sumberAirList.observe(this) { sumberAir ->
                    getListSumberAir(sumberAir.toString())
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()

                        sumberAir.forEach {
                            idSumberAir.add(it.id)
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
                                    this@AddSumberAirSubak,
                                    "${searchableSpinner.selectedItem}  ${searchableSpinner.selectedItemPosition}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (idSumberAir.count() >= 0 && position >= 0) {
//                                    setKecamatan(idPura[position])
                                }
                                idSumberAirSelected = idSumberAir[position]
                                if (isTextInputLayoutClicked)
                                    binding.textSumberAir.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textSumberAir.keyListener = null
                        binding.textSumberAir.setOnClickListener {
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

//    private fun setupViewModel(){
//        addDataUmumViewModel = ViewModelProvider(
//            this,
//            ViewModelFactory(UserPreference.getInstance(dataStore))
//        )[AddDataUmumViewModel::class.java]
//        addDataUmumViewModel.getUser().observe(this) { it ->
//            token = it
//            if (it != "") {
//                getListSumberAir(it)
////                sumberAirList.observe(this) { puraSubak ->
////                    getList(puraSubak)
////                    if (it != null) {
////                        showLoading(false)
////                    }
////                    Log.d("error", "setupViewModel: $puraSubak")
////                }
//            } else {
//                val intent = Intent(this, LoginActivity::class.java)
//                this.startActivity(intent)
//            }
//        }
//    }

    private fun addSubakSumberAir(id: String) {
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
            Log.e("token subak sumberair", it)
            val getSumberAirId = idSumberAirSelected
            val sumber_air_id = getSumberAirId?.toRequestBody("text/plain".toMediaType())
            val getPanjangSaluran = binding.panjangSaluran.text.toString()
            val panjang_saluan = getPanjangSaluran.toRequestBody("text/plain".toMediaType())
            val getSatuanPanjang = binding.satuanPanjang.text.toString()
            val satuan_panjang = getSatuanPanjang.toRequestBody("text/plain".toMediaType())
            val getDebitAir = binding.debitAir.text.toString()
            val debit_air = getDebitAir.toRequestBody("text/plain".toMediaType())
            val getSatuanDebitAir = binding.satuanDebitAir.text.toString()
            val satuan_debit_air = getSatuanDebitAir.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addDataSubakSumberAir(
                "Bearer $it",
                getIdSubak!!.toInt(),
                sumber_air_id!!,
                panjang_saluan,
                satuan_panjang,
                debit_air,
                satuan_debit_air,
                1
            )

            service.enqueue(object : Callback<SubakSumberAirResponse> {
                override fun onResponse(
                    call: Call<SubakSumberAirResponse>,
                    response: Response<SubakSumberAirResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddSumberAirSubak,
                            "Berhasil input data pura",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddSumberAirSubak,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddSumberAirSubak,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SubakSumberAirResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddSumberAirSubak,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
    }
}