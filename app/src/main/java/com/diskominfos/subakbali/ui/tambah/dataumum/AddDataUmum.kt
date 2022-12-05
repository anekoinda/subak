package com.diskominfos.subakbali.ui.tambah.dataumum

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.ApiConfig.Companion.getApiService
import com.diskominfos.subakbali.api.DataDesaDinas
import com.diskominfos.subakbali.api.DataKabupaten
import com.diskominfos.subakbali.api.DataKecamatan
import com.diskominfos.subakbali.api.DataSubakResponse
import com.diskominfos.subakbali.databinding.ActivityAddDataUmumBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddDataUmum : AppCompatActivity(), OnMapReadyCallback, AdapterView.OnItemSelectedListener {
    private lateinit var map: GoogleMap
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private lateinit var binding: ActivityAddDataUmumBinding
    private val jenisSubak = arrayOf("Pilih jenis subak", "subak", "abian")
    var idKabupatenSelected: String? = ""
    var idKecamatanSelected: String? = ""
    var idDesaSelected: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data_umum)
        binding = ActivityAddDataUmumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupJenis()
        setupViewModelKabupaten()
        setupViewModelKecamatan()
        setupViewModelDesaDinas()

        binding.btnLokasi.setOnClickListener {
            val intent = Intent(this, AddMarkerSubak::class.java)
            startActivity(intent)
        }

        binding.btnLanjutkan.setOnClickListener { addDataSubak() }

        val lat = intent.getStringExtra("lat")
        val long = intent.getStringExtra("long")
        binding.btnLokasi.text = "$lat, $long"

    }

    private fun addDataSubak() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]

        addDataUmumViewModel.getUser().observe(this) { it ->
            Log.e("token", it)
            val getName = binding.namaSubak.text.toString()
            val name = getName.toRequestBody("text/plain".toMediaType())
            val getLuas = binding.luasSubak.text.toString()
            val luas = getLuas.toRequestBody("text/plain".toMediaType())
            val getBatasTimur = binding.batasTimurSubak.text.toString()
            val batas_timur = getBatasTimur.toRequestBody("text/plain".toMediaType())
            val getBatasBarat = binding.batasBaratSubak.text.toString()
            val batas_barat = getBatasBarat.toRequestBody("text/plain".toMediaType())
            val getBatasUtara = binding.batasUtaraSubak.text.toString()
            val batas_utara = getBatasUtara.toRequestBody("text/plain".toMediaType())
            val getBatasSelatan = binding.batasSelatanSubak.text.toString()
            val batas_selatan = getBatasSelatan.toRequestBody("text/plain".toMediaType())
            val getKabupatenId = idKabupatenSelected.toString()
            val kabupaten_id = getKabupatenId.toRequestBody("text/plain".toMediaType())
            val getKecamatanId = idKecamatanSelected.toString()
            val kecamatan_id = getKecamatanId.toRequestBody("text/plain".toMediaType())
            val getDesaId = idDesaSelected.toString()
            val desa_id = getDesaId.toRequestBody("text/plain".toMediaType())
            val getJenisSubak = binding.spnJenis.selectedItem.toString()
            val jenis = getJenisSubak.toRequestBody("text/plain".toMediaType())

            val service = getApiService().addDataSubak(
                "Bearer $it",
                kabupaten_id,
                kecamatan_id,
                desa_id,
                name,
                jenis,
                luas,
                0,
                batas_utara,
                batas_selatan,
                batas_timur,
                batas_barat
            )

            service.enqueue(object : Callback<DataSubakResponse> {

                override fun onResponse(
                    call: Call<DataSubakResponse>,
                    response: Response<DataSubakResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddDataUmum, "berhasil", Toast.LENGTH_SHORT).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddDataUmum,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddDataUmum,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DataSubakResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddDataUmum,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
    }

    private fun setupJenis() {
        binding.spnJenis.onItemSelectedListener = this
        val adapterJenis = ArrayAdapter(this, android.R.layout.simple_spinner_item, jenisSubak)
        adapterJenis.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spnJenis.adapter = adapterJenis

        binding.spnJenis.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val text: String = parent?.getItemAtPosition(position).toString()
                    Toast.makeText(
                        this@AddDataUmum,
                        text,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    fun setupViewModelKabupaten() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]
        val idKabupaten: MutableList<String> = ArrayList()
        binding.spnKabupaten.onItemSelectedListener = this
        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getKabupaten(it)
                addDataUmumViewModel.kabupatenList.observe(this) { kabupaten ->
                    getListKabupaten(kabupaten)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()

                        list.add(0, "Pilih kabupaten")
                        idKabupaten.add(0, "0")

                        kabupaten.forEach {
                            idKabupaten.add(1, it.id)
                            list.add(1, it.name)
                        }

                        binding.spnKabupaten.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            list
                        )

                    }
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
        binding.spnKabupaten.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    idKabupatenSelected = idKabupaten[position]
                }
            }
    }

    private fun getListKabupaten(kabupaten: MutableList<DataKabupaten>) {
    }

    private fun setupViewModelKecamatan() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getKecamatan(it)
                addDataUmumViewModel.kecamataList.observe(this) { kecamatan ->
                    getListKecamatan(kecamatan)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()
                        val idKecamatan: MutableList<String> = ArrayList()
                        list.add(0, "Pilih kecamatan")
                        idKecamatan.add(0, "0")

                        kecamatan.forEach {
                            idKecamatan.add(1, it.id)
                            list.add(1, it.name)
                        }

                        binding.spnKecamatan.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            list
                        )
                        binding.spnKecamatan.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    idKecamatanSelected = idKecamatan[position]
                                }
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

    private fun setupViewModelDesaDinas() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getDesDinas(it)
                addDataUmumViewModel.desaList.observe(this) { desa ->
                    getListDesa(desa)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()
                        val idDesa: MutableList<String> = ArrayList()
                        list.add(0, "Pilih desa pengampu")
                        idDesa.add(0, "0")

                        desa.forEach {
                            list.add(1, it.name)
                            idDesa.add(1, it.id)
                        }

                        binding.spnDesa.adapter = ArrayAdapter(
                            this,
                            android.R.layout.simple_spinner_item,
                            list
                        )
                        binding.spnDesa.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                }

                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    idDesaSelected = idDesa[position]
                                }
                            }
                    }
                }
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun getListDesa(kabupaten: MutableList<DataDesaDinas>) {
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val sydney = LatLng(-8.613729, 115.214033)
        map.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Subak Sembung")
        )
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }


    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }
}
