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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;

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
                String str = "{" +" \"questionnaire_id\": 7," +
                        "  \"patient_id\": 2," +
                        "  \"data\": \"new feedback\"}";

                MediaType JSON = MediaType.parse("application/json; charset=utf-8");

                OkHttpClient client = new OkHttpClient();

                RequestBody body = RequestBody.create(JSON, str);

                String credential = Credentials.basic(MainActivity.storage.getAccountToken(),"");

                Request request = new Request.Builder()
                        .url("http://api.cardiacare.ru/patients/14/feedback")
                        .addHeader("Authorization", credential )
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

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

            } catch (Exception e) {
                Log.e("Exception", e.getMessage());
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