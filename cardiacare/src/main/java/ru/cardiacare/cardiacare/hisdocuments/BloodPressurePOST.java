package ru.cardiacare.cardiacare.hisdocuments;

import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import ru.cardiacare.cardiacare.MainActivity;

/* Отправка данных из дневника давления на сервер */

public class BloodPressurePOST extends AsyncTask<JSONObject, String, String> {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject... params) {

        try {
            OkHttpClient client = new OkHttpClient();

            String json = params[0].toString();
            System.out.println("Test! json " + json);
            // String json = "{ \"email\":" + CreateAccountActivity.etLogin.getText().toString();

            RequestBody body = RequestBody.create(JSON, json);

            String credential = Credentials.basic(MainActivity.storage.getAccountToken(), "");

            System.out.println("Test! body " + body.toString());

            Request request = new Request.Builder()
                    .url("http://api.cardiacare.ru/patients/" + MainActivity.storage.getAccountId() + "/bloodpressure")
                    .addHeader("Authorization", credential)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            //   Response response = client.newCall(request).execute();
            //   if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println("Test! request " + request.body().toString());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("Request", request.body().toString());
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
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        BloodPressureGET bloodGet = new BloodPressureGET();
        bloodGet.execute();
        BloodPressureActivity.refresh();
    }
}
