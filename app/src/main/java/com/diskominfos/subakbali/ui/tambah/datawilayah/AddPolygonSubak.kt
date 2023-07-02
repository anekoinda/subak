package com.diskominfos.subakbali.ui.tambah.datawilayah

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.devs.mdmanager.*
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.databinding.ActivityAddPolygonSubakBinding
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmum
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.io.IOException


class AddPolygonSubak : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener {
    private lateinit var mMap: GoogleMap
    private val polygonPoints = ArrayList<LatLng>()
    private val markers = mutableListOf<Marker>()
    private lateinit var binding: ActivityAddPolygonSubakBinding
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private var polygon: Polygon? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPolygonSubakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Area Lokasi Subak"

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.polygonSubak) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this@AddPolygonSubak)
        fetchLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        mMap.setOnMapClickListener { latLng ->
            polygonPoints.add(latLng)
            val marker = mMap.addMarker(MarkerOptions().position(latLng).draggable(true))
            markers.add(marker)
            drawPolygon()
        }
        mMap.setOnMarkerDragListener(this)

        binding.btnSimpan.setOnClickListener {
            intent.putExtra("polygon", polygonPoints)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onMarkerDragStart(marker: Marker) {
        // do nothing
    }

    override fun onMarkerDrag(marker: Marker) {
        // do nothing
    }

    override fun onMarkerDragEnd(marker: Marker) {
        val index = markers.indexOf(marker)
        polygonPoints[index] = marker.position
        drawPolygon()
    }

    private fun drawPolygon() {
        if (polygonPoints.size < 3) return

        polygon?.remove()

        val polygonOptions = PolygonOptions().addAll(polygonPoints)

            .strokeWidth(5f)
            .strokeColor(Color.RED)
            .fillColor(Color.argb(128, 255, 0, 0))
        polygon = mMap.addPolygon(polygonOptions)
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
                    (supportFragmentManager.findFragmentById(R.id.polygonSubak) as
                            SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@AddPolygonSubak)
            }
        }
    }

    fun searchLocation(view: View) {
        mMap.clear()
        val location: String = binding.searchLokasi.text.toString()
        var addressList: List<Address>? = null

        if (location == "") {
            Toast.makeText(
                applicationContext,
                "Masukkan lokasi yang ingin dicari!",
                Toast.LENGTH_SHORT
            ).show()
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
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
                Toast.makeText(
                    applicationContext,
                    "Lokasi ditemukan!",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(applicationContext, "Lokasi tidak ditemukan!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}