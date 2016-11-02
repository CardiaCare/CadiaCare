package ru.cardiacare.cardiacare.servey;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.servey.Feedback;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.PrintWriter;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.*;
import java.net.MalformedURLException;

import ru.cardiacare.cardiacare.MainActivity;

/* Отправка ответов на сервер */

public class FeedbackPOST extends AsyncTask<Void, Integer, Integer> {

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
    public Integer doInBackground(Void... params) {
       /* try {
            Gson json = new Gson();
            String jsonFeedback = json.toJson(MainActivity.feedback);
            jsonFeedback = "{\"user_id\":" + "123456" + ", \"date\":" + "123456"+"}";
            /*Log.i(MainActivity.TAG, "jsonFeedback = " + jsonFeedback);

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
            System.out.println("jsonFeedback отправлен");
            //////////////////////////////////////////////////////////////////////////////////////////
            HttpURLConnection urlConnection = null;
            // create connection
            URL urlToRequest = new URL("http://karelia.xyz/test.php");
            urlConnection = (HttpURLConnection) urlToRequest.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setFixedLengthStreamingMode(
                    jsonFeedback.getBytes().length);
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            //send the POST out
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(jsonFeedback);
            out.close();
            //////////////////////////////////////////////////////////////////////////////////////////

        } catch (Exception e) {
            e.printStackTrace();
        }*/
/////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
            Gson json = new Gson();
            String jsonFeedback = json.toJson(MainActivity.feedback);
            //jsonFeedback = "{\"user_id\":" + "123456" + ", \"date\":" + "123456"+jsonFeedback+"}";

            /////////////сравнение старого///////////
            String jsonFeedbackOld = readSavedData();
            String newfb = jsonFeedback.substring(jsonFeedback.indexOf("personUri"), jsonFeedback.length());
            String oldfb = jsonFeedback.substring(jsonFeedback.indexOf("personUri"), jsonFeedback.length());
            if(newfb.equals(oldfb)){
                return 0;
            }
            writeData(jsonFeedback);
            ////////////////////////////////////////

            String myURL = "http://api.cardiacare.ru/index.php?r=feedback/create";
            String parammetrs = "user_id="+"123456"+"&date="+"123456"+"&feedback="+jsonFeedback;
            byte[] data = null;
            InputStream is = null;

            try {
                URL url = new URL(myURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                conn.setRequestProperty("Content-Length", "" + Integer.toString(parammetrs.getBytes().length));
                OutputStream os = conn.getOutputStream();
                data = parammetrs.getBytes("UTF-8");
                os.write(data);
                data = null;

                conn.connect();
                int responseCode= conn.getResponseCode();

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                if (responseCode == 200) {
                    is = conn.getInputStream();

                    byte[] buffer = new byte[8192]; // Такого вот размера буфер
                    // Далее, например, вот так читаем ответ
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                    }
                    //data = baos.toByteArray();
                    //resultString = new String(data, "UTF-8");
                } else {
                }



            } catch (MalformedURLException e) {

                //resultString = "MalformedURLException:" + e.getMessage();
            } catch (IOException e) {

                //resultString = "IOException:" + e.getMessage();
            } catch (Exception e) {

                //resultString = "Exception:" + e.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
/////////////////////////////////////////////////////////////////////////////////////////////////////
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (result == -1) {

        } else {

        }
    }

    public String readSavedData(){
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream fIn = context.openFileInput("feedbackold.json");
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader buffreader = new BufferedReader(isr);

            String readString = buffreader.readLine();
            while ( readString != null ) {
                datax.append(readString);
                readString = buffreader.readLine();
            }
            isr.close();
        } catch ( IOException ioe) {
            ioe.printStackTrace();
        }
        return datax.toString();
    }

    public void writeData ( String data ) {
        try {
            //FileOutputStream fOut = openFileOutput (filename , MODE_PRIVATE );
            FileOutputStream fOut = context.openFileOutput("feedbackold.json", context.MODE_PRIVATE );
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}