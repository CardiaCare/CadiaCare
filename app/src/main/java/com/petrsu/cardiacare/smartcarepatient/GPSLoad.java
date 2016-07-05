package com.petrsu.cardiacare.smartcarepatient;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by Kiribaz on 05.07.16.
 */

public class GPSLoad extends AsyncTask<Void, Integer, Void> {
    Context context;
    public GPSLoad(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.gps = new com.petrsu.cardiacare.smartcarepatient.LocationService(context);
    }

    @Override
    public Void doInBackground(Void... params) {
        if(MainActivity.gps.canGetLocation() != false) {
            double latitude = MainActivity.gps.getLatitude();
            double longitude = MainActivity.gps.getLongitude();
            MainActivity.smart.sendLocation(MainActivity.nodeDescriptor, MainActivity.patientUri, MainActivity.locationUri, Double.toString(latitude), Double.toString(longitude));
        }else{
            MainActivity.gps.showSettingsAlert();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}