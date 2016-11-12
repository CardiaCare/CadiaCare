package ru.cardiacare.cardiacare.servey;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.servey.Answer;
import com.petrsu.cardiacare.smartcare.servey.AnswerItem;
import com.petrsu.cardiacare.smartcare.servey.Question;
import com.petrsu.cardiacare.smartcare.servey.Questionnaire;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import ru.cardiacare.cardiacare.user.AccountStorage;
import ru.cardiacare.cardiacare.MainActivity;

/* Работа с периодическим опросником */

public class QuestionnaireHelper {
    static public String serverUri;
    static public String filename = "questionnaire.json";

    // Отображение опросника
    static public void showQuestionnaire(Context context) {

        String QuestionnaireVersion = MainActivity.storage.getQuestionnaireVersion();
        String qst = MainActivity.smart.getQuestionnaire(MainActivity.nodeDescriptor);
        String QuestionnaireServerVersion = MainActivity.smart.getQuestionnaireVersion(MainActivity.nodeDescriptor, qst);

        // Если опросник ещё не был загружен или его версия ниже версии на сервере, то загружаем опросник
        if ((QuestionnaireVersion.equals("")) || (!QuestionnaireServerVersion.equals(QuestionnaireVersion)) || (MainActivity.questionnaire == null)) {
            serverUri = MainActivity.smart.getQuestionnaireSeverUri(MainActivity.nodeDescriptor, qst);
            Log.i("serverUri = ", serverUri);
            MainActivity.storage.sPref = context.getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, Context.MODE_PRIVATE);
            MainActivity.storage.setVersion(QuestionnaireServerVersion);
            QuestionnaireGET questionnaireGET = new QuestionnaireGET(context);
            questionnaireGET.execute();
        } else {
//            FeedbackPOST feedbackPOST = new FeedbackPOST(context);
//            feedbackPOST.execute();
            String jsonFromFile = readSavedData(context);
            Gson json = new Gson();
            MainActivity.questionnaire = json.fromJson(jsonFromFile, Questionnaire.class);
            printQuestionnaire(MainActivity.questionnaire);
//            MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
            Intent intentq = new Intent(context, QuestionnaireActivity.class);
            context.startActivity(intentq);
        }
    }

    // Вывод опросника в лог
    static public void printQuestionnaire(Questionnaire questionnaire) {
        LinkedList<Question> q = questionnaire.getQuestions();
        for (int i = 0; i < q.size(); i++) {
            Question qst = q.get(i);
            Log.i(MainActivity.TAG, qst.getDescription());
            Answer a = qst.getAnswer();
//            if (a.size()>0) {
//            for(int h = 0; h < a.size(); h++) {
            Log.i(MainActivity.TAG, a.getType());
            LinkedList<AnswerItem> ai = a.getItems();
            if (ai.size() > 0) {
                Log.i(MainActivity.TAG, "AnswerItem");
                for (int j = 0; j < ai.size(); j++) {
                    AnswerItem item = ai.get(j);
                    Log.i(MainActivity.TAG, item.getItemText());
                    LinkedList<Answer> suba = item.getSubAnswers();
                    if (suba.size() > 0) {
                        for (int k = 0; k < suba.size(); k++) {
                            Log.i(MainActivity.TAG, "subAnswer");
                            Answer sitem = suba.get(k);
                            Log.i(MainActivity.TAG, sitem.getType());
                        }
                    }
//                     }
//                    }
                }
            }
        }
    }

    // Чтение из файла
    static public String readSavedData(Context context) {
        StringBuilder datax = new StringBuilder("");
        try {
            FileInputStream fIn = context.openFileInput(filename);
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
}
