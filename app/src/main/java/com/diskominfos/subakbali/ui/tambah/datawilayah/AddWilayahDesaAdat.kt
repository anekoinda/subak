package com.diskominfos.subakbali.ui.tambah.datawilayah

import android.Manifest
import android.content.Context
import  android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
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
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.api.DataDesaAdat
import com.diskominfos.subakbali.api.DesaAdatResponse
import com.diskominfos.subakbali.api.TempSubakDesaAdatResponse
import com.diskominfos.subakbali.databinding.ActivityAddWilayahDesaAdatBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmumViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.leo.searchablespinner.SearchableSpinner
import com.leo.searchablespinner.interfaces.OnItemSelectListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
class AddWilayahDesaAdat : AppCompatActivity() {
    private lateinit var desaAdatViewModel: DesaAdatViewModel
    private lateinit var addDataUmumViewModel: AddDataUmumViewModel
    private lateinit var binding: ActivityAddWilayahDesaAdatBinding
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
//                val polygonPoints = intent.getParcelableArrayListExtra<LatLng>("polygonPoints")
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
        binding = ActivityAddWilayahDesaAdatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Area Subak Desa Adat"

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
//                finish()
//                val intent = Intent(this, AddDataWilayah::class.java)
//                startActivity(intent)
            }
            binding.btnBatal.setOnClickListener {
                finish()
            }
        }

        setDesa()
    }

    private fun setDesa() {
        desaAdatViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DesaAdatViewModel::class.java]

        desaAdatViewModel = ViewModelProvider(this)[DesaAdatViewModel::class.java]
        desaAdatViewModel.getUser().observe(this) { it ->
            if (it != "") {
                desaAdatViewModel.getDesaAdat(it)
                desaAdatViewModel.desaAdatList.observe(this) { desa ->
                    getListDesa(desa)
                    if (it != null) {
                        val list: MutableList<String> = ArrayList()
                        val idDesa: MutableList<String> = ArrayList()

                        desa.forEach {
                            list.add(it.nama+", "+it.sub_district?.name!!+", "+it.district?.name!!)
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

    private fun getListDesa(desa: MutableList<DataDesaAdat>) {
    }

    fun deleteTempSubakDesaAdat(id: String) {
        desaAdatViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DesaAdatViewModel::class.java]
        desaAdatViewModel.getUser().observe(this) { delTempSubakDesaAdat ->
            desaAdatViewModel.deleteDataTempSubakDesaAdat(delTempSubakDesaAdat, id)
            Toast.makeText(this, "hapus data berhasil", Toast.LENGTH_SHORT).show()
        }
    }

    fun simpanData(id: String) {
        getViewModel()

        addDataUmumViewModel.getUser().observe(this) { it ->
            Log.e("data id desa adat", "$id")
            Log.e("token", it)
            val getPolygon = polygon.toString()
            val savePolygon = getPolygon.toRequestBody("text/plain".toMediaType())
            val getDesaId = idDesaSelected.toString()
            val desa_id = getDesaId.toRequestBody("text/plain".toMediaType())
            Log.e("polygon get", "$getPolygon")
            Log.e("polygon get", "$savePolygon")
            val service = ApiConfig.getApiService().addDataTempSubakDesaAdat(
                "Bearer $it",
                getIdSubak.toInt(),
                desa_id
            )

            service.enqueue(object : Callback<TempSubakDesaAdatResponse> {
                override fun onResponse(
                    call: Call<TempSubakDesaAdatResponse>,
                    response: Response<TempSubakDesaAdatResponse>
                ) {
                    Log.e("token", it)
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddWilayahDesaAdat,
                            "Berhasil input data subak",
                            Toast.LENGTH_SHORT
                        ).show()
                        val responseBody = response.body()
                        if (responseBody != null && !responseBody.error) {
                            Toast.makeText(
                                this@AddWilayahDesaAdat,
                                responseBody.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this@AddWilayahDesaAdat,
                            response.message(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<TempSubakDesaAdatResponse>, t: Throwable) {
                    Toast.makeText(
                        this@AddWilayahDesaAdat,
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