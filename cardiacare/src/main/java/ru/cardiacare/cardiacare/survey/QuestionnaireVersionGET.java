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
//                    String jsonFromFile = QuestionnaireHelper.readSavedData(context);
                    String jsonFromFile = "{ \"id\": 80, \"doctor_id\": 1, \"emergency\": 0, \"version\": \"1.0\", \"description\": \"Description\", \"created_at\": \"2017-01-30\", \"lang\": \"RU\", \"questions\": [ { \"id\": 1, \"description\": \"Dichotomous\", \"answers\": [ { \"id\": 11, \"type\": \"Dichotomous\", \"items\": [ { \"id\": 111, \"itemText\": \"Dichotomous1\", \"itemScore\": 0, \"subAnswers\": [ { \"id\": 1111, \"type\": \"Text\", \"items\": [] } ] }, { \"id\": 112, \"itemText\": \"Dichotomous2\", \"itemScore\": 0, \"subAnswers\": [ { \"id\": 1121, \"type\": \"Text\", \"items\": [] } ] } ] } ] }, { \"id\": 2, \"description\": \"ContinuousScale\", \"answers\": [ { \"id\": 22, \"type\": \"ContinuousScale\", \"items\": [ { \"id\": 222, \"itemScore\": 0, \"subAnswers\": [] }, { \"id\": 223, \"itemScore\": 100, \"subAnswers\": [] } ] } ] }, { \"id\": 3, \"description\": \"Text\", \"answers\": [ { \"id\": 33, \"type\": \"Text\", \"items\": [] } ] }, { \"id\": 4, \"description\": \"MultipleChoise\", \"answers\": [ { \"id\": 44, \"type\": \"MultipleChoise\", \"items\": [ { \"id\": 441, \"itemText\": \"MultipleChoice1\", \"subAnswers\": [] }, { \"id\": 442, \"itemText\": \"MultipleChoice2\", \"subAnswers\": [] }, { \"id\": 443, \"itemText\": \"MultipleChoice3\", \"subAnswers\": [] }, { \"id\": 444, \"itemText\": \"MultipleChoice4\", \"subAnswers\": [] }, { \"id\": 445, \"itemText\": \"MultipleChoice5\", \"subAnswers\": [] } ] } ] }, { \"id\": 5, \"description\": \"SingleChoise\", \"answers\": [ { \"id\": 55, \"type\": \"SingleChoise\", \"items\": [ { \"id\": 551, \"itemText\": \"SingleChoice1\", \"itemScore\": 0, \"subAnswers\": [] }, { \"id\": 552, \"itemText\": \"SingleChoice2\", \"itemScore\": 0, \"subAnswers\": [] }, { \"id\": 553, \"itemText\": \"SingleChoice3\", \"itemScore\": 0, \"subAnswers\": [] }, { \"id\": 554, \"itemText\": \"SingleChoice4\", \"itemScore\": 0, \"subAnswers\": [ { \"id\": 5541, \"type\": \"Text\", \"items\": [] } ] } ] } ] } ] }";
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
