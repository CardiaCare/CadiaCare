package ru.cardiacare.cardiacare;

import android.os.AsyncTask;
import android.util.Base64;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;

/* Удаление токена на сервере */

public class DeleteToken extends AsyncTask<JSONObject, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject... params) {

        try {
            URL object = new URL("http://api.cardiacare.ru/tokens");
            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setConnectTimeout(5000);
            con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            con.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((MainActivity.authorization_token + ":").getBytes("UTF-8"), Base64.DEFAULT));
            con.setRequestMethod("DELETE");
            con.connect();
            //MainActivity.storage.setAccountPreferences("", "", "", "", "", "", "", "", "", "", "", "", "", "0", "", "");
            MainActivity.authorization_token = "";
            System.out.println("Test! DELETE " + con.getResponseCode());
        } catch (Exception e) {
            System.out.println("Test! exc " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
