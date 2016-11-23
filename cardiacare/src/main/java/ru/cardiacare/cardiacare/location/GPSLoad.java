package ru.cardiacare.cardiacare.location;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import ru.cardiacare.cardiacare.MainActivity;

/* Отправка геоданных */

public class GPSLoad extends AsyncTask<Void, Integer, Void> {

    private Context context;

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
        if (MainActivity.gps.canGetLocation()) {
            double latitude = MainActivity.gps.getLatitude();
            double longitude = MainActivity.gps.getLongitude();
            if (MainActivity.isNetworkAvailable(context)) {
                Log.i("SS", "" + Double.toString(latitude) + " " + Double.toString(longitude));
                    /**
                     * send location
                     **/
            }
        } else {
            MainActivity.gpsEnabledFlag = 0;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if ((MainActivity.gpsEnabledFlag == 0) && (!MainActivity.alarmButtonFlag)) {
            MainActivity.gps.showSettingsAlert();
        }
    }
}