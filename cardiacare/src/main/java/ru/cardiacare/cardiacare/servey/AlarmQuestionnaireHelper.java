package ru.cardiacare.cardiacare.servey;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.servey.Questionnaire;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.user.AccountStorage;

/* Работа с опросником */

public class AlarmQuestionnaireHelper {
    static public String serverUri;
    static public String filename = "alarmQuestionnaire.json";

    // Отображение экстренного опросника
    static public void showAlarmQuestionnaire(Context context) {

//        String AlarmQuestionnaireVersion = MainActivity.storage.getQuestionnaireVersion();
//        String qst = MainActivity.smart.getQuestionnaire(MainActivity.nodeDescriptor);
//        String QuestionnaireServerVersion = MainActivity.smart.getQuestionnaireVersion(MainActivity.nodeDescriptor,qst);

        // Если опросник ещё не был загружен или его версия ниже версии на сервере, то загружаем опросник
        if(MainActivity.alarmQuestionnaire == null) {
            serverUri = "http://api.cardiacare.ru/index.php?r=questionnaire/read&id=2";
//            MainActivity.storage.sPref = context.getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, Context.MODE_PRIVATE);
//            MainActivity.storage.setVersion(QuestionnaireServerVersion);
            AlarmQuestionnaireGET alarmQuestionnaireGET = new AlarmQuestionnaireGET(context);
            alarmQuestionnaireGET.execute();
        } else {
//            FeedbackPOST feedbackPOST = new FeedbackPOST(context);
//            feedbackPOST.execute();
            String jsonFromFile = readSavedDataAlarm(context);
            Gson json = new Gson();
            MainActivity.alarmQuestionnaire = json.fromJson(jsonFromFile,Questionnaire.class);
            QuestionnaireHelper.printQuestionnaire(MainActivity.alarmQuestionnaire);
            //MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
            Intent intentq = new Intent(context, AlarmQuestionnaireActivity.class);
            context.startActivity(intentq);
        }
    }

    // Чтение из файла
    static public String readSavedDataAlarm (Context context) {
        StringBuilder datax = new StringBuilder("");
        try {
            FileInputStream fIn = context.openFileInput(filename);
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
}
