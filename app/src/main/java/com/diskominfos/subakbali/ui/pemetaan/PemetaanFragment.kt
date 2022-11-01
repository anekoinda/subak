package com.diskominfos.subakbali.ui.pemetaan

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.ui.auth.LoginActivity
import com.diskominfos.subakbali.databinding.FragmentPemetaanBinding
import com.diskominfos.subakbali.model.UserPreference
import com.diskominfos.subakbali.model.ViewModelFactory
import com.diskominfos.subakbali.ui.data.DataViewModel
import com.diskominfos.subakbali.ui.profil.ProfilViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PemetaanFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnPolygonClickListener {
    private lateinit var mMap: GoogleMap
    private var _binding: FragmentPemetaanBinding? = null
    private lateinit var dataViewModel: DataViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val pemetaanViewModel =
            ViewModelProvider(this).get(PemetaanViewModel::class.java)

        _binding = FragmentPemetaanBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val profilViewModel: ProfilViewModel by viewModels {
            val pref = requireContext().dataStore
            ViewModelFactory(
                UserPreference.getInstance(pref)
            )
        }

        dataViewModel = ViewModelProvider(this)[DataViewModel::class.java]
        profilViewModel.getUser().observe(this) {
            if (it != "") {
                dataViewModel.getSubak(it)
                dataViewModel.subakList.observe(this) { subak ->
                    subak.forEach { it ->
                        if (it.lat != null && it.lng != null) {
                            val coordinate = LatLng(it.lat, it.lng)
                            googleMap.addMarker(
                                MarkerOptions().position(coordinate).title(it.nama)
                                    .snippet(it.jenis_subak)
                            )
                        }
                    }
                }
            } else {
                val intent = Intent(activity, LoginActivity::class.java)
                activity?.startActivity(intent)
            }
        }

//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-8.613729, 115.214033)
//        mMap.addMarker(
//            MarkerOptions()
//                .position(sydney)
//                .title("Marker in Subak Sembung")
//        )
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))

        // Add polylines to the map.
        // Polylines are useful to show a route or some other connection between points.
        val polygon = googleMap.addPolygon(
            PolygonOptions()
                .clickable(true)
                .add(
                    LatLng(-8.613729, 115.214033),
                    LatLng(-8.613587, 115.215720),
                    LatLng(-8.612740, 115.215283),
                    LatLng(-8.612740, 115.215283),
                    LatLng(-8.612998, 115.213985),
                    LatLng(-8.613729, 115.214033)
                )
                .strokeColor(Color.GREEN)
                .fillColor(0x7F00FF00)
        )

        // Position the map's camera near Alice Springs in the center of Australia,
        // and set the zoom factor so most of Australia shows on the screen.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-8.613729, 115.214033), 17f))

        // Set listeners for click events.
//        googleMap.setOnPolylineClickListener(this)
        googleMap.setOnPolygonClickListener(this)
    }

    override fun onPolygonClick(googleMap: Polygon) {
        TODO("Not yet implemented")
    }
}