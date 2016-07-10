package com.petrsu.cardiacare.smartcarepatient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.Questionnaire;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/* Загрузка опросника с сервера */

public class QuestionnaireGET extends AsyncTask<Void, Integer, Integer> {

    Context context;
    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";

    public QuestionnaireGET(Context context) {
            this.context = context;
        }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        MainActivity.mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public Integer doInBackground(Void... params) {
        try {
            URL url = new URL(QuestionnaireHelper.serverUri);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
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
            e.printStackTrace();
            return -1;
        }

        Gson json = new Gson();
        MainActivity.questionnaire = json.fromJson(resultJson,Questionnaire.class);
        if (MainActivity.questionnaire == null) {
            return -1;
        }
        try {
            OutputStreamWriter writer = new OutputStreamWriter(context.openFileOutput(QuestionnaireHelper.filename, Context.MODE_PRIVATE));
            writer.write(resultJson);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
        QuestionnaireHelper.printQuestionnaire(MainActivity.questionnaire);
        return 0;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        if (result == -1) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Ошибка");
            alertDialog.setMessage("Не удаётся подключиться к серверу. Повторите попытку позже");
            alertDialog.setNegativeButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
                        }
                    });
            alertDialog.show();
        } else {
            MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
            Intent intentq = new Intent(context, QuestionnaireActivity.class);
            context.startActivity(intentq);
        }
    }
}