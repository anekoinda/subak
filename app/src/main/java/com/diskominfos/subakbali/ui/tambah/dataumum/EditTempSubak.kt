package com.diskominfos.subakbali.ui.tambah.dataumum

import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityDetailTempSubakBinding
import com.diskominfos.subakbali.databinding.ActivityEditTempSubakBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.AddMarkerSubak
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.sql.Types.NULL
import java.util.*
import kotlin.collections.ArrayList

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class EditTempSubak : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityEditTempSubakBinding
    private lateinit var dataViewModel: DataViewModel
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private var token: String = ""
    var isTextInputLayoutClicked: Boolean = false
    private val jenisSubak = arrayListOf("Subak Basah", "Subak Abian")
    var idKabupatenSelected: String? = ""
    var idKecamatanSelected: String? = ""
    var idDesaSelected: String? = ""
    var jenisSubakSelected: String? = ""
    var kab_id: String = ""
    var kec_id: String = ""
    var desa_id: String = ""
    var jenis: String = ""
    var getNameKabupaten: String = ""
    private var latitude: String? = ""
    private var longitude: String? = ""
    private var getLat: String = ""
    private var getLng: String = ""
    private lateinit var mMap: GoogleMap
    var id: String = ""
    var actionStatus = "update"

    private val resultCOntract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == RESULT_OK) {
                latitude = result.data?.getStringExtra("lat").toString()
                longitude = result.data?.getStringExtra("lng").toString()

                Log.e("lat", "$latitude")
                Log.e("lng", "$longitude")

                val latLng = LatLng("$latitude".toDouble(), "$longitude".toDouble())
                mMap.addMarker(MarkerOptions().position(latLng))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

                getCompleteAddressString("$latitude".toDouble(), "$longitude".toDouble())
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditTempSubakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val supportMapFragment =
            (supportFragmentManager.findFragmentById(R.id.mapEditSubak) as
                    SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this@EditTempSubak)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val id = bundle.getString("id")
            getData("$id")

            binding.btnSimpan.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Edit Draf Subak")
                    .setMessage("Apakah anda yakin ingin mengedit draf subak ini?")
                    .setCancelable(true)
                    .setPositiveButton("Iya") { _, _ ->
                        simpanData("$id")
                        Log.e("data id", "$id")
                    }.setNegativeButton("Batal") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.show()
            }
            binding.btnBatal.setOnClickListener {
                finish()
            }
        }
    }

    fun getData(id: String) {
        setJenisSubak()
        setKabupaten()
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.getUser().observe(this) { tempSubak ->
            dataViewModel.getTempSubak(tempSubak, id)
            dataViewModel.tempSubakList.observe(this) {
                binding.apply {
                    val data = it[0]
                    val id = data.id
                    textKabupaten.text = data.kabupaten?.name
                    kab_id = data.kabupaten_id
                    textKecamatan.text = data.kecamatan?.name
                    kec_id = data.kecamatan_id
                    textDesa.text = data.desa_pengampu?.name
                    desa_id = data.desa_pengampu_id
                    namaSubak.setText(data.nama)
                    luasSubak.setText(data.luas)
                    textSubak.text = data.jenis_subak
                    jenis = data.jenis_subak
                    btnLokasi.text = data.lat + "," + data.lng
                    getLat = data.lat
                    getLng = data.lng
                    batasBaratSubak.setText(data.batas_barat)
                    batasTimurSubak.setText(data.batas_timur)
                    batasSelatanSubak.setText(data.batas_selatan)
                    batasUtaraSubak.setText(data.batas_utara)
                }
            }
        }
    }

    fun simpanData(id: String) {
        getViewModel()
        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        var getUsername: String = ""
        addDataUmumViewModel.getUsername().observe(this) { it ->
            getUsername = it
        }
        addDataUmumViewModel.getUser().observe(this) { it ->
            Log.e("data id", "$id")
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

            var kabupaten_id = ""
            if (idKabupatenSelected == "") {
                kabupaten_id = kab_id
            } else {
                val getKabupatenId = idKabupatenSelected.toString()
                kabupaten_id = getKabupatenId
            }

            var kecamatan_id = ""
            if (idKecamatanSelected == "") {
                kecamatan_id = kec_id
            } else {
                val getKecamatanId = idKecamatanSelected.toString()
                kecamatan_id = getKecamatanId
            }

            var desa_pengampu_id = ""
            if (idDesaSelected == "") {
                desa_pengampu_id = desa_id
            } else {
                val getDesaId = idDesaSelected.toString()
                desa_pengampu_id = getDesaId
            }

            var jenis_subak = ""
            if (jenisSubakSelected == "") {
                jenis_subak = jenis
            } else {
                val getJenis = jenisSubakSelected.toString()
                jenis_subak = getJenis
            }

            var lng = ""
            if (longitude == "") {
                lng = getLng
            } else {
                lng = longitude.toString()
            }

            var lat = ""
            if (latitude == "") {
                lat = getLat
            } else {
                lat = latitude.toString()
            }

            val action_status = actionStatus.toRequestBody("text/plain".toMediaType())

            val updated_by = getUsername.toRequestBody("text/plain".toMediaType())
            Log.e("updated by", "$updated_by")

            val service = ApiConfig.getApiService().updateDataSubak(
                "Bearer $it",
                id,
                kabupaten_id.toRequestBody("text/plain".toMediaType()),
                kecamatan_id.toRequestBody("text/plain".toMediaType()),
                desa_pengampu_id.toRequestBody("text/plain".toMediaType()),
                name,
                jenis_subak.toRequestBody("text/plain".toMediaType()),
                luas,
                lat.toRequestBody("text/plain".toMediaType()),
                lng.toRequestBody("text/plain".toMediaType()),
                1,
                batas_utara,
                batas_selatan,
                batas_timur,
                batas_barat,
                action_status,
                updated_by
            )

            service.enqueue(object : Callback<DataSubakResponse> {
                override fun onResponse(
                    call: Call<DataSubakResponse>,
                    response: Response<DataSubakResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@EditTempSubak,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@EditTempSubak,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@EditTempSubak,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DataSubakResponse>, t: Throwable) {
                    Toast.makeText(
                        this@EditTempSubak,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
    }

    private fun setJenisSubak() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Edit Jenis Subak"
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
        searchSpinner()
    }

    private fun setKabupaten() {
        getViewModel()
        val idKabupaten: MutableList<String> = ArrayList()
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
                            val data = it
                            Log.e("data kab", "$data")
                            Log.e("data kab", getNameKabupaten)
                            if (getNameKabupaten == data.id) {
                                binding.textKabupaten.text = data.name
                            }
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Kabupaten"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                Toast.makeText(
                                    this@EditTempSubak,
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
                        searchSpinner()
                    }
                }
            }
        }
    }

    private fun getListKabupaten(kabupaten: MutableList<DataKabupaten>) {
    }

    private fun setKecamatan(district_id: String) {
        getViewModel()
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
                        searchSpinner()
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
        getViewModel()
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
                        searchSpinner()
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

    fun getViewModel() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
    }

    fun searchSpinner() {
        val searchableSpinner = SearchableSpinner(this)
        binding.editTextSpinner.keyListener = null
        binding.editTextSpinner.setOnClickListener {
            searchableSpinner.highlightSelectedItem = false
            isTextInputLayoutClicked = false
            searchableSpinner.show()
        }
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String? {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString()
                binding.btnLokasi.text = strReturnedAddress.toString()
            } else {
                binding.btnLokasi.text = "No Address returned!"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.btnLokasi.text = "Canont get Address!"
        }
        return strAdd
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.uiSettings.isScrollGesturesEnabled = false
        dataViewModel.tempSubakList.observe(this) {
            val data = it[0]
            val idDrafSubak = data.id
            val marker = LatLng(data.lat.toDouble(), data.lng.toDouble())
            getCompleteAddressString(data.lat.toDouble(), data.lng.toDouble())
            googleMap.addMarker(
                MarkerOptions()
                    .position(marker)
                    .title(data.nama)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))

            mMap.setOnMapClickListener {
                mMap.clear()
                val intent = Intent(this, EditMarkerSubak::class.java)
                val bundle = Bundle()
                bundle.putString("id", idDrafSubak)
                intent.putExtras(bundle)
                resultCOntract.launch(intent)
                onResume()
            }
        }
    }
}