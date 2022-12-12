package com.diskominfos.subakbali.ui.tambah.dataumum

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.databinding.ActivityAddMarkerSubakBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class AddMarkerSubak : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAddMarkerSubakBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private var lat: String = ""
    private var long: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMarkerSubakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Koordinat Lokasi Subak"

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@AddMarkerSubak)
        fetchLocation()
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(ACCESS_FINE_LOCATION), permissionCode
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(
                    applicationContext, currentLocation.latitude.toString() + "" +
                            currentLocation.longitude, Toast.LENGTH_SHORT
                ).show()
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.mapSubak) as
                        SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@AddMarkerSubak)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        mMap.setOnMapClickListener { latlng ->
            mMap.clear()
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))
            val location = LatLng(latlng.latitude, latlng.longitude)
            mMap.addMarker(MarkerOptions().position(location))

            Toast.makeText(
                applicationContext,
                latlng.latitude.toString() + " " + latlng.longitude,
                Toast.LENGTH_LONG
            ).show()

            val lat = latlng.latitude.toString()
            val long = latlng.longitude.toString()

            binding.btnSimpan.setOnClickListener {
                intent.putExtra(EXTRA_LAT, lat)
                intent.putExtra(EXTRA_LNG, long)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
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
            val long = address.longitude.toString()

            binding.btnSimpan.setOnClickListener {
                intent.putExtra(EXTRA_LAT, lat)
                intent.putExtra(EXTRA_LNG, long)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
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
                fetchLocation()
            }
        }
    }

    companion object {
        const val EXTRA_LAT = ""
        const val EXTRA_LNG = ""
    }
}