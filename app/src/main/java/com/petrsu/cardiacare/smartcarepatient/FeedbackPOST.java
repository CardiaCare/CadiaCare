package com.petrsu.cardiacare.smartcarepatient;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/* Отправка ответов на сервер */

public class FeedbackPOST extends AsyncTask<Void, Integer, Void> {

    Context context;
    HttpURLConnection urlConnection = null;

    public FeedbackPOST(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public Void doInBackground(Void... params) {
        try {
            Gson json = new Gson();
            String jsonFeedback = json.toJson(MainActivity.feedback);
            jsonFeedback = "{user_id:" + "123456" + ", date:" + "123456" + ", " + jsonFeedback + "}";
            Log.i(MainActivity.TAG, "jsonFeedback = " + jsonFeedback);

            URL url = new URL("http://api.cardiacare.ru/index.php?r=feedback/create");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            Uri.Builder builder = new Uri.Builder()
                    //.appendQueryParameter("user_id", "123456")
                    //.appendQueryParameter("date", "123456")
                    .appendQueryParameter("feedback", jsonFeedback);
            String query = builder.build().getEncodedQuery();
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            Log.i(MainActivity.TAG, "jsonFeedback отправлен");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }
}