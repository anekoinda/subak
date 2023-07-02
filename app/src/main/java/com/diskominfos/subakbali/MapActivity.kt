package com.diskominfos.subakbali

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.diskominfos.subakbali.databinding.ActivityAddDataUmumBinding
import com.diskominfos.subakbali.databinding.ActivityMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        val sydney = LatLng(-8.613729, 115.214033)
        map.addMarker(
            MarkerOptions()
                .position(sydney)
                .title("Marker in Subak Sembung")
        )
        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}