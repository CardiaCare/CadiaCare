package ru.cardiacare.cardiacare.survey;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.survey.Answer;
import com.petrsu.cardiacare.smartcare.survey.AnswerItem;
import com.petrsu.cardiacare.smartcare.survey.Question;
import com.petrsu.cardiacare.smartcare.survey.Questionnaire;

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
    static public String questionnaireFile = "questionnaire.json";
    static public String alarmQuestionnaireFile = "alarmQuestionnaire.json";
    static public boolean questionnaireDownloaded = false;
    static public boolean alarmQuestionnaireDownloaded = false;
    static public String questionnaireType;

    static public Questionnaire questionnaire;
    static public Questionnaire alarmQuestionnaire;

    // Отображение опросника
    static public void showQuestionnaire(Context context) {
        questionnaireType = "periodic";
        String QuestionnaireVersion = MainActivity.storage.getQuestionnaireVersion();

        String QuestionnaireServerVersion = "1.0";

        // Если опросник ещё не был загружен или его версия ниже версии на сервере, то загружаем опросник
        if ((QuestionnaireVersion.equals("")) || (!QuestionnaireServerVersion.equals(QuestionnaireVersion)) || (!questionnaireDownloaded)) {
            serverUri = "http://api.cardiacare.ru/questionnaire/7";
            //serverUri = "http://api.cardiacare.ru/index.php?r=questionnaire/read&id=1";
            Log.i("serverUri = ", serverUri);
            //System.out.println("Test! token in main " + MainActivity.storage.getAccountToken());
            MainActivity.storage.sPref = context.getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, Context.MODE_PRIVATE);
            MainActivity.storage.setVersion(QuestionnaireServerVersion);
            QuestionnaireGET questionnaireGET = new QuestionnaireGET(context);
            questionnaireGET.execute();
        } else {
//            FeedbackPOST feedbackPOST = new FeedbackPOST(context);
//            feedbackPOST.execute();
            String jsonFromFile = readSavedData(context);
            //String jsonFromFile = "{\"emergency\": false,\"version\": \"1.0\",\"description\": \"Description\",\"lang\": \"RU\",\"questions\":[{\"answers\":[{\"items\":[{\"itemScore\":\"0\",\"itemText\":\"No\",\"subAnswers\":[{\"items\":[],\"type\":\"Text\",\"uri\":\"http://oss.fruct.org/smartcare#8117516\"}],\"uri\":\"http://oss.fruct.org/smartcare#3511018\"},{\"itemScore\":\"0\",\"itemText\":\"Yes\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#7558817\"}],\"type\":\"Dichotomous\",\"uri\":\"http://oss.fruct.org/smartcare#8117516\"}],\"description\":\"Intense headaches?\",\"uri\":\"http://oss.fruct.org/smartcare#5532215\"},{\"answers\":[{\"items\":[{\"itemScore\":\"0\",\"itemText\":\"No\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#3511022\"},{\"itemScore\":\"0\",\"itemText\":\"Yes\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#7558821\"}],\"type\":\"Dichotomous\",\"uri\":\"http://oss.fruct.org/smartcare#8117520\"}],\"description\":\"Severe dizziness?\",\"uri\":\"http://oss.fruct.org/smartcare#5532219\"},{\"answers\":[{\"items\":[{\"itemScore\":\"0\",\"itemText\":\"No\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#3511026\"},{\"itemScore\":\"0\",\"itemText\":\"Yes\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#7558825\"}],\"type\":\"Dichotomous\",\"uri\":\"http://oss.fruct.org/smartcare#8117524\"}],\"description\":\"Nausea, vomiting?\",\"uri\":\"http://oss.fruct.org/smartcare#5532223\"},{\"answers\":[{\"items\":[{\"itemScore\":\"0\",\"itemText\":\"No\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#3511030\"},{\"itemScore\":\"0\",\"itemText\":\"Yes\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#7558829\"}],\"type\":\"Dichotomous\",\"uri\":\"http://oss.fruct.org/smartcare#8117528\"}],\"description\":\"Acute visual impairment?\",\"uri\":\"http://oss.fruct.org/smartcare#5532227\"},{\"answers\":[{\"items\":[{\"itemScore\":\"0\",\"itemText\":\"No\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#3511034\"},{\"itemScore\":\"0\",\"itemText\":\"Yes\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#7558833\"}],\"type\":\"Dichotomous\",\"uri\":\"http://oss.fruct.org/smartcare#8117532\"}],\"description\":\"Severe chest pain?\",\"uri\":\"http://oss.fruct.org/smartcare#5532231\"},{\"answers\":[{\"items\":[{\"itemScore\":\"0\",\"itemText\":\"No\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#3511038\"},{\"itemScore\":\"0\",\"itemText\":\"Yes\",\"subAnswers\":[],\"uri\":\"http://oss.fruct.org/smartcare#7558837\"}],\"type\":\"Dichotomous\",\"uri\":\"http://oss.fruct.org/smartcare#8117536\"}],\"description\":\"The lack of air?\",\"uri\":\"http://oss.fruct.org/smartcare#5532235\"}]}";
            Gson json = new Gson();
            questionnaire = json.fromJson(jsonFromFile, Questionnaire.class);
//            printQuestionnaire(questionnaire);
//            MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(context, QuestionnaireActivity.class);
            intent.putExtra("questionnaireType", questionnaireType);
            context.startActivity(intent);
        }
    }

    static public void showAlarmQuestionnaire(Context context) {
        questionnaireType = "alarm";
        // Если опросник ещё не был загружен, то загружаем опросник
        if (!alarmQuestionnaireDownloaded) {
            serverUri = "http://api.cardiacare.ru/survey/2";
            //serverUri = "http://api.cardiacare.ru/index.php?r=questionnaire/read&id=2";
            QuestionnaireGET questionnaireGET = new QuestionnaireGET(context);
            questionnaireGET.execute();
        } else {
//            FeedbackPOST feedbackPOST = new FeedbackPOST(context);
//            feedbackPOST.execute();
            String jsonFromFile = readSavedData(context);
            Gson json = new Gson();
            alarmQuestionnaire = json.fromJson(jsonFromFile, Questionnaire.class);
//            printQuestionnaire(alarmQuestionnaire);
//            MainActivity.mProgressBar.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(context, QuestionnaireActivity.class);
            intent.putExtra("questionnaireType", questionnaireType);
            context.startActivity(intent);
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
    private static String readSavedData(Context context) {
        StringBuilder datax = new StringBuilder("");
        try {
            FileInputStream fIn;
            if (questionnaireType.equals("periodic"))
                fIn = context.openFileInput(questionnaireFile);
            else fIn = context.openFileInput(alarmQuestionnaireFile);
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
