package com.diskominfos.subakbali.ui.tambah.datawilayah

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.ActionMode
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.devs.mdmanager.*
import com.diskominfos.subakbali.R
import com.diskominfos.subakbali.databinding.ActivityAddPolygonSubakBinding
import com.diskominfos.subakbali.ui.tambah.dataumum.AddDataUmum
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException


class AddPolygonSubak : AppCompatActivity(), View.OnClickListener, OnShapeRemoveListener,
    OnShapeDrawListener {

    companion object {
        private val TAG = AddPolygonSubak::class.java.simpleName
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAddPolygonSubakBinding
    private var parentView: View? = null
    private var actionMode: ActionMode? = null
    private var mapDrawingManager: MapDrawingManager? = null
//    private var polygons = []


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPolygonSubakBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Area Lokasi Subak"

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.polygonSubak) as SupportMapFragment
        mapFragment.getMapAsync { gm ->
            mMap = gm
            mMap.getUiSettings().isMapToolbarEnabled = false
            mMap.getUiSettings().isMyLocationButtonEnabled = true
            //googleMap?.getUiSettings()?.isZoomControlsEnabled = true
            //googleMap?.setMapType(GoogleMap.MAP_TYPE_SATELLITE)
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(-8.613508437334934, 115.21408604140129), 16f)
            )

            this.let {
                mapDrawingManager = MDMBuilder(it.baseContext).withMap(gm).build()
                mapDrawingManager?.removeListener = this
                mapDrawingManager?.drawListener = this
//                onClick(iv_polygon)
            }
        }

        mapDrawingManager?.shapeType = ShapeType.POLYGON
        //mapDrawingManager?.strokColor = AnimatorPolygon.POLY_STROKE_COLOR
        //mapDrawingManager?.fillColor = AnimatorPolygon.POLY_FILL_COLOR
        updateShapeSizeTotal(ShapeType.POLYGON)
    }

    fun updateShapeSizeTotal(shapeType: ShapeType) {
        var total = 0f

        when (shapeType) {
            ShapeType.POLYGON ->
                mapDrawingManager?.polygonList?.let {
                    it.forEach {
                        val titleParts = it.second.title.toString().split(" ")
                        total += titleParts[0].toFloat()
                    }
//                    tv_size.text= String.format("%.2f sq. feet", total)
                }
            ShapeType.POLYLINE ->
                mapDrawingManager?.polylineList?.let {
                    it.forEach {
                        val titleParts = it.second.title.toString().split(" ")
                        total += titleParts[0].toFloat()
                    }
//                    tv_size.text= String.format("%.2f feet", total)
                }

            ShapeType.CIRCLE ->
                mapDrawingManager?.circleList?.let {
                    it.forEach {
                        val titleParts = it.second.title.toString().split(" ")
                        total += titleParts[0].toFloat()
                    }
//                    tv_size.text= String.format("%.2f sq. feet", total)
                }

            ShapeType.POINT ->
                mapDrawingManager?.markerList?.let {
                    it.size

//                    tv_size.text= String.format("%d units", it.size)
                }
        }
    }

    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

    override fun onShapeCompleted(shapeType: ShapeType, shapeId: String) {
        updateShapeSizeTotal(shapeType)

    }

    override fun onShapeUpdated(shapeType: ShapeType, shapeId: String) {
        TODO("Not yet implemented")
    }

    override fun onAllShapeRemove() {
        TODO("Not yet implemented")
    }

    override fun onShapeRemoveAfter(deleted: Boolean) {
        TODO("Not yet implemented")
    }

    override fun onShapeRemoveBefore(shapeType: ShapeType, shapeIndex: Int, shapeCount: Int) {
        TODO("Not yet implemented")
    }

    override fun onShapeRemoveModeEnabled(removeModeEnable: Boolean) {
        TODO("Not yet implemented")
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