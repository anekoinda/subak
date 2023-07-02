package com.diskominfos.subakbali.ui.tambah.datalain

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
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAddUsahaBinding
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

class AddUsaha : AppCompatActivity() {
    private lateinit var binding: ActivityAddUsahaBinding
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    var idJenisUsahaSelected: String? = ""
    var idJenisPengelolaUsahaSelected: String? = ""
    private var getIdSubak: String? = ""
    var isTextInputLayoutClicked: Boolean = false
    private var token : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUsahaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Data Usaha"

        setJenisUsaha()
        setJenisPengelolaUsaha()

        getIdSubak = intent.getStringExtra("idsubak")
        Log.e("id add usaha", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnLanjutkan.setOnClickListener {
                addUsaha("$getIdSubak")
                val intent = Intent(this, Usaha::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }

    fun setJenisUsaha() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        val idJenisUsaha: MutableList<String> = ArrayList()

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getJenisUsaha(it)
                addDataUmumViewModel.jenisUsahaList.observe(this) { jenisUsaha ->
                    getListJenisUsaha(jenisUsaha)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()

                        jenisUsaha.forEach {
                            idJenisUsaha.add(it.id)
                            list.add(it.nama)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Jenis Usaha"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                Toast.makeText(
                                    this@AddUsaha,
                                    "${searchableSpinner.selectedItem}  ${searchableSpinner.selectedItemPosition}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                idJenisUsahaSelected = idJenisUsaha[position]
                                Log.e("produk selected", "$idJenisUsahaSelected")
                                if (isTextInputLayoutClicked)
                                    binding.textJenisUsaha.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textJenisUsaha.keyListener = null
                        binding.textJenisUsaha.setOnClickListener {
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
        }
    }

    private fun getListJenisUsaha(jenisUsaha: MutableList<DataJenisUsaha>) {
    }

    fun setJenisPengelolaUsaha() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        val idJenisPengelolaUsaha: MutableList<String> = ArrayList()

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getJenisPengelolaUsaha(it)
                addDataUmumViewModel.jenisPengelolaUsahaList.observe(this) { jenisPengelolaUsaha ->
                    getListJenisPengelolaUsaha(jenisPengelolaUsaha)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()

                        jenisPengelolaUsaha.forEach {
                            idJenisPengelolaUsaha.add(it.id)
                            list.add(it.nama)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Jenis Usaha"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                Toast.makeText(
                                    this@AddUsaha,
                                    "${searchableSpinner.selectedItem}  ${searchableSpinner.selectedItemPosition}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                idJenisPengelolaUsahaSelected = idJenisPengelolaUsaha[position]
                                Log.e("produk selected", "$idJenisPengelolaUsahaSelected")
                                if (isTextInputLayoutClicked)
                                    binding.textJenisPengelolaUsaha.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textJenisPengelolaUsaha.keyListener = null
                        binding.textJenisPengelolaUsaha.setOnClickListener {
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
        }
    }

    private fun getListJenisPengelolaUsaha(jenisPengelolaUsaha: MutableList<DataJenisPengelolaUsaha>) {
    }

    private fun addUsaha(id: String) {
        addDataUmumViewModel.addSubak()
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
            val getNama = binding.namaUsaha.text.toString()
            val nama = getNama.toRequestBody("text/plain".toMediaType())
            val getKeterangan = binding.keteranganUsaha.text.toString()
            val keterangan = getKeterangan.toRequestBody("text/plain".toMediaType())
            val getJenisUsahaId = idJenisUsahaSelected.toString()
            val jenis_usaha_id = getJenisUsahaId.toRequestBody("text/plain".toMediaType())
            val getJenisPengelolaUsahaId = idJenisPengelolaUsahaSelected.toString()
            val jenis_pengelola_usaha_id = getJenisPengelolaUsahaId.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addDataUsaha(
                "Bearer $it",
                getIdSubak!!.toInt(),
                nama,
                jenis_usaha_id,
                jenis_pengelola_usaha_id,
                keterangan
            )

            service.enqueue(object : Callback<UsahaResponse> {
                override fun onResponse(
                    call: Call<UsahaResponse>,
                    response: Response<UsahaResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddUsaha,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT

                        ).show()
//                        val subak = response.body()
//                        idsubak = subak?.data?.id.toString()
//                        Log.e("idsubak", subak?.data?.id.toString())
                    } else {
                        Toast.makeText(
                            this@AddUsaha,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<UsahaResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddUsaha,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }
}