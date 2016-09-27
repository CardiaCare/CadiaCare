package ru.cardiacare.cardiacare;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Yulia on 22.04.2015.
 */
public class InternetEcgFileService  extends AsyncTask<String, Void, Void> { //JSONObject

    final String LOG_TAG = "myLogs InternetService";
    JSONObject json;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("LOG_TAG", "in onPreExecute()");
        // Showing progress dialog
    }

    @Override
    protected Void doInBackground(String... data) {
        Log.d("LOG_TAG", "in doInBackground()");
        //JSONObject json = data[0];
        json = new JSONObject();
        Log.d("LOG_TAG", "json" + json.toString());
        String url = "http://yzavyalo.cardiacare.ru/web-server/index.php/ecg/demo";
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/mnt/sdcard/demo.txt");

        Log.d("msg", file.toString());
       HttpClient client = new DefaultHttpClient();
        HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);

        JSONObject jsonResponse = null;
        HttpPost post = new HttpPost(url);
        try {
            StringEntity se = new StringEntity("json="+json.toString());
            post.addHeader("content-type", "application/x-www-form-urlencoded");
            post.setEntity(se);

            HttpResponse response;
            response = client.execute(post);
          //  String resFromServer = org.apache.http.util.EntityUtils.toString(response.getEntity());

            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String resFromServer = reader.readLine();

            //jsonResponse = new JSONObject(resFromServer);
            Log.i("Response from server", resFromServer);
        } catch (Exception e) { e.printStackTrace();}

       //Log.d("LOG_TAG", jsonResponse.toString());

        try{
            URL object=new URL(url);

            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httppost = new HttpPost(url);

            InputStreamEntity reqEntity = new InputStreamEntity(
                    new FileInputStream(file), -1);
            reqEntity.setContentType("binary/octet-stream");
            reqEntity.setChunked(true); // Send in multiple parts if needed
            httppost.setEntity(reqEntity);
            HttpResponse response = httpclient.execute(httppost);
            Log.d("msg", response.toString());

        }catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "--- JSONParser, makeRequest() --- " + e.getMessage() + " Exception Type: " + e.getClass().getName());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(LOG_TAG, "--- JSONParser, makeRequest() --- " + e.getMessage() + " Exception Type: " + e.getClass().getName());
        }



        return null;
    }

    @Override
    protected void onPostExecute(Void result) { //JSONObject jsonData
        //super.onPostExecute(jsonData);
        //Log.d("LOG_TAG", result.toString());
    }
}

