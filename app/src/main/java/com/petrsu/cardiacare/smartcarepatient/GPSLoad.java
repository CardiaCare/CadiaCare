package com.petrsu.cardiacare.smartcarepatient;

import android.content.Context;
import android.os.AsyncTask;

import com.petrsu.cardiacare.smartcare.SmartCareLibrary;

/* Отправка геоданных */

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
        if(MainActivity.gps.canGetLocation()) {
            double latitude = MainActivity.gps.getLatitude();
            double longitude = MainActivity.gps.getLongitude();
            if (MainActivity.isNetworkAvailable(context)) {
                SmartCareLibrary.sendLocation(MainActivity.nodeDescriptor, MainActivity.patientUri, MainActivity.locationUri, Double.toString(latitude), Double.toString(longitude));
            }
        } else {
            MainActivity.gpsEnabledFlag = 0;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (MainActivity.gpsEnabledFlag == 0) {
            MainActivity.gps.showSettingsAlert();
        }
    }
}