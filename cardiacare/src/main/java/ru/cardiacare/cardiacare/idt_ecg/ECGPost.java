package ru.cardiacare.cardiacare.idt_ecg;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.cardiacare.cardiacare.MainActivity;

/* Отправка данных ЭКГ на сервер */

public class ECGPost extends AsyncTask<JSONObject, String, String> {

    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject... params) {

        try {
//            String dirPath = "/";
//            File fileq = MainActivity.mContext.getFilesDir();//new File(dirPath);
//            File[] files = fileq.listFiles();
//            for(int i = 0; i < files.length; i++)
//                System.out.println("Test! files " + files[i].toString());

            OkHttpClient client = new OkHttpClient();

            //File file = new File("feedback.json"); // MainActivity.storage.getECGFile() //questionnaire.json

//            File file = new File(MainActivity.mContext.getFilesDir(), MainActivity.storage.getECGFile());
            File file = new File(MainActivity.mContext.getFilesDir(), "ecgfile");
            //File file = new File(MainActivity.mContext.getFilesDir(), "lbp.txt");

            System.out.println("Test! file " + file.toString());

            RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, file);

            System.out.println("Test! body " + body.toString());

            String credential = Credentials.basic(MainActivity.storage.getAccountToken(), "");

            System.out.println("Test! token " + credential);

            Request request = new Request.Builder()
                    .url("http://api.cardiacare.ru/biosignals")
                    .addHeader("Authorization", credential)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println("Test! response " + response.body().string());

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
    }
}
