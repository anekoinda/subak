package com.diskominfos.subakbali.ui.tambah.datawilayah

import android.Manifest
import android.content.Context
import  android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.*
import com.diskominfos.subakbali.databinding.ActivityAddWilayahDesaAdatBinding
import com.diskominfos.subakbali.databinding.ActivityAddWilayahDesaDinasBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddWilayahDesaDinas : AppCompatActivity(){
    private lateinit var desaDinasViewModel: DesaDinasViewModel
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private lateinit var binding: ActivityAddWilayahDesaDinasBinding
    var isTextInputLayoutClicked: Boolean = false
    var idDesaSelected: String? = ""
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private lateinit var mMap: GoogleMap
    private var latitude: String? = ""
    private var longitude: String? = ""
    private var polygon = ArrayList<LatLng>()
    var getIdSubak: String = ""

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
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddWilayahDesaDinasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Area Subak Desa Dinas"

        getIdSubak = intent.getStringExtra("idsubak")!! // Retrieve the string value from the Intent
        Log.e("idsubakadataaaaaa", "$getIdSubak")
        if (getIdSubak != null) {

            val idtempsubak = getIdSubak
            binding.btnSimpan.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                simpanData("$getIdSubak")
                Log.e("idsubakadat 3333", "$getIdSubak")
                val intent = Intent(this, AddDataWilayah::class.java).apply {
                    putExtra("idsubak", idtempsubak)
                }
                startActivity(intent)
            }
            binding.btnBatal.setOnClickListener {
                finish()
            }
        }

        setDesa()
    }

    private fun setDesa() {
        desaDinasViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DesaDinasViewModel::class.java]

        desaDinasViewModel = ViewModelProvider(this)[DesaDinasViewModel::class.java]
        desaDinasViewModel.getUser().observe(this) { it ->
            if (it != "") {
                desaDinasViewModel.getDesaDinas(it)
                desaDinasViewModel.desaDinasList.observe(this) { desa ->
                    getListDesa(desa)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()
                        val idDesa: MutableList<String> = ArrayList()

                        desa.forEach {
                            list.add(it.name+", "+it.sub_district?.name!!+", "+it.district?.name!!)
                            idDesa.add(it.id)
                        }

                        val searchableSpinner = SearchableSpinner(this)
                        searchableSpinner.windowTitle = "Pilih Desa Adat"
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

    fun simpanData(id: String) {
        getViewModel()

        addDataUmumViewModel.getUser().observe(this) { it ->
            Log.e("data id", "$id")
            Log.e("token", it)
            val getPolygon = polygon.toString()
            val savePolygon = getPolygon.toRequestBody("text/plain".toMediaType())
            val getDesaId = idDesaSelected.toString()
            val desa_id = getDesaId.toRequestBody("text/plain".toMediaType())
            Log.e("polygon get", "$getPolygon")
            Log.e("polygon get", "$savePolygon")
            val service = ApiConfig.getApiService().addDataTempSubakDesaDinas(
                "Bearer $it",
                getIdSubak.toInt(),
                desa_id
            )

            service.enqueue(object : Callback<TempSubakDesaDinasResponse> {
                override fun onResponse(
                    call: Call<TempSubakDesaDinasResponse>,
                    response: Response<TempSubakDesaDinasResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddWilayahDesaDinas,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddWilayahDesaDinas,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddWilayahDesaDinas,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<TempSubakDesaDinasResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddWilayahDesaDinas,
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

}