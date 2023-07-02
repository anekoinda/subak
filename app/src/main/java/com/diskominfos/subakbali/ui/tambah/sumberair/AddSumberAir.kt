package com.diskominfos.subakbali.ui.tambah.sumberair

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
import com.diskominfos.subakbali.databinding.ActivityAddDataPuraBinding
import com.diskominfos.subakbali.databinding.ActivityAddSumberAirBinding
import com.diskominfos.subakbali.databinding.ActivitySumberAirBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.sumberdana.SumberDana
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddSumberAir : AppCompatActivity() {
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private lateinit var binding: ActivityAddSumberAirBinding
    var idKabupatenSelected: String = ""
    var idKecamatanSelected: String? = ""
    var idDesaDinasSelected: String? = ""
    var isTextInputLayoutClicked: Boolean = false
    var statusSelected: String? = ""
    var verifikasiSelected: String? = ""
    private var getIdSubak: String? = ""

    private val status = arrayListOf("Aktif", "Tidak Aktif")
    private val verifikasi = arrayListOf("Draft", "Reject", "Verif")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSumberAirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Sumber Air"

        setKabupaten()

        Log.e("id add sumber air", "$getIdSubak")
        getIdSubak = intent.getStringExtra("idsubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            binding.btnTambah.setOnClickListener {
                addDataSumberAir()
                val intent = Intent(this, AddSumberAirSubak::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
        }
    }

    private fun addDataSumberAir() {
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
            val getName = binding.nama.text.toString()
            val nama = getName.toRequestBody("text/plain".toMediaType())
            val getDeskripsi = binding.deskripsiSumberAir.text.toString()
            val deskripsi = getDeskripsi.toRequestBody("text/plain".toMediaType())
            val getKabupatenId = idKabupatenSelected
            val kabupaten_id = getKabupatenId.toRequestBody("text/plain".toMediaType())
            val getKecamatanId = idKecamatanSelected.toString()
            val kecamatan_id = getKecamatanId.toRequestBody("text/plain".toMediaType())
            val getDesaDinas = idDesaDinasSelected.toString()
            val desa_dinas_id = getDesaDinas.toRequestBody("text/plain".toMediaType())
            val getStatus = statusSelected
            val status = getStatus?.toRequestBody("text/plain".toMediaType())
            val getVerifikasi = verifikasiSelected
            val verifikasi = getVerifikasi?.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addDataSumberAir(
                "Bearer $it",
                0,
                kabupaten_id,
                kecamatan_id,
                desa_dinas_id,
                nama,
                deskripsi,
                verifikasi!!,
                status!!
            )

            service.enqueue(object : Callback<SumberAirResponse> {
                override fun onResponse(
                    call: Call<SumberAirResponse>,
                    response: Response<SumberAirResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddSumberAir,
                            "Berhasil input data pura",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddSumberAir,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddSumberAir,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SumberAirResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddSumberAir,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
    }

    fun setKabupaten() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        val idKabupaten: MutableList<String> = ArrayList()

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getKabupaten(it)
                addDataUmumViewModel.kabupatenList.observe(this) { kabupaten ->
                    getListKabupaten(kabupaten)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()

                        kabupaten.forEach {
                            idKabupaten.add(it.id)
                            list.add(it.name)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Kabupaten"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                Toast.makeText(
                                    this@AddSumberAir,
                                    "${searchableSpinner.selectedItem}  ${searchableSpinner.selectedItemPosition}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                if (idKabupaten.count() >= 0 && position >= 0) {
                                    setKecamatan(idKabupaten[position])
                                }
                                idKabupatenSelected = idKabupaten[position]
                                if (isTextInputLayoutClicked)
                                    binding.textKabupaten.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textKabupaten.keyListener = null
                        binding.textKabupaten.setOnClickListener {
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

    private fun getListKabupaten(kabupaten: MutableList<DataKabupaten>) {
    }

    private fun setKecamatan(district_id: String) {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getKecamatan(it, district_id)
                addDataUmumViewModel.kecamataList.observe(this) { kecamatan ->
                    getListKecamatan(kecamatan)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()
                        val idKecamatan: MutableList<String> = ArrayList()

                        kecamatan.forEach {
                            idKecamatan.add(it.id)
                            list.add(it.name)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Kecamatan"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                if (idKecamatan.count() >= 0 && position >= 0) {
                                    setDesaDinas(idKecamatan[position])
                                }
                                idKecamatanSelected = idKecamatan[position]
                                if (isTextInputLayoutClicked)
                                    binding.textKecamatan.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textKecamatan.keyListener = null
                        binding.textKecamatan.setOnClickListener {
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
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun getListKecamatan(kecamatan: MutableList<DataKecamatan>) {
    }

    private fun setDesaDinas(id: String) {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getDesDinas(it, id)
                addDataUmumViewModel.desaList.observe(this) { desa ->
                    getListDesaDinas(desa)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()
                        val idDesa: MutableList<String> = ArrayList()

                        desa.forEach {
                            list.add(it.name)
                            idDesa.add(it.id)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Kecamatan"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                idDesaDinasSelected = idDesa[position]
                                if (isTextInputLayoutClicked)
                                    binding.textDesa.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textDesa.keyListener = null
                        binding.textDesa.setOnClickListener {
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
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun getListDesaDinas(kabupaten: MutableList<DataDesaDinas>) {
    }
}