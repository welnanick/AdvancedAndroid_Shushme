package com.example.android.shushme;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.GeofencingRequest.Builder;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Geofencing implements ResultCallback {

    private Context context;
    private GoogleApiClient client;
    private PendingIntent geofencePendingIntent;
    private List<Geofence> geofenceList;
    public static final String TAG = Geofencing.class.getSimpleName();

    public Geofencing(Context context, GoogleApiClient client) {

        this.context = context;
        this.client = client;

        geofencePendingIntent = null;
        geofenceList = new ArrayList<>();

    }

    public void registerAllGeofences() {

        if (client == null || !client.isConnected() || geofenceList == null ||
                geofenceList.size() == 0) {

            return;

        }
        try {

            LocationServices.GeofencingApi
                    .addGeofences(client, getGeofencingRequest(), getGeofencePendingIntent())
                    .setResultCallback(this);

        }
        catch (SecurityException e) {

            Log.e(TAG, e.getMessage());

        }

    }

    public void unregisterAllGeofences() {

        if (client == null || !client.isConnected()) {

            return;

        }
        try {

            LocationServices.GeofencingApi.removeGeofences(client, getGeofencePendingIntent())
                                          .setResultCallback(this);

        }
        catch (SecurityException e) {

            Log.e(TAG, e.getMessage());

        }

    }

    public void updateGeofencesList(PlaceBuffer places) {

        geofenceList = new ArrayList<>();
        if (places == null || places.getCount() == 0) {

            return;

        }
        for (Place place : places) {

            String placeUID = place.getId();
            double placeLat = place.getLatLng().latitude;
            double placeLng = place.getLatLng().longitude;

            Geofence geofence = new Geofence.Builder().setRequestId(placeUID)
                                                      .setCircularRegion(placeLat, placeLng, 50.0f)
                                                      .setTransitionTypes(
                                                              Geofence.GEOFENCE_TRANSITION_ENTER |
                                                                      Geofence.GEOFENCE_TRANSITION_EXIT)
                                                      .setExpirationDuration(TimeUnit.DAYS
                                                              .convert(1, TimeUnit.MILLISECONDS))
                                                      .build();
            geofenceList.add(geofence);

        }

    }

    private GeofencingRequest getGeofencingRequest() {

        GeofencingRequest.Builder builder = new Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();

    }

    private PendingIntent getGeofencePendingIntent() {

        if (geofencePendingIntent != null) {

            return geofencePendingIntent;

        }
        Intent intent = new Intent(context, GeofenceBroadcastReciever.class);
        geofencePendingIntent =
                PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return geofencePendingIntent;

    }

    @Override
    public void onResult(@NonNull Result result) {

        Log.e(TAG, String.format("Error adding/removing geofence : %s",
                result.getStatus().toString()));

    }
}
