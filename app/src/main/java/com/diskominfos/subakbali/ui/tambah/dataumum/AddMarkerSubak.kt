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
//                Toast.makeText(
//                    applicationContext, currentLocation.latitude.toString() + ", " +
//                            currentLocation.longitude, Toast.LENGTH_SHORT
//                ).show()
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.mapSubak) as
                        SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@AddMarkerSubak)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))

        mMap.setOnMapClickListener { latlng ->
            mMap.clear()
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latlng))
            val location = LatLng(latlng.latitude, latlng.longitude)
            mMap.addMarker(MarkerOptions().position(location))

            val lat = latlng.latitude.toString()
            val lng = latlng.longitude.toString()

            binding.btnSimpan.setOnClickListener {
                intent.putExtra("lat", lat)
                intent.putExtra("lng", lng)
                setResult(Activity.RESULT_OK, intent)
                Log.e("lat", lat)
                Log.e("lng", lng)
                finish()
            }
        }
    }

    fun searchLocation(view: View) {
        mMap.clear()
        val location: String = binding.searchLokasi.text.toString()
        var addressList: List<Address>? = null

        if (location == "") {
            Toast.makeText(applicationContext, "Masukkan lokasi yang ingin dicari!", Toast.LENGTH_SHORT).show()
        } else {
            val geoCoder = Geocoder(this)
            try {
                addressList = geoCoder.getFromLocationName(location, 1)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (addressList != null && addressList.isNotEmpty()) {
                val address = addressList[0]
                val latLng = LatLng(address.latitude, address.longitude)
                mMap.addMarker(MarkerOptions().position(latLng).title(location))
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                Toast.makeText(
                    applicationContext,
                    "Lokasi ditemukan!",
                    Toast.LENGTH_LONG
                ).show()

                val lat = address.latitude.toString()
                val lng = address.longitude.toString()

                binding.btnSimpan.setOnClickListener {
                    intent.putExtra("lat", lat)
                    intent.putExtra("lng", lng)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            } else {
                Toast.makeText(applicationContext, "Lokasi tidak ditemukan!", Toast.LENGTH_SHORT).show()
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
}