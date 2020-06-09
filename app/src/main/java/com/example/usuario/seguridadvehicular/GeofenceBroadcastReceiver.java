package com.example.usuario.seguridadvehicular;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "GeofenceBroadcastReceiv";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //Toast.makeText(context, "Geofence triggered...", Toast.LENGTH_SHORT).show();

        NotificationHelper notificationHelper = new NotificationHelper(context);

        GeofencingEvent geofencingEvent =  GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()){
            Log.d(TAG, "onReceive: Error receiving event...");
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();

        for (Geofence geofence: geofenceList){
            Log.d(TAG, "onReceive: "+geofence.getRequestId());
        }

        //Location location = geofencingEvent.getTriggeringLocation();
        int transitionType = geofencingEvent.getGeofenceTransition();

        switch (transitionType){
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Toast.makeText(context, "GEOFENCE_TRANSITION_ENTER", Toast.LENGTH_SHORT).show();
                notificationHelper.sendHighPriorityNotification("SSV",String.format("Tu estas dentro de tu geocerca, pulsa para verificar ubicacion de tu vehiculo."), MapsActivity.class);
                break;
                case Geofence.GEOFENCE_TRANSITION_DWELL:
                    Toast.makeText(context, "GEOFENCE_TRANSITION_DWELL", Toast.LENGTH_SHORT).show();
                    notificationHelper.sendHighPriorityNotification("SSV",String.format("Tu estas transitando dentro de tu geocerca."), MapsActivity.class);
                    break;
                    case Geofence.GEOFENCE_TRANSITION_EXIT:
                        Toast.makeText(context, "GEOFENCE_TRANSITION_EXIT", Toast.LENGTH_SHORT).show();
                        notificationHelper.sendHighPriorityNotification("SSV",String.format("Saliste de tu geocerca, pulsa para verificar ubicacion de tu vehiculo."),MapsActivity.class);
                        break;
        }

    }
}
