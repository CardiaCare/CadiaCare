package com.petrsu.cardiacare.smartcarepatient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.petrsu.cardiacare.smartcare.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    static public SmartCareLibrary smart;
    static public long nodeDescriptor;
    static protected String patientUri;
    static protected String authUri;
    static protected String locationUri;
    static protected String alarmUri;
    static public String serverUri;
    private GoogleApiClient client;
    public Context context = this;

    Toolbar mToolbar;
    Button alarmButton;
    Button nextButton;
    EditText etFirstName;
    EditText etSecondName;
    static public ProgressBar mProgressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    com.petrsu.cardiacare.smartcarepatient.AccountStorage storage;
    static public com.petrsu.cardiacare.smartcarepatient.LocationService gps;

    public static boolean registratedState = false;
    //protected static final String TAG = "location";

    public MainActivity() {}

    static String TAG = "SS-main";

    static public Questionnaire questionnaire;
    static protected Feedback feedback;

    static String filename = "questionnaire.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

       smart = new SmartCareLibrary();
        nodeDescriptor = smart.connectSmartSpace("X", "78.46.130.194", 10010);
        if (nodeDescriptor == -1){
            return;
        }

        patientUri = smart.initPatient(nodeDescriptor);
        if (patientUri == null){
            return;
        }

        locationUri = smart.initLocation(nodeDescriptor,patientUri);
        if (locationUri == null) {
            return;
        }

        GPSLoad gpsLoad = new GPSLoad(context);
        gpsLoad.execute();

        storage = new com.petrsu.cardiacare.smartcarepatient.AccountStorage();
        storage.sPref = getSharedPreferences(storage.ACCOUNT_PREFERENCES, MODE_PRIVATE);

        if (storage.getAccountFirstName().isEmpty() || storage.getAccountSecondName().isEmpty()){
            setUnregisteredActivity();
        } else {
            setRegisteredActivity();
        }
    }

    public void writeData ( String data ){
        try {
            //FileOutputStream fOut = openFileOutput (filename , MODE_PRIVATE );
            FileOutputStream fOut = context.openFileOutput(filename, context.MODE_PRIVATE );
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            osw.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String readSavedData(){
        StringBuffer datax = new StringBuffer("");
        try {
            FileInputStream fIn = openFileInput(filename);
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

    static public void printQuestionnaire(Questionnaire questionnaire){
        LinkedList<Question> q = questionnaire.getQuestions();
        for (int i = 0; i < q.size(); i++) {
            Question qst = q.get(i);
            Log.i(TAG, qst.getDescription());
            Answer a = qst.getAnswer();
            //if (a.size()>0) {
            //for(int h = 0; h < a.size(); h++) {
            Log.i(TAG, a.getType());
            LinkedList<AnswerItem> ai = a.getItems();
            if (ai.size() > 0) {
                Log.i(TAG, "AnswerItem");
                for (int j = 0; j < ai.size(); j++) {
                    AnswerItem item = ai.get(j);
                    Log.i(TAG, item.getItemText());
                    LinkedList<Answer> suba = item.getSubAnswers();
                    if (suba.size() > 0) {
                        for (int k = 0; k < suba.size(); k++) {
                            Log.i(TAG, "subAnswer");
                            Answer sitem = suba.get(k);
                            Log.i(TAG, sitem.getType());
                            LinkedList<AnswerItem> sai = sitem.getItems();
                        }
                    }
                    // }
                    //}
                }
            }
        }
    }

    public void setRegisteredActivity() {
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        alarmButton = (Button)findViewById(R.id.alarmButton);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmButton.setBackgroundColor(0x77a71000);
                alarmUri = smart.sendAlarm(nodeDescriptor, patientUri);
            }
        });

        Button saveToJson = (Button)findViewById(R.id.buttonSJson);
        saveToJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson json = new Gson();
                String jsonStr = json.toJson(questionnaire);
                //String jsonStr = json.toJson(feedback);
                System.out.println(jsonStr);
                writeData(jsonStr);
            }
        });

        Button loadFromJson = (Button)findViewById(R.id.buttonLJson);
        loadFromJson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String jsonFromFile = readSavedData();
                    Gson json = new Gson();
                    Questionnaire qst = json.fromJson(jsonFromFile,Questionnaire.class);
                    questionnaire = qst;
                    printQuestionnaire(questionnaire);
            }
        });

        Button AboutLoad = (Button)findViewById(R.id.AboutLoad);
        AboutLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                smart.sendFeedback(nodeDescriptor, patientUri, "050716");
                Intent intentq = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentq);
            }
        });

        Button ButtonExit = (Button) findViewById(R.id.ButtonExit);
        ButtonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button QuestionnaireLoad = (Button) findViewById(R.id.QuestionnaireLoad);
        QuestionnaireLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String QuestionnaireVersion = storage.getQuestionnaireVersion();
                String qst = smart.getQuestionnaire(nodeDescriptor);
                String QuestionnaireServerVersion = smart.getQuestionnaireVersion(nodeDescriptor,qst);
                if((QuestionnaireVersion == "") || (!QuestionnaireServerVersion.equals(QuestionnaireVersion))) {
                    serverUri = smart.getQuestionnaireSeverUri(nodeDescriptor, qst);
                    storage.sPref = getSharedPreferences(storage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
                    storage.setVersion(QuestionnaireServerVersion);

                    QuestionnaireGET questionnaireGET = new QuestionnaireGET(context);
                    questionnaireGET.execute();
                } else {
                    FeedbackPOST feedbackPOST = new FeedbackPOST(context);
                    feedbackPOST.execute();

                    String jsonFromFile = readSavedData();
                    Gson json = new Gson();
                    Questionnaire qst1 = json.fromJson(jsonFromFile,Questionnaire.class);
                    questionnaire = qst1;
                    printQuestionnaire(questionnaire);
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Intent intentq = new Intent(MainActivity.this, QuestionnaireActivity.class);
                    startActivity(intentq);
                }
            }

        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar); mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.INVISIBLE);


        storage.sPref = getSharedPreferences(storage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
        smart.insertPersonName(nodeDescriptor, patientUri, storage.getAccountFirstName() + " " + storage.getAccountSecondName());
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(true);
                refreshAll();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void setUnregisteredActivity(){
        setContentView(R.layout.activity_main_account_connection);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etSecondName = (EditText) findViewById(R.id.etSecondName);

        etSecondName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_NULL) {
                    registration(etFirstName.getText().toString(), etSecondName.getText().toString());
                    return true;
                }
                return false;
            }
        });

        nextButton = (Button)findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration(etFirstName.getText().toString(), etSecondName.getText().toString());
            }
        });
    }

    public void registration(String first, String second){
        if (first.isEmpty() ||second.isEmpty()) {
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.dialog_title);
            builder.setMessage(R.string.dialog_message);
            builder.setPositiveButton(R.string.dialog_ok, null);
            builder.setNegativeButton(R.string.dialog_cancle, null);
            builder.show();
        } else {
            storage.sPref = getSharedPreferences(storage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
            storage.setAccountPreferences(first,second, "", "", "", "","");
            setRegisteredActivity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intentAccount = new Intent(this, UserAccount.class);
            startActivity(intentAccount);
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshAll() {
        //TODO delete alarm
        smart.removeAlarm(nodeDescriptor, alarmUri);
        alarmButton.setBackgroundColor(getResources().getColor(R.color.colorSuperAccent));
    }

    public void onDestroy() {
        moveTaskToBack(true);
        super.onDestroy();

        smart.removeIndividual(nodeDescriptor, locationUri);
        smart.removeIndividual(nodeDescriptor, authUri);
        //TODO delete alarm
        smart.removeIndividual(nodeDescriptor, alarmUri);
        smart.removeIndividual(nodeDescriptor, patientUri);

        smart.disconnectSmartSpace(nodeDescriptor);
        gps.stopUsingGPS();

        System.exit(0);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.petrsu.cardiacare.smartcarepatient/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.petrsu.cardiacare.smartcarepatient/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}