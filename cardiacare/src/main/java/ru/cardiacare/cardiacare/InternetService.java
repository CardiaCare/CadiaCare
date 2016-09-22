package ru.cardiacare.cardiacare;


import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;


/**
 * Created by Yulia on 20.04.2015.
 */
public class InternetService extends AsyncTask<JSONObject, JSONObject, JSONObject> { //JSONObject

    final String LOG_TAG = "myLogs InternetService";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("LOG_TAG", "in onPreExecute()");
        // Showing progress dialog
    }

    @Override
    protected JSONObject doInBackground(JSONObject... data) {
        Log.d("LOG_TAG", "in doInBackground()");
        JSONObject json = data[0];
        Log.d("LOG_TAG", "json" + json.toString());
        String url = "http://yzavyalo.cardiacare.ru/web-server/index.php/user/auth";
       /*HttpClient client = new DefaultHttpClient();
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

       //Log.d("LOG_TAG", jsonResponse.toString());*/

        try{
            URL object=new URL(url);

        HttpURLConnection con = (HttpURLConnection) object.openConnection();

        con.setDoOutput(true);

        con.setDoInput(true);

        con.setRequestProperty("Content-Type", "application/json");

        con.setRequestProperty("Accept", "application/json");

        con.setRequestMethod("POST");

        OutputStreamWriter wr= new OutputStreamWriter(con.getOutputStream());

        wr.write(json.toString());

        wr.flush();

//display what returns the POST request

        StringBuilder sb = new StringBuilder();

        int HttpResult =con.getResponseCode();

        if(HttpResult ==HttpURLConnection.HTTP_OK){

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));

            String line = null;

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            br.close();

            System.out.println(""+sb.toString());

        }else{
            System.out.println(con.getResponseMessage());
        }
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
    protected void onPostExecute(JSONObject result) { //JSONObject jsonData
        //super.onPostExecute(jsonData);
        //Log.d("LOG_TAG", result.toString());
    }
}
