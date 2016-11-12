package ru.cardiacare.cardiacare.servey;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.servey.Answer;
import com.petrsu.cardiacare.smartcare.servey.Feedback;
import com.petrsu.cardiacare.smartcare.servey.Question;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Отображение экстренного опросника */

public class AlarmQuestionnaireActivity extends AppCompatActivity {

    RecyclerView AlarmQuestionnaireRecyclerView;
    RecyclerView.Adapter AlarmQuestionnaireAdapter;
    RecyclerView.LayoutManager AlarmQuestionnaireLayoutManager;
    public Context context = this;
    static public ImageButton buttonClean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            FileInputStream fIn = openFileInput("alarmFeedback.json");
            String jsonFromFile = readSavedData();
            Gson json = new Gson();
            Feedback qst = json.fromJson(jsonFromFile, Feedback.class);
            MainActivity.alarmFeedback = qst;

        } catch (Exception e) {

        }
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_questionnaire);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.backgroundFlag = 1;
                onBackPressed();
            }
        });

        AlarmQuestionnaireRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        AlarmQuestionnaireLayoutManager = new LinearLayoutManager(getApplicationContext());
        AlarmQuestionnaireRecyclerView.setLayoutManager(AlarmQuestionnaireLayoutManager);

        LinkedList<Question> alarmQuestionnaire = MainActivity.alarmQuestionnaire.getQuestions();
        int[] Types = new int[alarmQuestionnaire.size()];

        for (int i = 0; i < alarmQuestionnaire.size(); i++) {
            Question question = alarmQuestionnaire.get(i);
            Answer answer = question.getAnswer();
            switch (answer.getType()) {
                case "Text":
                    Types[i] = AlarmRecyclerViewAdapter.TextField;
                    break;
                case "MultipleChoise":
                    Types[i] = AlarmRecyclerViewAdapter.Multiplechoice;
                    break;
                case "SingleChoise":
                    Types[i] = AlarmRecyclerViewAdapter.Singlechoice;
                    break;
                case "BipolarQuestion":
                    Types[i] = AlarmRecyclerViewAdapter.Bipolarquestion;
                    break;
                case "Dichotomous":
                    Types[i] = AlarmRecyclerViewAdapter.Dichotomous;
                    break;
                case "GuttmanScale":
                    Types[i] = AlarmRecyclerViewAdapter.Guttmanscale;
                    break;
                case "LikertScale":
                    Types[i] = AlarmRecyclerViewAdapter.Likertscale;
                    break;
                case "ContinuousScale":
                    Types[i] = AlarmRecyclerViewAdapter.Continuousscale;
                    break;
                default:
                    Types[i] = AlarmRecyclerViewAdapter.DefaultValue;
            }
        }

        AlarmQuestionnaireAdapter = new AlarmRecyclerViewAdapter(MainActivity.alarmQuestionnaire.getQuestions(), Types, context);
        AlarmQuestionnaireRecyclerView.setAdapter(AlarmQuestionnaireAdapter);

        buttonClean = (ImageButton) findViewById(R.id.buttonClean);
        buttonClean.setVisibility(4);
        buttonClean.setOnClickListener(new View.OnClickListener() {// Clean
            @Override // Clean
            public void onClick(View v) {
                MainActivity.backgroundFlag = 1;
                buttonClean.setEnabled(false);
                buttonClean.setVisibility(4);
                MainActivity.alarmFeedback = new Feedback("2 test", "Student", "alarmFeedback");
                Gson json = new Gson();
                String jsonStr = json.toJson(MainActivity.alarmFeedback);
                System.out.println(jsonStr);
                writeData(jsonStr);

                Intent intent = getIntent();
                finish();
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }// Clean
        });
        buttonClean.setVisibility(0);
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.backgroundFlag = 0;
        MainActivity.alarmButton.setEnabled(true);
        MainActivity.alarmButton.setBackgroundResource(R.color.alarm_button_standart_color);
//        to SIB
//        Long timestamp = System.currentTimeMillis()/1000;
//        String ts = timestamp.toString();
//        MainActivity.smart.sendFeedback(MainActivity.nodeDescriptor, MainActivity.patientUri, MainActivity.alarmFeedbackUri,ts);
//        to Server
//        FeedbackPOST feedbackPOST = new FeedbackPOST(context);
//        feedbackPOST.execute();
//
//        MainActivity.QuestionnaireButton.setEnabled(true);
//        buttonClean.setEnabled(true);
//        MainActivity.alarmButton.setEnabled(true);
    }

    public void writeData(String data) {
        try {
//            FileOutputStream fOut = openFileOutput (filename , MODE_PRIVATE );
            FileOutputStream fOut = context.openFileOutput("alarmFeedback.json", context.MODE_PRIVATE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readSavedData() {
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream fIn = openFileInput("alarmFeedback.json");
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

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.backgroundFlag = 0;
        MainActivity.ConnectToSmartSpace();
    }

    @Override
    public void onPause() {
        Gson json = new Gson();
        String jsonStr = json.toJson(MainActivity.alarmFeedback);
        System.out.println(jsonStr);
        writeData(jsonStr);
        super.onPause();
        if (MainActivity.backgroundFlag == 0) {
            MainActivity.DisconnectFromSmartSpace();
        }
    }

    @Override
    public void onBackPressed() {
        MainActivity.backgroundFlag = 1;
        super.onBackPressed();
    }
}