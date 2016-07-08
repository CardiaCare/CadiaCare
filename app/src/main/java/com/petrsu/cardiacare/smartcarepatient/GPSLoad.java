package com.petrsu.cardiacare.smartcarepatient;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
        Log.i(MainActivity.TAG,"ПреЭкзекуция");
    }

    @Override
    public Void doInBackground(Void... params) {
        Log.i(MainActivity.TAG,"ПотокЭкзекуция");
        if(MainActivity.gps.canGetLocation() != false) {
            Log.i(MainActivity.TAG,"ПотокИФЭкзекуция");
            double latitude = MainActivity.gps.getLatitude();
            double longitude = MainActivity.gps.getLongitude();
            if (MainActivity.isNetworkAvailable(context)) {
                MainActivity.smart.sendLocation(MainActivity.nodeDescriptor, MainActivity.patientUri, MainActivity.locationUri, Double.toString(latitude), Double.toString(longitude));
            }
        } else {
//            MainActivity.gps.showSettingsAlert();
            MainActivity.gpsflag = 0;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        Log.i(MainActivity.TAG,"ПостЭкзекуция");
        if (MainActivity.gpsflag == 0) {
            MainActivity.gps.showSettingsAlert();
        }
    }
}