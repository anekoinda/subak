package com.diskominfos.subakbali.ui.tambah.parahyangan

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.basgeekball.awesomevalidation.AwesomeValidation
import com.basgeekball.awesomevalidation.ValidationStyle
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAddDataPuraBinding
import com.diskominfos.subakbali.databinding.ActivityAddKramaBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.AddMarkerSubak
import com.diskominfos.subakbali.ui.tambah.dataumum.EditTempSubak
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
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddDataPura : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private lateinit var puraViewModel: PuraViewModel
    private lateinit var binding: ActivityAddDataPuraBinding
    var idKabupatenSelected: String = ""
    var idKecamatanSelected: String? = ""
    var idDesaDinasSelected: String? = ""
    var idDesaAdatSelected: String? = ""
    var idWuku: Int = 0
    var idSaptawara: Int = 0
    var namePancawara: String? = ""
    var activityStatus: String = "add"
    var isTextInputLayoutClicked: Boolean = false
    private var getIdSubak: String? = ""
    private lateinit var mMap: GoogleMap
    private var latitude: String? = ""
    private var longitude: String? = ""
    private lateinit var listWuku: ArrayList<String>
    private lateinit var listSaptawara: ArrayList<String>
    private var awesomeValidation: AwesomeValidation = AwesomeValidation(ValidationStyle.BASIC)
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private val jenisOdalan = arrayListOf("Sasih", "Pawukon")
    private val bulanOdalan = arrayListOf("Purnama", "Tilem")
    private val status = arrayListOf("Aktif", "Tidak Aktif")
    private val verifikasi = arrayListOf("Draft", "Reject", "Verif")
    private val sapwatara =
        arrayListOf("Redite", "Soma", "Anggara", "Buddha", "Wrespati", "Sukra", "Saniscara")
    private val jenisSasih = arrayListOf(
        "Kasa",
        "Karo",
        "Katiga",
        "Kapat",
        "Kalima",
        "Kaenem",
        "Kepitu",
        "Kewolu",
        "Kesanga",
        "Kedasa",
        "Jiyesta",
        "Shada"
    )
    var jenisOdalanSelected: String? = ""
    var bulanOdalanSelected: String? = ""
    var jenisSasihSelected: String? = ""
    var wukuSelected: String? = ""
    var saptawaraSelected: String? = ""
    var statusSelected: String? = ""
    var verifikasiSelected: String? = ""

    //    var pancawaraSelected: String? = ""
    var jenis_odalan: String = ""

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
        binding = ActivityAddDataPuraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Tambah Pura"

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@AddDataPura)
        fetchLocation()

        setJenisOdalan()
        setBulanOdalan()
        setJenisSasih()
        wuku_detail()
        saptawara()
        setStatus()
        setVerifikasi()
        setKabupaten()

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent
        Log.e("id subak add pura", "$getIdSubak")
        if (getIdSubak != null) {
            val idtempsubak = getIdSubak
            Log.e("id subak add puraa", "$getIdSubak")

            binding.btnLanjutkan.setOnClickListener {
                addDataPura("$getIdSubak")
                val intent = Intent(this, AddDataParahyangan::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                    Log.e("id subak add puraaa", "$idtempsubak")
                }
                startActivity(intent)
            }
        }
    }

    private fun addDataPura(id: String) {
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
            val getName = binding.namaPura.text.toString()
            val nama = getName.toRequestBody("text/plain".toMediaType())
            val getTahunBerdiri = binding.tahunBerdiri.text.toString()
            val tahun_berdiri = getTahunBerdiri.toRequestBody("text/plain".toMediaType())
            val getNamaPemangku = binding.namaPemangku.text.toString()
            val nama_pemangku = getNamaPemangku.toRequestBody("text/plain".toMediaType())
            val getTelpPemangku = binding.telpPemangku.text.toString()
            val telp_pemangku = getTelpPemangku.toRequestBody("text/plain".toMediaType())
            val getDeskripsi = binding.deskripsiPura.text.toString()
            val deskripsi = getDeskripsi.toRequestBody("text/plain".toMediaType())
            val getKabupatenId = idKabupatenSelected
            val kabupaten_id = getKabupatenId.toRequestBody("text/plain".toMediaType())
            val getKecamatanId = idKecamatanSelected.toString()
            val kecamatan_id = getKecamatanId.toRequestBody("text/plain".toMediaType())
            val getDesaDinas = idDesaDinasSelected.toString()
            val desa_dinas_id = getDesaDinas.toRequestBody("text/plain".toMediaType())
            val getDesaAdat = idDesaAdatSelected.toString()
            val desa_adat_id = getDesaAdat.toRequestBody("text/plain".toMediaType())
            val getLongitude = longitude.toString()
            val lng = getLongitude.toRequestBody("text/plain".toMediaType())
            val getLatitude = latitude.toString()
            val lat = getLatitude.toRequestBody("text/plain".toMediaType())
            val getJenisOdalan = jenisOdalanSelected
            val odalan_jenis = getJenisOdalan?.toRequestBody("text/plain".toMediaType())
            val getOdalanSaptawara = saptawaraSelected
            val odalan_saptawara = getOdalanSaptawara?.toRequestBody("text/plain".toMediaType())
            val getOdalanPancawara = namePancawara
            val odalan_pancawara = getOdalanPancawara?.toRequestBody("text/plain".toMediaType())
            val getOdalanBulan = bulanOdalanSelected
            val odalan_bulan = getOdalanBulan?.toRequestBody("text/plain".toMediaType())
            val getOdalanWuku = wukuSelected
            val odalan_wuku = getOdalanWuku?.toRequestBody("text/plain".toMediaType())
            val getOdalanSasih = jenisSasihSelected
            val odalan_sasih = getOdalanSasih?.toRequestBody("text/plain".toMediaType())
            val getKeteranganOdalan = binding.keteranganOdalanSasih.text.toString()
            val odalan_keterangan = getKeteranganOdalan.toRequestBody("text/plain".toMediaType())
            val getStatus = statusSelected
            val status = getStatus?.toRequestBody("text/plain".toMediaType())
            val getVerifikasi = verifikasiSelected
            val verifikasi = getVerifikasi?.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().addDataPura(
                "Bearer $it",
                0,
                kabupaten_id,
                kecamatan_id,
                desa_dinas_id,
                desa_adat_id,
                nama,
                deskripsi,
                tahun_berdiri,
                nama_pemangku,
                telp_pemangku,
                lat,
                lng,
                odalan_jenis!!,
                odalan_saptawara!!,
                odalan_pancawara!!,
                odalan_bulan!!,
                odalan_wuku!!,
                odalan_sasih!!,
                odalan_keterangan,
                verifikasi!!,
                status!!
            )

            service.enqueue(object : Callback<PuraResponse> {
                override fun onResponse(
                    call: Call<PuraResponse>,
                    response: Response<PuraResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddDataPura,
                            "Berhasil input data pura",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddDataPura,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddDataPura,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<PuraResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddDataPura,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
            val intent = Intent(this, AddDataParahyangan::class.java)
            startActivity(intent)
        }
    }

    fun wuku_detail(): ArrayList<String> {
        val wukuArray = arrayOf(
            arrayOf("id" to 0, "number" to 1, "name" to "Sinta"),
            arrayOf("id" to 1, "number" to 2, "name" to "Landep"),
            arrayOf("id" to 2, "number" to 3, "name" to "Ukir"),
            arrayOf("id" to 3, "number" to 4, "name" to "Kulantir"),
            arrayOf("id" to 4, "number" to 5, "name" to "Tolu"),
            arrayOf("id" to 5, "number" to 6, "name" to "Gumbreg"),
            arrayOf("id" to 6, "number" to 7, "name" to "Wariga"),
            arrayOf("id" to 7, "number" to 8, "name" to "Warigadian"),
            arrayOf("id" to 8, "number" to 9, "name" to "Julungwangi"),
            arrayOf("id" to 9, "number" to 10, "name" to "Sungsang"),
            arrayOf("id" to 10, "number" to 11, "name" to "Dungulan"),
            arrayOf("id" to 11, "number" to 12, "name" to "Kuningan"),
            arrayOf("id" to 12, "number" to 13, "name" to "Langkir"),
            arrayOf("id" to 13, "number" to 14, "name" to "Medangsia"),
            arrayOf("id" to 14, "number" to 15, "name" to "Pujut"),
            arrayOf("id" to 15, "number" to 16, "name" to "Pahang"),
            arrayOf("id" to 16, "number" to 17, "name" to "Krulut"),
            arrayOf("id" to 17, "number" to 18, "name" to "Merakih"),
            arrayOf("id" to 18, "number" to 19, "name" to "Tambir"),
            arrayOf("id" to 19, "number" to 20, "name" to "Medangkungan"),
            arrayOf("id" to 20, "number" to 21, "name" to "Matal"),
            arrayOf("id" to 21, "number" to 22, "name" to "Uye"),
            arrayOf("id" to 22, "number" to 23, "name" to "Menail"),
            arrayOf("id" to 23, "number" to 24, "name" to "Prangbakat"),
            arrayOf("id" to 24, "number" to 25, "name" to "Bala"),
            arrayOf("id" to 25, "number" to 26, "name" to "Ugu"),
            arrayOf("id" to 26, "number" to 27, "name" to "Wayang"),
            arrayOf("id" to 27, "number" to 28, "name" to "Kelawu"),
            arrayOf("id" to 28, "number" to 29, "name" to "Dukut"),
            arrayOf("id" to 29, "number" to 30, "name" to "Watugunung"),
        )
        listWuku = wukuArray.flatMap { subArray ->
            subArray.filter { it.first == "name" }.map { it.second.toString() }
        } as ArrayList<String>

        Log.e("nameee", "$listWuku")

        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Wuku"
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    wukuSelected = selectedString
                val nameToFind = wukuSelected
                Log.e("id haha", "$nameToFind")
                val subArray =
                    wukuArray.find { it.any { tuple -> tuple.first == "name" && tuple.second == nameToFind } }
                idWuku = subArray?.find { it.first == "number" }?.second as Int
                Log.e("id haha", "$idWuku")
                binding.textWuku.text = wukuSelected
            }
        }

        searchableSpinner.setSpinnerListItems(listWuku)
        binding.textWuku.keyListener = null
        binding.textWuku.setOnClickListener {
            isTextInputLayoutClicked = true
            searchableSpinner.show()
        }

        binding.editTextSpinner.keyListener = null
        binding.editTextSpinner.setOnClickListener {
            searchableSpinner.highlightSelectedItem = false
            isTextInputLayoutClicked = false
            searchableSpinner.show()
        }


        val wukuList = ArrayList<String>()
        return wukuList
    }

    fun pancawara(): Array<Array<Any>> {
        return arrayOf(
            arrayOf(
                "id" to 0,
                "number" to 1,
                "name" to "Umanis",
                "subject" to "umanis"
            ),
            arrayOf(
                "id" to 1,
                "number" to 2,
                "name" to "Paing",
                "subject" to "paing"
            ),
            arrayOf(
                "id" to 2,
                "number" to 3,
                "name" to "Pon",
                "subject" to "pon"
            ),
            arrayOf(
                "id" to 3,
                "number" to 4,
                "name" to "Wage",
                "subject" to "wage"
            ),
            arrayOf(
                "id" to 4,
                "number" to 5,
                "name" to "Kliwon",
                "subject" to "kliwon"
            )
        )
    }

    fun saptawara(): ArrayList<String> {
        val saptawaraArray = arrayOf(
            arrayOf(
                "id" to 0,
                "number" to 1,
                "name" to "Redite",
                "subject" to "redite"
            ),
            arrayOf(
                "id" to 1,
                "number" to 2,
                "name" to "Soma",
                "subject" to "soma"
            ),
            arrayOf(
                "id" to 2,
                "number" to 3,
                "name" to "Anggara",
                "subject" to "anggara"
            ),
            arrayOf(
                "id" to 3,
                "number" to 4,
                "name" to "Buddha",
                "subject" to "buddha"
            ),
            arrayOf(
                "id" to 4,
                "number" to 5,
                "name" to "Wrespati",
                "subject" to "wrespati"
            ),
            arrayOf(
                "id" to 5,
                "number" to 6,
                "name" to "Sukra",
                "subject" to "sukra"
            ),
            arrayOf(
                "id" to 6,
                "number" to 7,
                "name" to "Saniscara",
                "subject" to "saniscara"
            )
        )

        listSaptawara = saptawaraArray.flatMap { subArray ->
            subArray.filter { it.first == "name" }.map { it.second.toString() }
        } as ArrayList<String>

        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Wuku"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    saptawaraSelected = selectedString
                val nameToFind = saptawaraSelected
                Log.e("id haha", "$nameToFind")
                val subArray =
                    saptawaraArray.find { it.any { tuple -> tuple.first == "name" && tuple.second == nameToFind } }
                idSaptawara = subArray?.find { it.first == "id" }?.second as Int
                Log.e("id haha", "$idSaptawara")
                binding.textSaptawara.text = saptawaraSelected
                val getIdWuku = idWuku
                get_pancawara_by_wuku_saptawara(getIdWuku, idSaptawara)
                Log.e("get wuku 1", "$getIdWuku")
                Log.e("get saptawara 1", "$idSaptawara")
            }
        }

        searchableSpinner.setSpinnerListItems(listSaptawara)
        binding.textSaptawara.keyListener = null
        binding.textSaptawara.setOnClickListener {
            isTextInputLayoutClicked = true
            searchableSpinner.show()
        }

        binding.editTextSpinner.keyListener = null
        binding.editTextSpinner.setOnClickListener {
            searchableSpinner.highlightSelectedItem = false
            isTextInputLayoutClicked = false
            searchableSpinner.show()
        }

        val saptawaraList = ArrayList<String>()
        return saptawaraList
    }

    fun get_pancawara_by_wuku_saptawara(wuku: Int, saptawara: Int): Map<String, Any> {
        val x = (wuku * 7) + saptawara
        val sisa = x % 5
        Log.e("get wuku", "$wuku")
        Log.e("get saptawara", "$saptawara")
        Log.e("get sisa", "$sisa")
        val pancawara: Map<String, Any> = when (sisa) {
            1 -> mapOf(
                "id" to 0,
                "number" to 1,
                "name" to "Umanis",
                "subject" to "umanis"
            )
            2 -> mapOf(
                "id" to 1,
                "number" to 2,
                "name" to "Paing",
                "subject" to "paing"
            )
            3 -> mapOf(
                "id" to 2,
                "number" to 3,
                "name" to "Pon",
                "subject" to "pon"
            )
            4 -> mapOf(
                "id" to 3,
                "number" to 4,
                "name" to "Wage",
                "subject" to "wage"
            )
            0, 5 -> mapOf(
                "id" to 4,
                "number" to 5,
                "name" to "Kliwon",
                "subject" to "kliwon"
            )
            else -> throw IllegalArgumentException("Invalid sisa value")
        }
        namePancawara = pancawara["name"].toString()
        Log.e("pancawara", "$namePancawara")
        if (saptawaraSelected == null) {
            binding.textPancawara.setText(" ")
        } else {
            binding.textPancawara.setText(namePancawara)
        }
        return pancawara
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
                                    this@AddDataPura,
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
                                    setDesaAdat(idKecamatan[position])
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
                                    binding.textDesaDinas.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textDesaDinas.keyListener = null
                        binding.textDesaDinas.setOnClickListener {
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

    private fun setDesaAdat(id: String) {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
        addDataUmumViewModel.getUser().observe(this) { it ->
            if (it != "") {
                addDataUmumViewModel.getDesaAdat(it, id)
                addDataUmumViewModel.desaAdatList.observe(this) { desaAdat ->
                    getListDesaAdat(desaAdat)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()
                        val idDesa: MutableList<String> = ArrayList()

                        desaAdat.forEach {
                            list.add(it.nama)
                            idDesa.add(it.id)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Kecamatan"
                        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
                            override fun setOnItemSelectListener(
                                position: Int,
                                selectedString: String
                            ) {
                                idDesaAdatSelected = idDesa[position]
                                if (isTextInputLayoutClicked)
                                    binding.textDesaAdat.text = selectedString
                                else
                                    binding.editTextSpinner.setText(selectedString)
                            }
                        }

                        searchableSpinner.setSpinnerListItems(list as ArrayList<String>)
                        binding.textDesaAdat.keyListener = null
                        binding.textDesaAdat.setOnClickListener {
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

    private fun getListDesaAdat(desaAdat: MutableList<DataDesaAdat>) {
    }

    private fun setJenisOdalan() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Jenis Odalan"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    jenisOdalanSelected = selectedString

                binding.textJenisOdalan.text = jenisOdalanSelected
                if (jenisOdalanSelected == "Sasih") {
                    binding.jenisOdalanPawukon.visibility = View.INVISIBLE
                    binding.jenisOdalanSasih.visibility = View.VISIBLE
                } else {
                    binding.jenisOdalanSasih.visibility = View.INVISIBLE
                    binding.jenisOdalanPawukon.visibility = View.VISIBLE
                }
            }
        }

        searchableSpinner.setSpinnerListItems(jenisOdalan)
        binding.textJenisOdalan.keyListener = null
        binding.textJenisOdalan.setOnClickListener {
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

    private fun setBulanOdalan() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Bulan Odalan"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    bulanOdalanSelected = selectedString
                binding.textJenisBulan.text = bulanOdalanSelected
            }
        }

        searchableSpinner.setSpinnerListItems(bulanOdalan)
        binding.textJenisBulan.keyListener = null
        binding.textJenisBulan.setOnClickListener {
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

    private fun setJenisSasih() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Sasih"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    jenisSasihSelected = selectedString
                binding.textJenisSasih.text = jenisSasihSelected
            }
        }

        searchableSpinner.setSpinnerListItems(jenisSasih)
        binding.textJenisSasih.keyListener = null
        binding.textJenisSasih.setOnClickListener {
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

    private fun setWuku() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Wuku"
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    wukuSelected = selectedString
                binding.textWuku.text = wukuSelected
            }
        }

        searchableSpinner.setSpinnerListItems(listWuku)
        binding.textWuku.keyListener = null
        binding.textWuku.setOnClickListener {
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

    private fun setSaptawara() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Wuku"
        searchableSpinner.searchViewVisibility = SearchableSpinner.SpinnerView.GONE
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    saptawaraSelected = selectedString
                binding.textSaptawara.text = saptawaraSelected
            }
        }

        searchableSpinner.setSpinnerListItems(sapwatara)
        binding.textSaptawara.keyListener = null
        binding.textSaptawara.setOnClickListener {
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

    private fun setStatus() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Status"
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {

                if (position == 0) {
                    statusSelected = "1"
                } else {
                    statusSelected = "0"
                }
                if (isTextInputLayoutClicked)
                    binding.textStatus.text = selectedString
            }
        }

        searchableSpinner.setSpinnerListItems(status)
        binding.textStatus.keyListener = null
        binding.textStatus.setOnClickListener {
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

    private fun setVerifikasi() {
        val searchableSpinner = SearchableSpinner(this)
        searchableSpinner.windowTitle = "Pilih Status Verifikasi"
        searchableSpinner.onItemSelectListener = object : OnItemSelectListener {
            override fun setOnItemSelectListener(
                position: Int,
                selectedString: String
            ) {
                if (isTextInputLayoutClicked)
                    verifikasiSelected = selectedString
                binding.textVerifikasi.text = verifikasiSelected
            }
        }

        searchableSpinner.setSpinnerListItems(verifikasi)
        binding.textVerifikasi.keyListener = null
        binding.textVerifikasi.setOnClickListener {
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
//                Toast.makeText(
//                    applicationContext, currentLocation.latitude.toString() + ", " +
//                            currentLocation.longitude, Toast.LENGTH_SHORT
//                ).show()
                val supportMapFragment =
                    (supportFragmentManager.findFragmentById(R.id.mapDataPura) as
                            SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@AddDataPura)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap.uiSettings.isScrollGesturesEnabled = false

        mMap.setOnMapClickListener {
            mMap.clear()
            val intent = Intent(this, AddMarkerSubak::class.java)
            resultCOntract.launch(intent)
            onResume()
        }
    }
}
