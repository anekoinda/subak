package com.diskominfos.subakbali.ui.tambah.datawilayah

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.adapter.TempSubakDesaAdatAdapter
import com.diskominfos.subakbali.adapter.TempSubakDesaDinasAdapter
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAddDataWilayahBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.profil.ProfilViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.diskominfos.subakbali.ui.tambah.parahyangan.AddDataParahyangan
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class AddDataWilayah : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityAddDataWilayahBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var token: String = ""
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private val permissionCode = 101
    private lateinit var mMap: GoogleMap
    private var polygon = ArrayList<LatLng>()
    private var longitude: String? = ""
    private var idsubak: String? = ""
    private var getIdSubak: String? = ""
    private lateinit var setAdapterDesaAdat: TempSubakDesaAdatAdapter
    private lateinit var setAdapterDesaDinas: TempSubakDesaDinasAdapter

    private val _tempSubakDesaAdatList = MutableLiveData<MutableList<GetAllDataTempSubakDesaAdat>>()
    val tempSubakDesaAdatList: LiveData<MutableList<GetAllDataTempSubakDesaAdat>> =
        _tempSubakDesaAdatList

    private val _tempSubakDesaDinasList =
        MutableLiveData<MutableList<GetAllDataTempSubakDesaDinas>>()
    val tempSubakDesaDinasList: LiveData<MutableList<GetAllDataTempSubakDesaDinas>> =
        _tempSubakDesaDinasList

    private val resultCOntract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            if (result?.resultCode == RESULT_OK) {
                polygon = result.data?.extras?.get("polygon") as ArrayList<LatLng>
                longitude = result.data?.getStringExtra("lng").toString()

                Log.e("polygonadddatawilayah", "$polygon")

                val boundsBuilder = LatLngBounds.Builder()
                if (polygon != null) {
                    val polygonOptions = PolygonOptions()
                    for (point in polygon) {
                        boundsBuilder.include(point)
                        polygonOptions.add(point)
                    }

                    val bounds = boundsBuilder.build()
                    val padding = 100 // Padding in pixels to apply around the bounds

                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                    mMap.animateCamera(cameraUpdate)

                    polygonOptions.fillColor(Color.argb(128, 255, 0, 0))
                    polygonOptions.strokeColor(Color.RED)
                    polygonOptions.strokeWidth(5f)

                    mMap.addPolygon(polygonOptions)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddDataWilayahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@AddDataWilayah)
        fetchLocation()

        supportActionBar?.title = "Tambah Data Wilayah Subak"

        getIdSubak = intent.getStringExtra("idsubak") // Retrieve the string value from the Intent
        Log.e("id subak data wilayah", "$getIdSubak")
        if (getIdSubak != null) {

            val idtempsubak = getIdSubak
            binding.btnAddDesaAdat.setOnClickListener {
                val intent = Intent(this, AddWilayahDesaAdat::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }

            binding.btnAddDesaDinas.setOnClickListener {
                val intent = Intent(this, AddWilayahDesaDinas::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
                finish()
            }
        }

        val idtempsubak = getIdSubak
        binding.btnSimpan.setOnClickListener {
            simpanData("$getIdSubak")
            val gifImageView = GifImageView(this)
            gifImageView.setImageResource(R.drawable.check)
            SweetAlertDialog(this@AddDataWilayah)
                .setTitleText("Berhasil input data wilayah!")
                .setConfirmButton("Lanjutkan") {
                    val bundle = Bundle()
                    val intent = Intent(this@AddDataWilayah, AddDataParahyangan::class.java)
                    bundle.putString("idsubak", idtempsubak)
                    intent.putExtras(bundle)
                    startActivity(intent)
                    finish()
                }
                .setCustomView(gifImageView)
                .show()
            binding.btnLewati.setOnClickListener {
                finish()
            }
        }
        setupRecyclerViewDesaAdat()
        setupRecyclerViewDesaDinas()
        setupViewModel()
    }

    private fun setupRecyclerViewDesaAdat() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvDesaAdat.layoutManager = layoutManager
        binding.rvDesaAdat.setHasFixedSize(true)
    }

    private fun setupRecyclerViewDesaDinas() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvDesaDinas.layoutManager = layoutManager
        binding.rvDesaDinas.setHasFixedSize(true)
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
                val supportMapFragment =
                    (supportFragmentManager.findFragmentById(R.id.mapPolygon) as
                            SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@AddDataWilayah)
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
            val intent = Intent(this, AddPolygonSubak::class.java)
            resultCOntract.launch(intent)
            onResume()
        }
    }

    fun simpanData(id: String) {
        getViewModel()

        addDataUmumViewModel.getUser().observe(this) { it ->
            val getPolygon = polygon.toString()
            val savePolygon = getPolygon.toRequestBody("text/plain".toMediaType())
            val service = ApiConfig.getApiService().updateDataSubakPolygon(
                "Bearer $it",
                id,
                savePolygon
            )

            service.enqueue(object : Callback<DataSubakResponse> {
                override fun onResponse(
                    call: Call<DataSubakResponse>,
                    response: Response<DataSubakResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddDataWilayah,
                            "Berhasil input data wilayah",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddDataWilayah,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddDataWilayah,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<DataSubakResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddDataWilayah,
                        "Cannot instance Retrofit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        }
    }

    fun getViewModel() {
        addDataUmumViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AddDataUmumViewModel::class.java]

        addDataUmumViewModel = ViewModelProvider(this)[AddDataUmumViewModel::class.java]
    }

    private fun setupViewModel() {
        val profilViewModel: ProfilViewModel by viewModels {
            val pref = this.dataStore
            ViewModelFactory(
                UserPreference.getInstance(pref)
            )
        }

        val dataViewModel: DataViewModel by viewModels {
            val pref = this.dataStore
            ViewModelFactory(
                UserPreference.getInstance(pref)
            )
        }

        profilViewModel.getUser().observe(this) {

            if (it != "") {

                getListTempSubakDesaAdat(it)
                var getName: String = ""
                profilViewModel.getName().observe(this) { it ->
                    getName = it
//                    binding.namaUser.text = getName
                }
                tempSubakDesaAdatList.observe(this) { tempSubakDesaAdat ->
                    getList(tempSubakDesaAdat)
                    if (tempSubakDesaAdat != null && tempSubakDesaAdat.isNotEmpty()) {
                        // showLoading(false)
                        Log.e("desa adat list", "$tempSubakDesaAdat")
                        binding.emptyObjectDesaAdat.visibility = View.GONE
                    } else {
                        binding.emptyObjectDesaAdat.visibility = View.VISIBLE
                    }
                }
            } else {

                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }

        profilViewModel.getUser().observe(this) {
            token = it
            if (it != "") {
                getListTempSubakDesaDinas(it)
                tempSubakDesaDinasList.observe(this) { tempSubakDesaDinas ->
                    getListDesaDinas(tempSubakDesaDinas)
                    if (tempSubakDesaDinas != null && tempSubakDesaDinas.isNotEmpty()) {
                        // showLoading(false)
                        Log.e("desa dinas list", "$tempSubakDesaDinas")
                        binding.emptyObjectDesaDinas.visibility = View.GONE
                    } else {
                        binding.emptyObjectDesaDinas.visibility = View.VISIBLE
                    }
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                this.startActivity(intent)
            }
        }
    }


    private fun getList(tempSubakDesaAdat: MutableList<GetAllDataTempSubakDesaAdat>) {
        val tempSubakDesaAdatAdapter = TempSubakDesaAdatAdapter(
            tempSubakDesaAdat,
            object : TempSubakDesaAdatAdapter.OnAdapterAllTempSubakDesaAdatListener {
                override fun onClick(result: GetAllDataTempSubakDesaAdat) {
                    val bundle = Bundle()
//                    val intent = Intent(this@AddDataWilayah, DetailTempSubak::class.java)
//                    bundle.putString("id", data.id)
//                    intent.putExtras(bundle)
//                    startActivity(intent)
                }
            }, token
        )
        binding.rvDesaAdat.adapter = tempSubakDesaAdatAdapter
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


        setAdapterDesaAdat = tempSubakDesaAdat.let {
            TempSubakDesaAdatAdapter(
                it,
                object : TempSubakDesaAdatAdapter.OnAdapterAllTempSubakDesaAdatListener {
                    override fun onClick(result: GetAllDataTempSubakDesaAdat) {
//                        val bundle = Bundle()
//                        val intent = Intent(this@DraftSubak, DetailTempSubak::class.java)
//                        bundle.putString("id", data.id)
//                        intent.putExtras(bundle)
//                        startActivity(intent)
                    }
                }, token
            )
        }
    }

    private fun getListDesaDinas(tempSubakDesaDinas: MutableList<GetAllDataTempSubakDesaDinas>) {
        val tempSubakDesaDinasAdapter = TempSubakDesaDinasAdapter(
            tempSubakDesaDinas,
            object : TempSubakDesaDinasAdapter.OnAdapterAllTempSubakDesaDinasListener {
                override fun onClick(result: GetAllDataTempSubakDesaDinas) {
                    val bundle = Bundle()
//                    val intent = Intent(this@AddDataWilayah, DetailTempSubak::class.java)
//                    bundle.putString("id", data.id)
//                    intent.putExtras(bundle)
//                    startActivity(intent)
                }
            }, token
        )
        binding.rvDesaDinas.adapter = tempSubakDesaDinasAdapter
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


        setAdapterDesaDinas = tempSubakDesaDinas.let {
            TempSubakDesaDinasAdapter(
                it,
                object : TempSubakDesaDinasAdapter.OnAdapterAllTempSubakDesaDinasListener {
                    override fun onClick(result: GetAllDataTempSubakDesaDinas) {
//                        val bundle = Bundle()
//                        val intent = Intent(this@DraftSubak, DetailTempSubak::class.java)
//                        bundle.putString("id", data.id)
//                        intent.putExtras(bundle)
//                        startActivity(intent)
                    }
                }, token
            )
        }
    }

    fun getListTempSubakDesaAdat(token: String) {
        val client =
            ApiConfig.getApiService().getListTempSubakDesaAdat("Bearer $token", "$getIdSubak")
//        val client = ApiConfig.getApiService().getListTempSubakDesaAdat("Bearer $token")
        Log.e("data id temp", "$getIdSubak")
        client.enqueue(object : Callback<GetTempSubakDesaAdatResponse> {
            override fun onResponse(
                call: Call<GetTempSubakDesaAdatResponse>,
                response: Response<GetTempSubakDesaAdatResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _tempSubakDesaAdatList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetTempSubakDesaAdatResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }

    fun getListTempSubakDesaDinas(token: String) {
        val client =
            ApiConfig.getApiService().getListTempSubakDesaDinas("Bearer $token", "$getIdSubak")
//        val client = ApiConfig.getApiService().getListTempSubakDesaAdat("Bearer $token")
        Log.e("data id temp", "$getIdSubak")
        client.enqueue(object : Callback<GetTempSubakDesaDinasResponse> {
            override fun onResponse(
                call: Call<GetTempSubakDesaDinasResponse>,
                response: Response<GetTempSubakDesaDinasResponse>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _tempSubakDesaDinasList.value = response.body()?.data
                    }

                } else {
                    Log.e(ContentValues.TAG, "onFailure : ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<GetTempSubakDesaDinasResponse>, t: Throwable) {
                Log.d(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}