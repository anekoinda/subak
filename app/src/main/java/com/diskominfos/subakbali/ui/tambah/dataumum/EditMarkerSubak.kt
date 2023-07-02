package com.diskominfos.subakbali.ui.tambah.dataumum

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.databinding.ActivityAddMarkerSubakBinding
import com.diskominfos.subakbali.databinding.ActivityEditMarkerSubakBinding
import com.diskominfos.subakbali.databinding.ActivityEditTempSubakBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class EditMarkerSubak : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityEditMarkerSubakBinding
    private val permissionCode = 101
    private lateinit var dataViewModel: DataViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditMarkerSubakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Koordinat Lokasi Subak"

        val supportMapFragment =
            (supportFragmentManager.findFragmentById(R.id.mapEditMarkerSubak) as
                    SupportMapFragment?)!!
        supportMapFragment.getMapAsync(this@EditMarkerSubak)

        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val id = bundle.getString("id")
            getBaleSubak("$id")
        }
    }

    fun getBaleSubak(id: String) {
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.getUser().observe(this) { tempSubak ->
            dataViewModel.getTempSubak(tempSubak, id)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[DataViewModel::class.java]
        dataViewModel.tempSubakList.observe(this) {
            val data = it[0]
            val marker = LatLng(data.lat.toDouble(), data.lng.toDouble())
            googleMap.addMarker(
                MarkerOptions()
                    .position(marker)
                    .title(data.nama)
            )
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker, 15f))
        }

        mMap.setOnMapClickListener { latlng ->
            mMap.clear()
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))
            val location = LatLng(latlng.latitude, latlng.longitude)
            mMap.addMarker(MarkerOptions().position(location))

            val lat = latlng.latitude.toString()
            val lng = latlng.longitude.toString()

            binding.btnSimpanMarker.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Edit Lokasi Bale Subak Subak")
                    .setMessage("Apakah anda yakin ingin mengedit lokasi bale subak ini?")
                    .setCancelable(true)
                    .setPositiveButton("Iya") { _, _ ->
                        intent.putExtra("lat", lat)
                        intent.putExtra("lng", lng)
                        setResult(Activity.RESULT_OK, intent)
                        Log.e("lat", lat)
                        Log.e("lng", lng)
                        finish()
                    }.setNegativeButton("Batal") { dialogInterface, _ ->
                        dialogInterface.cancel()
                    }.show()
            }
        }
        binding.btnBatal.setOnClickListener {
            finish()
        }
    }

    fun searchLocation(view: View) {
        mMap.clear()
        val location: String = binding.searchLokasi.text.toString()
        var addressList: List<Address>? = null

        if (location == "") {
//            Toast.makeText(applicationContext, "provide location", Toast.LENGTH_SHORT).show()
        } else {
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val address = addressList!![0]
            val latLng = LatLng(address.latitude, address.longitude)
            mMap.addMarker(MarkerOptions().position(latLng).title(location))
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            Toast.makeText(
                applicationContext,
                address.latitude.toString() + " " + address.longitude,
                Toast.LENGTH_LONG
            ).show()

            val lat = address.latitude.toString()
            val lng = address.longitude.toString()

//            binding.btnSimpan.setOnClickListener {
//                intent.putExtra("lat", lat)
//                intent.putExtra("long", lng)
//                setResult(Activity.RESULT_OK, intent)
//                finish()
//            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {

            }
        }
    }
}