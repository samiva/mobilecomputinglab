package com.example.mobilecomputing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val geoFencingEvent = GeofencingEvent.fromIntent(intent)
        val geofencingTransition=geoFencingEvent.geofenceTransition

        if(geofencingTransition==Geofence.GEOFENCE_TRANSITION_ENTER
            || geofencingTransition==Geofence.GEOFENCE_TRANSITION_DWELL){
            var uid=intent!!.getIntExtra("uid", 0)
            val text=intent.getStringExtra("message")

            MainActivity.showNotification(context!!,text)
        }
    }

}