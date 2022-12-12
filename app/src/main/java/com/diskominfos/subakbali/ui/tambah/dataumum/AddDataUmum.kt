package com.diskominfos.subakbali.ui.tambah.dataumum


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddDataUmum : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private lateinit var binding: ActivityAddDataUmumBinding
    private val jenisSubak = arrayListOf("Subak", "Abian")
    var idKabupatenSelected: String? = ""
    var idKecamatanSelected: String? = ""
    var idDesaSelected: String? = ""
    var jenisSubakSelected: String? = ""
    var latitude: String? = ""
    var longitude: String? = ""
    var activityStatus: String = "add"
    var isTextInputLayoutClicked: Boolean = false

    private val resultCOntract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == Activity.RESULT_OK) {
                latitude = result.data?.getStringExtra(AddMarkerSubak.EXTRA_LAT)
                longitude = result.data?.getStringExtra(AddMarkerSubak.EXTRA_LNG)
                binding.btnLokasi.text = "$latitude, $longitude"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_data_umum)
        binding = ActivityAddDataUmumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setJenisSubak()
        setKabupaten()

        binding.btnLokasi.setOnClickListener {
            val intent = Intent(this, AddMarkerSubak::class.java)
            resultCOntract.launch(intent)
        }
        binding.btnLanjutkan.setOnClickListener { addDataSubak() }
    }

    private fun addDataSubak() {
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
            val getJenisSubak = jenisSubakSelected.toString()
            val jenis = getJenisSubak.toRequestBody("text/plain".toMediaType())
            val getLongitude = longitude.toString()
            val lng = getLongitude.toRequestBody("text/plain".toMediaType())
            val getLatitude = latitude.toString()
            val lat = getLatitude.toRequestBody("text/plain".toMediaType())
            val activity_status = activityStatus.toRequestBody("text/plain".toMediaType())
            val created_by = getUsername.toRequestBody("text/plain".toMediaType())

            val service = getApiService().addDataSubak(
                "Bearer $it",
                kabupaten_id,
                kecamatan_id,
                desa_id,
                name,
                jenis,
                luas,
                lat,
                lng,
                1,
                batas_utara,
                batas_selatan,
                batas_timur,
                batas_barat,
                activity_status,
                created_by
            )

            service.enqueue(object : Callback<DataSubakResponse> {

                override fun onResponse(
                    call: Call<DataSubakResponse>,
                    response: Response<DataSubakResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddDataUmum,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT
                        ).show()
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

    private fun setJenisSubak() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Jenis Subak"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    jenisSubakSelected = selectedString
                    binding.textSubak.text = jenisSubakSelected
            }
        }

        searchableSpinner.setSpinnerListItems(jenisSubak)
        binding.textSubak.keyListener = null
        binding.textSubak.setOnClickListener {
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
                                    this@AddDataUmum,
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
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
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
                                    setDesa(idKecamatan[position])
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

    private fun setDesa(id: String) {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getDesDinas(it, id)
                addDataUmumViewModel.desaList.observe(this) { desa ->
                    getListDesa(desa)
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
                                idDesaSelected = idDesa[position]
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
}
