package ru.cardiacare.cardiacare;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
        MainActivity.gps = new LocationService(context);
    }

    @Override
    public Void doInBackground(Void... params) {
        if(MainActivity.gps.canGetLocation()) {
            double latitude = MainActivity.gps.getLatitude();
            double longitude = MainActivity.gps.getLongitude();
            if (MainActivity.isNetworkAvailable(context)) {
                Log.i("SS", ""+ Double.toString(latitude)+" "+ Double.toString(longitude));
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