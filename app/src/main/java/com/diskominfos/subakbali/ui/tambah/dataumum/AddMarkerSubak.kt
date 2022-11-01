package com.diskominfos.subakbali.ui.tambah.dataumum

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.databinding.ActivityAddDataUmumBinding
import com.diskominfos.subakbali.databinding.ActivityAddMarkerSubakBinding
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddMarkerSubakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Koordinat Lokasi Subak"

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapSubak) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
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
                val intent = Intent(this, AddDataUmum::class.java)
                intent.putExtra("lat", lat)
                intent.putExtra("long", long)
                startActivity(intent)
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
                val intent = Intent(this, AddDataUmum::class.java)
                intent.putExtra("lat", lat)
                intent.putExtra("long", long)
                startActivity(intent)
            }
        }

    }
}