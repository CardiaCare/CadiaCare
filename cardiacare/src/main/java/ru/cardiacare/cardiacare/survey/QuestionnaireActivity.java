package ru.cardiacare.cardiacare.survey;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.survey.Answer;
import com.petrsu.cardiacare.smartcare.survey.Feedback;
import com.petrsu.cardiacare.smartcare.survey.Question;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import ru.cardiacare.cardiacare.HelpActivity;
import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Отображение периодического опросника */

public class QuestionnaireActivity extends AppCompatActivity {

    public static final int TextField = 0;
    public static final int Multiplechoice = 1;
    public static final int Singlechoice = 2;
    public static final int Bipolarquestion = 3;
    public static final int Guttmanscale = 4;
    public static final int Likertscale = 5;
    public static final int Continuousscale = 6;
    public static final int Dichotomous = 7;
    public static final int DefaultValue = 8;

    static public Feedback feedback;
    static public Feedback alarmFeedback;
    RecyclerView QuestionnaireRecyclerView;
    RecyclerView.Adapter QuestionnaireAdapter;
    RecyclerView.LayoutManager QuestionnaireLayoutManager;
    public Context context = this;
    static public Context mContext;
    boolean sendFlag = false; // Была ли нажата кнопка "Отправить", true - была нажата / false - не была
    static ImageButton buttonRefresh;
    static ImageButton buttonSend;
    static String periodic = "periodic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final FileInputStream fIn;
        mContext = this;
        try {
//            if (QuestionnaireHelper.questionnaireType.equals(periodic))
//                fIn = openFileInput("feedback.json");
//            else fIn = openFileInput("alarmFeedback.json");
            String jsonFromFile = readSavedData();
            Gson json = new Gson();
            Feedback qst = json.fromJson(jsonFromFile, Feedback.class);
            Log.i("QActivity", "QuestionnaireHelper.questionnaireType = " + QuestionnaireHelper.questionnaireType);
            if (QuestionnaireHelper.questionnaireType.equals(periodic)) {
                if (qst != null) {
                    feedback = qst;
                    Gson json3 = new Gson();
                    String jsonFeedback2;
                    jsonFeedback2 = json3.toJson(feedback);
                    Log.i("QActivity", "feedbackFromFile = " + jsonFeedback2);
                } else {
                    feedback = new Feedback(QuestionnaireHelper.questionnaire.getId(), QuestionnaireHelper.questionnaire.getLang());
                    Gson json2 = new Gson();
                    String jsonFeedback;
                    jsonFeedback = json2.toJson(feedback);
                    Log.i("QActivity", "newFeedback = " + jsonFeedback);
                }
            } else {
                if (qst != null) {
                    alarmFeedback = qst;
                } else {
                    alarmFeedback = new Feedback(QuestionnaireHelper.questionnaire.getId(), QuestionnaireHelper.questionnaire.getLang());
                }
            }
        } catch (Exception e) {
            Log.i("QActivity", "CATCH");
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_questionnaire);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);
//                configIntent.setAction(" ");
//                startActivity(configIntent);
                if ((!sendFlag) && (feedback.getResponds().size() > 0)) {
                    feedbackDialog();
                } else {
                    startActivity(new Intent(QuestionnaireActivity.this, MainActivity.class));
                }
            }
        });

        if (!QuestionnaireHelper.questionnaireDownloadedFromFile) {
            Log.i("QuestionnaireActivity", "!QuestionnaireHelper.questionnaireDownloadedFromFile = " + QuestionnaireHelper.questionnaireDownloadedFromFile);
            clearFeedback();
        }

        QuestionnaireRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        QuestionnaireLayoutManager = new LinearLayoutManager(getApplicationContext());
        QuestionnaireRecyclerView.setLayoutManager(QuestionnaireLayoutManager);

        LinkedList<Question> questionnaire;
        if (QuestionnaireHelper.questionnaireType.equals(periodic))
            questionnaire = QuestionnaireHelper.questionnaire.getQuestions();
        else questionnaire = QuestionnaireHelper.alarmQuestionnaire.getQuestions();
        int[] Types = new int[questionnaire.size()];

        for (int i = 0; i < questionnaire.size(); i++) {
//            Question question = questionnaire.get(i);
//            Answer answer = question.getAnswer();
            Question question = questionnaire.get(i);
            LinkedList<Answer> answers = question.getAnswers();
            for (int j = 0; j < answers.size(); j++) {
                Answer answer = answers.get(j);
                Log.i("Questionnaire", i + " question, " + j + " answer, " + answer.getType());
                switch (answer.getType()) {
                    case "Text":
                        Types[i] = TextField;
                        break;
                    case "MultipleChoise":
                        Types[i] = Multiplechoice;
                        break;
                    case "SingleChoise":
                        Types[i] = Singlechoice;
                        break;
                    case "BipolarQuestion":
                        Types[i] = Bipolarquestion;
                        break;
                    case "Dichotomous":
                        Types[i] = Dichotomous;
                        break;
                    case "GuttmanScale":
                        Types[i] = Guttmanscale;
                        break;
                    case "LikertScale":
                        Types[i] = Likertscale;
                        break;
                    case "ContinuousScale":
                        Types[i] = Continuousscale;
                        break;
                    default:
                        Types[i] = DefaultValue;
                }
            }
        }

        if (QuestionnaireHelper.questionnaireType.equals(periodic)) {
            QuestionnaireAdapter = new RecyclerViewAdapter(QuestionnaireHelper.questionnaire.getQuestions(), Types, context);
            QuestionnaireRecyclerView.setAdapter(QuestionnaireAdapter);
        }
//        } else {
//            QuestionnaireAdapter = new AlarmRecyclerViewAdapter(QuestionnaireHelper.alarmQuestionnaire.getQuestions(), Types, context);
//            QuestionnaireRecyclerView.setAdapter(QuestionnaireAdapter);
//        }

        buttonRefresh = (ImageButton) findViewById(R.id.buttonClean);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {// Clean
            @Override // Clean
            public void onClick(View v) {
                String jsonStr = "";
                buttonRefresh.setEnabled(false);
                if (QuestionnaireHelper.questionnaireType.equals(periodic))
                    feedback = new Feedback(QuestionnaireHelper.questionnaire.getId(), QuestionnaireHelper.questionnaire.getLang());
//                else alarmFeedback = new Feedback("2 test", "Student", "alarmFeedback");

                Gson json = new Gson();
                if (QuestionnaireHelper.questionnaireType.equals(periodic))
                    jsonStr = json.toJson(feedback);
                else jsonStr = json.toJson(alarmFeedback);
                System.out.println(jsonStr);
                writeData(jsonStr);

                Intent intent = getIntent();
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }// Clean
        });

        buttonSend = (ImageButton) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendAnswers();
            }
        });
    }

    public void sendAnswers() {
        if (MainActivity.isNetworkAvailable(context)) {
            String jsonStr;
            Gson json = new Gson();
            if (QuestionnaireHelper.questionnaireType.equals(periodic))
                jsonStr = json.toJson(feedback);
            else jsonStr = json.toJson(alarmFeedback);
            System.out.println("feedback: " + jsonStr);
            writeData(jsonStr);
            if (QuestionnaireHelper.questionnaireType.equals(periodic)) {
                // To SIB
                Long timestamp = System.currentTimeMillis() / 1000;
                String ts = timestamp.toString();
                MainActivity.storage.setLastQuestionnairePassDate(ts);
            }
            if (feedback.getResponds().size() > 0) {
                FeedbackPOST feedbackPOST = new FeedbackPOST(context);
                feedbackPOST.execute();
            }
            sendFlag = true;
//            if (MainActivity.storage.getFeedbackRefresh()) {
////                Log.i("QuestionnaireActivity", "SendAnswers, getFeedbackRefresh() = " + MainActivity.storage.getFeedbackRefresh());
//                clearFeedback();
//            }
            startActivity(new Intent(QuestionnaireActivity.this, MainActivity.class));
        } else {
            wiFiAlertDialog();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
//        if (QuestionnaireHelper.questionnaireType.equals(periodic))
//            MainActivity.serveyButton.setEnabled(true);
//        else {
////            alarmButton.setEnabled(true);
////            alarmButton.setBackgroundResource(R.color.alarm_button_standard_color);
//        }

//        MainActivity.QuestionnaireButton.setEnabled(true);//возвращаем состояние нажатия от повторного нажатия
//        buttonRefresh.setEnabled(true);//возвращаем состояние нажатия от повторного нажатия
//        alarmButton.setEnabled(true);//возвращаем состояние нажатия от повторного нажатия
    }

    static public void writeData(String data) {
        try {
//            FileOutputStream fOut = openFileOutput (filename , MODE_PRIVATE );
            FileOutputStream fOut;
            if (QuestionnaireHelper.questionnaireType.equals(periodic))
                fOut = mContext.openFileOutput("feedback.json", mContext.MODE_PRIVATE);
            else fOut = mContext.openFileOutput("alarmFeedback.json", mContext.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readSavedData() {
        StringBuilder datax = new StringBuilder("");
        FileInputStream fIn;
        try {
            if (QuestionnaireHelper.questionnaireType.equals(periodic))
                fIn = openFileInput("feedback.json");
            else fIn = openFileInput("alarmFeedback.json");
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

    static public void clearFeedback() {
//        Log.i("QQQ", "clearFeedback()");
        int respondsCount = feedback.getResponds().size();
        for (int i = respondsCount; i > 0; i--) {
            feedback.getResponds().remove(i - 1);
        }
        String jsonStr = "";
//        Gson json = new Gson();
//        jsonStr = json.toJson(feedback);
        writeData(jsonStr);
    }

    @Override
    public void onPause() {
        super.onPause();
        sendFlag = false;
    }

    @Override
    public void onBackPressed() {
//        Intent configIntent = new Intent(getApplicationContext(), MainActivity.class);
//        configIntent.setAction(" ");
//        startActivity(configIntent);
        if ((!sendFlag) && (feedback.getResponds().size() > 0)) {
            feedbackDialog();
        } else {
            startActivity(new Intent(QuestionnaireActivity.this, MainActivity.class));
        }
    }

    public void feedbackDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        alertDialog.setTitle(R.string.dialog_feedback_title);
        alertDialog.setMessage(R.string.dialog_feedback_message);
        alertDialog.setPositiveButton(R.string.dialog_feedback_positive_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendAnswers();
                    }
                });
        alertDialog.setNegativeButton(R.string.dialog_feedback_negative_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String jsonStr = "";
                        Gson json = new Gson();
                        jsonStr = json.toJson(feedback);
                        writeData(jsonStr);
                        startActivity(new Intent(QuestionnaireActivity.this, MainActivity.class));
                    }
                });
        alertDialog.show();
    }

    // WiFi диалог
    static public void wiFiAlertDialog() {
        final WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        alertDialog.setTitle(R.string.dialog_wifi_title);
        alertDialog.setMessage(R.string.dialog_wifi_message);
        alertDialog.setPositiveButton(R.string.dialog_wifi_positive_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        wifiManager.setWifiEnabled(true);
                        mContext.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        alertDialog.setNegativeButton(R.string.dialog_wifi_negative_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
}