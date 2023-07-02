package com.diskominfos.subakbali.ui.tambah.dataumum

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import cn.pedant.SweetAlert.SweetAlertDialog
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.basgeekball.awesomevalidation.utility.RegexTemplate
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.api.ApiConfig.Companion.getApiService
import com.diskominfos.subakbali.databinding.ActivityAddDataUmumBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.datawilayah.AddDataWilayah
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddDataUmum : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private lateinit var binding: ActivityAddDataUmumBinding
    private val jenisSubak = arrayListOf("Subak Basah", "Subak Abian")
    var idKabupatenSelected: String? = ""
    var idKecamatanSelected: String? = ""
    var idDesaSelected: String? = ""
    var jenisSubakSelected: String? = ""
    var activityStatus: String = "add"
    var isTextInputLayoutClicked: Boolean = false
    private lateinit var mMap: GoogleMap
    private var latitude: String? = ""
    private var longitude: String? = ""
    private var idsubak: String? = ""
    private var awesomeValidation: AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101

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
        binding = ActivityAddDataUmumBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@AddDataUmum)
        fetchLocation()

        supportActionBar?.title = "Tambah Data Umum Subak"

        setJenisSubak()
        setKabupaten()

        awesomeValidation.addValidation(
            binding.namaSubak,
            RegexTemplate.NOT_EMPTY,
            "Nama subak belum terisi"
        )
        awesomeValidation.addValidation(
            binding.luasSubak,
            RegexTemplate.NOT_EMPTY,
            "Luas subak belum terisi"
        )
        awesomeValidation.addValidation(
            binding.batasUtaraSubak,
            RegexTemplate.NOT_EMPTY,
            "Batas utara subak belum terisi"
        )
        awesomeValidation.addValidation(
            binding.batasBaratSubak,
            RegexTemplate.NOT_EMPTY,
            "Batas barat subak belum terisi"
        )
        awesomeValidation.addValidation(
            binding.batasSelatanSubak,
            RegexTemplate.NOT_EMPTY,
            "Batas selatan subak belum terisi"
        )
        awesomeValidation.addValidation(
            binding.batasTimurSubak,
            RegexTemplate.NOT_EMPTY,
            "Batas timur subak belum terisi"
        )

        binding.btnLokasi.setOnClickListener {
            val intent = Intent(this, AddMarkerSubak::class.java)
            resultCOntract.launch(intent)
        }

        binding.btnLanjutkan.setOnClickListener {
//            if (awesomeValidation.validate() && jenisSubakSelected !== "" && idKabupatenSelected !== "" && idKecamatanSelected !== "" && idDesaSelected !== "" && latitude !== "" && longitude !== "") {
                addDataSubak()
            val gifImageView = GifImageView(this)
            gifImageView.setImageResource(R.drawable.check)
            SweetAlertDialog(this@AddDataUmum)
                .setTitleText("Berhasil input data umum!")
                .setConfirmButton("Lanjutkan") {
                    val bundle = Bundle()
                    val intent = Intent(this@AddDataUmum, AddDataWilayah::class.java)
                    bundle.putString("idsubak", idsubak)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    finish()
                }
                .setCustomView(gifImageView)
                .show()
//            }

            if (jenisSubakSelected == "") {
                binding.errorSubak.requestFocus()
                binding.errorSubak.error = "Pilih jenis subak"
                binding.errorSubak.setTextColor(Color.RED)
            }
            if (idKabupatenSelected == "") {
                binding.errorKabupaten.requestFocus()
                binding.errorKabupaten.error = "Pilih kabupaten"
                binding.errorKabupaten.setTextColor(Color.RED)
            }
            if (idKecamatanSelected == "") {
                binding.errorKecamatan.requestFocus()
                binding.errorKecamatan.error = "Pilih kecamatan"
                binding.errorKecamatan.setTextColor(Color.RED)
            }
            if (idDesaSelected == "") {
                binding.errorDesa.requestFocus()
                binding.errorDesa.error = "Pilih desa"
                binding.errorDesa.setTextColor(Color.RED)
            }
            if (latitude == "" && longitude == "") {
                binding.errorLokasi.requestFocus()
                binding.errorLokasi.error = "Input lokasi subak"
                binding.errorLokasi.setTextColor(Color.RED)
            }
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

    private fun addDataSubak() {
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

            service.enqueue(object : Callback<SingleTempSubakResponse> {
                override fun onResponse(
                    call: Call<SingleTempSubakResponse>,
                    response: Response<SingleTempSubakResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        val subak = response.body()
                        idsubak = subak?.data?.id.toString()
                        Log.e("id subak di data umum", subak?.data?.id.toString())
                    } else {
                        Toast.makeText(
                            this@AddDataUmum,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<SingleTempSubakResponse>, t: Throwable) {
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
                binding.textSubak.setTextColor(Color.BLACK)
                binding.errorSubak.visibility = View.GONE
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
                                if (idKabupaten.count() >= 0 && position >= 0) {
                                    setKecamatan(idKabupaten[position])
                                }
                                idKabupatenSelected = idKabupaten[position]
                                if (isTextInputLayoutClicked) {
                                    binding.textKabupaten.text = selectedString
                                    binding.textKabupaten.setTextColor(Color.BLACK)
                                    binding.errorKabupaten.visibility = View.GONE
                                } else
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
                                if (isTextInputLayoutClicked) {
                                    binding.textKecamatan.text = selectedString
                                    binding.textKecamatan.setTextColor(Color.BLACK)
                                    binding.errorKecamatan.visibility = View.GONE
                                } else
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
                                if (isTextInputLayoutClicked) {
                                    binding.textDesa.text = selectedString
                                    binding.textDesa.setTextColor(Color.BLACK)
                                    binding.errorDesa.visibility = View.GONE
                                } else
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

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.map) as
                        SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@AddDataUmum)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap.uiSettings.isScrollGesturesEnabled = false

        mMap.setOnMapClickListener {
            binding.errorLokasi.visibility = View.GONE
            mMap.clear()
            val intent = Intent(this, AddMarkerSubak::class.java)
            resultCOntract.launch(intent)
            onResume()
        }
    }
}