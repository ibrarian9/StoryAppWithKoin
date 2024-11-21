package com.app.intermediatesubmission.ui.mapStory

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.intermediatesubmission.R
import com.app.intermediatesubmission.databinding.ActivityMapsBinding
import com.app.intermediatesubmission.di.Injection.messageToast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var bind: ActivityMapsBinding
    private val boundsBuilder = LatLngBounds.Builder()
    private val mapsViewModel: MapsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(bind.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()

        // Set Location from Api
        mapsViewModel.locationStory.observe(this@MapsActivity) {
            it.forEach { place ->
                val latLng = LatLng(place.lat!!, place.lon)
                mMap.addMarker(MarkerOptions().position(latLng).title(place.name))
                boundsBuilder.include(latLng)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this@MapsActivity, R.raw.map_style))
            if (!success) {
                messageToast(this@MapsActivity, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            messageToast(this@MapsActivity,"Can't find style. Error: $e")
        }
    }
}