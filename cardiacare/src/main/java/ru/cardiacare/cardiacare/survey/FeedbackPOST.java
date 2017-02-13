package ru.cardiacare.cardiacare.survey;

import android.content.Context;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import ru.cardiacare.cardiacare.MainActivity;

/* Отправка ответов на сервер */

public class FeedbackPOST extends AsyncTask<Void, Integer, Integer> {

    private Context context;
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

            try {


                CookieManager cookieManager = new CookieManager();
                cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

                OkHttpClient client_get = new OkHttpClient();

                String credential_get = Credentials.basic(MainActivity.storage.getAccountToken(),"");

                Request request_get = new Request.Builder()
                        .url("http://api.cardiacare.ru/patients/14/feedback")
                        //.post(body)
                        .header("Authorization", credential_get)
                        .build();

                client_get.setCookieHandler(cookieManager);

                client_get.newCall(request_get).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("response", request.body().toString());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        Log.e("response", response.body().string());
                    }
                });



                MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                Map<String, String> param;
                param = new HashMap<String, String>();
                param.put("questionnaire_id", "4");
                param.put("data", "4");
                param.put("version", "4");
                JSONObject parameter = new JSONObject(param);
                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, parameter.toString());

                String credential = Credentials.basic(MainActivity.storage.getAccountToken(),"");

                Request request = new Request.Builder()
                        .url("http://api.cardiacare.ru/patients/14/feedback")
                        //.post(body)
                        .header("Authorization", credential)
                        .addHeader("Content-Length", String.valueOf(parameter.length()))
                        .addHeader("Cookie", "PHPSESSID=a00eusn4981mj1n93ejd3h4o2pe2rfc0b99dbjodkbfucosb5bp1")
                        .post(body)
                        .build();

                client.setFollowRedirects(false|false);
                client.setCookieHandler(cookieManager);


                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {
                        Log.e("response", request.body().toString());
                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        Log.e("response", response.body().string());
                    }
                });



//                URL url = new URL("http://api.cardiacare.ru/patients/2/feedback");
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(10000);
//                conn.setConnectTimeout(15000);
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
//                conn.setUseCaches (false);
//                conn.setDoOutput(true);
//                conn.setDoInput(true);
//
//                conn.setRequestProperty("Authorization", String.format("Basic %s", Base64.encodeToString((MainActivity.storage.getAccountToken()+":").getBytes("UTF-8"), Base64.DEFAULT) ));
//
//                conn.setRequestProperty("Content-Type", "application/json");


//                Map<String, List<String>> hdrs = conn.getHeaderFields();
//                Set<String> hdrKeys = hdrs.keySet();
//                for (String k : hdrKeys)
//                    System.out.println("Key: " + k + "  Value: " + hdrs.get(k));

                // Send post request
//                conn.setDoOutput(true);
//                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
//                wr.write("{" +
//                        "  \"questionnaire_id\": 4,\n" +
//                        "  \"version\": \"0.1.1\",\n" +
//                        "  \"data\": \"new questionnaire\"\n" +
//                        "\n" +
//                        "}");
//                wr.flush();
//                wr.close();
//
//                int responseCode = conn.getResponseCode();
//
//                System.out.println("Response Code : " + responseCode);
//
//                BufferedReader in = new BufferedReader(
//                        new InputStreamReader(conn.getInputStream()));
//                String inputLine;
//                StringBuffer response = new StringBuffer();
//
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                in.close();
//
//
//                System.out.println(response.toString());
//
//                DataOutputStream wr = new DataOutputStream(conn.getOutputStream ());
//                wr.writeBytes("{" +
//                        "  \"questionnaire_id\": 4,\n" +
//                        "  \"version\": \"0.1.1\",\n" +
//                        "  \"data\": \"new questionnaire\"\n" +
//                        "\n" +
//                        "}");
//                wr.flush();
//                wr.close();
//
//                Log.i("RESP","response " + conn.getResponseCode());
//
//                int responseCode = conn.getResponseCode();
//
//                InputStream stream = conn.getInputStream();
//                int sz = stream.available();
//                byte[] b = new byte[sz];
//                stream.read(b);
//                stream.close();
//                String data = new String(b);
//
//                Log.i("RESP","response " + data);
//                if (responseCode == 200) {
//
//                }
            } catch (Exception e) {
            }

        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
    }

    private String readSavedData() {
        StringBuilder datax = new StringBuilder("");
        try {
            FileInputStream fIn;
            if (QuestionnaireHelper.questionnaireType.equals("periodic"))
                fIn = context.openFileInput("feedbackold.json");
            else fIn = context.openFileInput("alarmfeedbackold.json");
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader buffreader = new BufferedReader(isr);

            String readString = buffreader.readLine();
            while (readString != null) {
                datax.append(readString);
                readString = buffreader.readLine();
            }
            isr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return datax.toString();
    }

    private void writeData(String data) {
        try {
//            FileOutputStream fOut = openFileOutput (filename , MODE_PRIVATE );
            FileOutputStream fOut;
            if (QuestionnaireHelper.questionnaireType.equals("periodic"))
                fOut = context.openFileOutput("feedbackold.json", context.MODE_PRIVATE);
            else fOut = context.openFileOutput("alarmfeedbackold.json", context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}