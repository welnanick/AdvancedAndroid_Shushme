package com.example.android.shushme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GeofenceBroadcastReciever extends BroadcastReceiver {

    public static final String TAG = GeofenceBroadcastReciever.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i(TAG, "onReceve called");

    }

}
