package com.example.mobilecomputing

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_map.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import java.lang.Exception
import java.util.*

class MapActivity : AppCompatActivity(), OnMapReadyCallback {

    lateinit var gMap: GoogleMap
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var selectedLocation: LatLng

    val GEOFENCEID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        (map_fragment as SupportMapFragment).getMapAsync(this)
        // TODO: map stuff
        map_create.setOnClickListener {
            val reminderText = reminder_message.text.toString()
            if (reminderText.isEmpty()) {
                toast("Please provide reminder message")
                return@setOnClickListener
            }
            if (selectedLocation == null) {
                toast("Please select a location on map")
                return@setOnClickListener
            }

            val reminder = Reminder(
                uid = null,
                time = null,
                location = String.format(
                    "%.3f,%.3f",
                    selectedLocation.latitude,
                    selectedLocation.longitude
                ),
                message = reminderText
            )

            doAsync {
                val db =
                    Room.databaseBuilder(applicationContext, AppDatabase::class.java, "reminders")
                        .build()
                db.reminderDao().insert(reminder)
                db.close()

            }
            finish()
        }

    }

    private fun CreateGeoFence(selectedLocation:LatLng, reminder:Reminder, geofencingClient: GeofencingClient) {
        val GEOFENCE_RADIUS = 500.0f
        val geofence = Geofence.Builder().setRequestId(GEOFENCEID).setCircularRegion(selectedLocation.latitude,selectedLocation.longitude, GEOFENCE_RADIUS)
            .setExpirationDuration(GEONFENCE_EXPIRATION.toLong()).setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL)
            .
            )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED ||
                grantResults[1] == PackageManager.PERMISSION_DENIED
            ) {
                toast("Needs all the permissions")
            }

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                if(grantResults[2] == PackageManager.PERMISSION_DENIED)
                toast("Needs all the permissions")
            }
        }
    }

    override fun onMapReady(p0: GoogleMap?) {

        gMap = p0 ?: return
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            gMap.isMyLocationEnabled = true;

            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    var latLong = LatLng(location.latitude, location.longitude)
                    with(gMap) {
                        animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 13f))
                    }
                } else {
                    var permission = mutableListOf<String>()
                    permission.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    permission.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        permission.add(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    }
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION
                        ),
                        123
                    )
                }

                gMap.setOnMapClickListener { location: LatLng ->
                    with(gMap) {
                        clear()
                        animateCamera(CameraUpdateFactory.newLatLngZoom(location, 13f))
                        val geocoder = Geocoder(applicationContext, Locale.getDefault())
                        var city: String = ""
                        var title: String = ""
                        try {
                            val addressList =
                                geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            city = addressList.get(0).locality
                            title = addressList.get(0).getAddressLine(0)
                        } catch (e: Exception) {

                        }
                        val marker =
                            addMarker(MarkerOptions().position(location).snippet(title).title(city))
                        marker.showInfoWindow()
                        selectedLocation = location
                    }
                }
            }
        }
    }
}
