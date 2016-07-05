package com.petrsu.cardiacare.smartcarepatient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.Questionnaire;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Kiribaz on 05.07.16.
 */

public class QuestionnaireLoad extends AsyncTask<Void, Integer, Void> {
        Context context;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = "";
        public QuestionnaireLoad(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MainActivity.mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public Void doInBackground(Void... params) {
            /*
            questionnaire = smart.getQuestionnaire(nodeDescriptor);
            printQuestionnaire(questionnaire);
            // id, personName, questionnaire
            feedback = new Feedback("1 test", "Student", questionnaire.getUri());
            return null;
            */

            try {
                URL url = new URL(MainActivity.serverUri);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }

            Gson json = new Gson();
            MainActivity.questionnaire = json.fromJson(resultJson,Questionnaire.class);
            //MainActivity.writeData(resultJson);
            try {
                //FileOutputStream fOut = openFileOutput (filename , MODE_PRIVATE );
                FileOutputStream fOut = context.openFileOutput(MainActivity.filename, context.MODE_PRIVATE );
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write(resultJson);
                osw.flush();
                osw.close();
            } catch (Exception e){
                e.printStackTrace();
            }
            MainActivity.printQuestionnaire(MainActivity.questionnaire);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
            Intent intentq = new Intent(context, QuestionnaireActivity.class);
            context.startActivity(intentq);
        }
}