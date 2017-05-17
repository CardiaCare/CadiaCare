package ru.cardiacare.cardiacare.survey;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Base64;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.survey.Questionnaire;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.cardiacare.cardiacare.MainActivity;

/* Запрос на получение версии опросника */

public class QuestionnaireVersionGET extends AsyncTask<Void, String, String> {

    private Context context;
    private HttpURLConnection urlConnection = null;
    private BufferedReader reader = null;
    private String resultJson = "";

    public QuestionnaireVersionGET(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    public String doInBackground(Void... params) {

        try {
            URL url = new URL("http://api.cardiacare.ru/patients/" + MainActivity.storage.getAccountId() + "/questionnaires");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString((MainActivity.storage.getAccountToken() + ":").getBytes("UTF-8"), Base64.DEFAULT));//MainActivity.authorization_token);///
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            resultJson = buffer.toString();

        } catch (Exception e) {
        }


        return resultJson;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println("Test! resultversion " + result);
        JSONArray jArray = null;

        try {
            jArray = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < jArray.length(); i++) {
            try {
                JSONObject oneObject = jArray.getJSONObject(i);
                String id = oneObject.getString("id");
                String version = oneObject.getString("version");

//                System.out.println("Test! " + version + " ? " + MainActivity.storage.getQuestionnaireVersion());
                if (version.compareTo(MainActivity.storage.getQuestionnaireVersion()) != 0) {

                    QuestionnaireHelper.serverUri = "http://api.cardiacare.ru/questionnaire/" + id;
                    MainActivity.storage.setVersion(version);
                    QuestionnaireGET questionnaireGET = new QuestionnaireGET(context);
                    questionnaireGET.execute();
                    QuestionnaireHelper.questionnaireDownloadedFromFile = false;
                } else {
                    String jsonFromFile = QuestionnaireHelper.readSavedData(context);
                    QuestionnaireHelper.questionnaireDownloadedFromFile = true;
                    Gson json = new Gson();
                    QuestionnaireHelper.questionnaire = json.fromJson(jsonFromFile, Questionnaire.class);
                    Intent intent = new Intent(context, QuestionnaireActivity.class);
                    intent.putExtra("questionnaireType", QuestionnaireHelper.questionnaireType);
                    context.startActivity(intent);
                }
            } catch (JSONException e) {
            }
        }
    }
}
