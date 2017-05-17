package ru.cardiacare.cardiacare.hisdocuments;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import ru.cardiacare.cardiacare.MainActivity;

/* Запрос на получение списка врачей */

public class DoctorGET extends AsyncTask<JSONObject, String, String> {

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
                    .url("http://api.cardiacare.ru/patients/" + MainActivity.storage.getAccountId() + "/doctors")
                    .addHeader("Authorization", credential)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            switch (ret = response.body().string()) {
            }
            System.out.println("Test! response " + ret);
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
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

        return ret;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

//        result = result.substring(0, result.length()-1);
//        result = result + ", {\"email\":\"email\", \"name\":\"name\", \"patronymic\":\"patronymic\", \"surname\":\"surname\"}]";

//        System.out.println("Test! result " + result);
//        try {
//            result = result.substring(1, result.length()-1);
//            System.out.println("Test! result " + result);
//            JSONObject dataJsonObj = null;
//            dataJsonObj = new JSONObject(result);
//            //result = dataJsonObj.getString("token");
//
//            MainActivity.storage.setDoctorEmail(dataJsonObj.getString("email"));
//            MainActivity.storage.setDoctorName(dataJsonObj.getString("name"));
//            MainActivity.storage.setDoctorPatronymic(dataJsonObj.getString("patronymic"));
//            MainActivity.storage.setDoctorSurname(dataJsonObj.getString("surname"));
//
//        } catch (Exception e) {}

        MainActivity.storage.setDoctors(result);

        JSONArray jArray = null;
        try {
            jArray = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jArray.length(); i++) {
            try {
                JSONObject oneObject = jArray.getJSONObject(i);

                MainActivity.storage.setDoctorEmail(oneObject.getString("email"));
                MainActivity.storage.setDoctorName(oneObject.getString("name"));
                MainActivity.storage.setDoctorPatronymic(oneObject.getString("patronymic"));
                MainActivity.storage.setDoctorSurname(oneObject.getString("surname"));

            } catch (JSONException e) {
            }
        }
    }
}