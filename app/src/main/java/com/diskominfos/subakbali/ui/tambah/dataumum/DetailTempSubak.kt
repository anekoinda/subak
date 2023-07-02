package com.diskominfos.subakbali.ui.tambah.dataumum

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.api.ApiConfig
import com.diskominfos.subakbali.api.DataKabupaten
import com.diskominfos.subakbali.api.DataTempSubak
import com.diskominfos.subakbali.api.TempSubakResponse
import com.diskominfos.subakbali.databinding.ActivityDetailTempSubakBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.ui.data.DataFragment
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.tambah.dataumum.AddMarkerSubak
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class DetailTempSubak : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDetailTempSubakBinding
    private lateinit var dataViewModel: DataViewModel
    private var token: String = ""
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailTempSubakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val supportMapFragment =
            (supportFragmentManager.findFragmentById(R.id.mapDetailSubak) as
                    SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this@DetailTempSubak)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val id = bundle.getString("id")
            detailTempSubak("$id")

            binding.btnHapus.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Hapus Draf Subak")
                    .setMessage("Apakah anda yakin ingin menghapus draf subak ini?")
                    .setCancelable(true)
                    .setPositiveButton("Iya") { _, _ ->
                        deleteTempSubak("$id")
                    }.setNegativeButton("Batal") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.show()
            }
            binding.btnEdit.setOnClickListener {
                val intent = Intent(this, EditTempSubak::class.java)
                bundle.putString("id", "$id")
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    fun deleteTempSubak(id: String) {
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.getUser().observe(this) { delTempSubak ->
            dataViewModel.deleteDataTempSubak(delTempSubak, id)
            Toast.makeText(this, "hapus data berhasil", Toast.LENGTH_SHORT).show()
        }
    }

    fun detailTempSubak(id: String) {
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.getUser().observe(this) { tempSubak ->
            dataViewModel.getTempSubak(tempSubak, id)
            dataViewModel.tempSubakList.observe(this) {
                binding.apply {
                    val data = it[0]
                    kabSubak.setText(data.kabupaten?.name)
                    kecSubak.setText(data.kecamatan?.name)
                    desaSubak.setText(data.desa_pengampu?.name)
                    namaSubak.setText(data.nama)
                    jenisSubak.setText(data.jenis_subak)
                    luasSubak.setText(data.luas)
                    batasBaratSubak.setText(data.batas_barat)
                    batasTimurSubak.setText(data.batas_timur)
                    batasSelatanSubak.setText(data.batas_selatan)
                    batasUtaraSubak.setText(data.batas_utara)
                }
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
                binding.lokasiSubak.text = strReturnedAddress.toString()
            } else {
                binding.lokasiSubak.text = "No Address returned!"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            binding.lokasiSubak.text = "Canont get Address!"
        }
        return strAdd
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        googleMap.uiSettings.isScrollGesturesEnabled = false
        dataViewModel.tempSubakList.observe(this) {
            val data = it[0]
            val marker = LatLng(data.lat.toDouble(), data.lng.toDouble())
            getCompleteAddressString(data.lat.toDouble(), data.lng.toDouble())
            googleMap.addMarker(
                MarkerOptions()
                    .position(marker)
                    .title(data.nama)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
        }
    }
}


