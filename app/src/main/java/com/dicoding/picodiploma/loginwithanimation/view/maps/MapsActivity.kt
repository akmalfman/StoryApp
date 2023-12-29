package com.dicoding.picodiploma.loginwithanimation.view.maps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.dicoding.picodiploma.loginwithanimation.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val mapsViewModel by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapsViewModel.getSession().observe(this) { user ->
            if (!user.isLogin) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            }else {
                mapsViewModel.getStoriesWithLocation(user.token)
            }
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        addManyMarker()
    }

    private fun addManyMarker() {
        mapsViewModel.listStoryLocation.observe(this@MapsActivity) {
            it?.listStory?.forEach { data ->
                val latLng = LatLng(data.lat, data.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(data.name)
                        .snippet(data.description)
                )
            }
        }
    }
}