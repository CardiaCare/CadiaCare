package ru.cardiacare.cardiacare.user;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import ru.cardiacare.cardiacare.MainActivity;

public class PatientsGET  extends AsyncTask<JSONObject, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject... params) {

        String ret = "";
        try {
            OkHttpClient client = new OkHttpClient();

            String credential = Credentials.basic(MainActivity.storage.getAccountToken(), "");

            Request request = new Request.Builder()
                    .url("http://api.cardiacare.ru/patients/" + MainActivity.storage.getAccountId())
                    .addHeader("Authorization", credential)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            //System.out.println("Test! response " + response.body().string());
            switch (ret = response.body().string()) {
            }
            System.out.println("Test! response " + ret);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    //Log.e("Request", request.body().toString());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    Log.i("Response", response.body().string());
                }
            });

            System.out.println("Test! POST");
        } catch (Exception e) {
            System.out.println("Test! exc " + e.getMessage());
            e.printStackTrace();
        }
        //return null;
        return ret;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        System.out.println("Test! result " + result);
        try {
            System.out.println("Test! result " + result);
            JSONObject dataJsonObj = null;
            dataJsonObj = new JSONObject(result);

            MainActivity.storage.setAccountFirstName(dataJsonObj.getString("name"));
            MainActivity.storage.setAccountSecondName(dataJsonObj.getString("surname"));

        } catch (Exception e) {}
    }
}
