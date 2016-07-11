package com.petrsu.cardiacare.smartcarepatient;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
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
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import com.petrsu.cardiacare.smartcare.*;

/* Главный экран */

public class MainActivity extends AppCompatActivity {

    static public SmartCareLibrary smart;
    static public long nodeDescriptor = -1;
    static protected String patientUri;
    static protected String authUri;
    static protected String locationUri;
    static protected String alarmUri;

    static public com.petrsu.cardiacare.smartcarepatient.LocationService gps;
    static String TAG = "SS-main";
    static public Questionnaire questionnaire;
    static protected Feedback feedback;

    public int passSurveyButtonClickCount = 0; //количество нажатий на кнопку PASS SURVEY при отключенном интернете
    static public int gpsEnabledFlag = 1; //включена ли передача геоданных, 1 - вкл/0 - выкл
    static public int alarmButtonFlag = 0; //была ли нажата кнопка SOS, 1 - была нажата/0 - не была

    static public ProgressBar mProgressBar;

    private GoogleApiClient client;

    public Context context = this;

    Toolbar mToolbar;
    Button alarmButton;
    Button nextButton;
    EditText etFirstName;
    EditText etSecondName;
    SwipeRefreshLayout mSwipeRefreshLayout;

    static com.petrsu.cardiacare.smartcarepatient.AccountStorage storage;

    public MainActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        smart = new SmartCareLibrary();
        setLoadingActivity();
        feedback = new Feedback("1 test", "Student", "feedback");////////////////////////////////////
    }

    // Запись в файл
//    public void writeData ( String data ) {
//        try {
//            //FileOutputStream fOut = openFileOutput (filename , MODE_PRIVATE );
//            FileOutputStream fOut = context.openFileOutput("feedback.json", context.MODE_PRIVATE );
//            OutputStreamWriter osw = new OutputStreamWriter(fOut);
//            osw.write(data);
//            osw.flush();
//            osw.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    // Подготовка к работе
    public void setLoadingActivity() {
        setContentView(R.layout.activity_loading);

        ProgressBar mLoadingProgressBar;
        mLoadingProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);
        assert mLoadingProgressBar != null;
        mLoadingProgressBar.setVisibility(View.VISIBLE);

        final Button WifiButton = (Button) findViewById(R.id.WifiButton);
        assert WifiButton != null;
        WifiButton.setVisibility(View.INVISIBLE);
        WifiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingActivity();
            }
        });

        if (isNetworkAvailable(this)) {
            ConnectToSmartSpace();

            GPSLoad gpsLoad = new GPSLoad(context);
            gpsLoad.execute();

            storage = new com.petrsu.cardiacare.smartcarepatient.AccountStorage();
            storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);

            if (storage.getAccountFirstName().isEmpty() || storage.getAccountSecondName().isEmpty()) {
                setUnregisteredActivity();
            } else {
                setRegisteredActivity();
            }
        } else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Отсутствует подключение к сети");
            alertDialog.setMessage("Включите wifi и перезапустите приложение");

            // Setting Icon to Dialog
            // alertDialog.setIcon(R.drawable.ic_launcher);

            alertDialog.setPositiveButton("Перейти к настройкам wifi",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            WifiButton.setVisibility(View.VISIBLE);
                        }
                    });

            alertDialog.setNegativeButton("Перезапустить приложение",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.cancel();
                            setLoadingActivity();
                        }
                    });

            alertDialog.show();
        }
    }

    // Интерфейс для зарегистрированного пользователя
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
                alarmButtonFlag = 1;
            }
        });

//        Button SaveInJsonButton = (Button)findViewById(R.id.SaveInJsonButton);
//        SaveInJsonButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Gson json = new Gson();
//                String jsonStr = json.toJson(questionnaire);
//                //String jsonStr = json.toJson(feedback);
//                System.out.println(jsonStr);
//                writeData(jsonStr);
//            }
//        });

//        Button LoadToJsonButton = (Button)findViewById(R.id.LoadToJsonButton);
//        LoadToJsonButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String jsonFromFile = readSavedData();
//                    Gson json = new Gson();
//                    Questionnaire qst = json.fromJson(jsonFromFile,Questionnaire.class);
//                    questionnaire = qst;
//                    printQuestionnaire(questionnaire);
//            }
//        });

        Button AboutButton = (Button)findViewById(R.id.AboutButton);
        assert AboutButton != null;
        AboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long timestamp = System.currentTimeMillis()/1000;
                String ts = timestamp.toString();
                SmartCareLibrary.sendFeedback(nodeDescriptor, patientUri, ts);
                Intent intentq = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intentq);
            }
        });

        Button QuestionnaireButton = (Button) findViewById(R.id.QuestionnaireButton);
        assert QuestionnaireButton != null;
        QuestionnaireButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.i(TAG, "Клик" + "; Net=" + isNetworkAvailable(context) + "; nodeDescriptor=" + nodeDescriptor);
                if (isNetworkAvailable(context) && (nodeDescriptor != -1)) {
                    //Log.i(TAG, "Есть сеть, норм дескриптор" + "; Net=" + isNetworkAvailable(context) + "; nodeDescriptor=" + nodeDescriptor);
                    QuestionnaireHelper.showQuestionnaire(context);
                } else if (!isNetworkAvailable(context)) {
                    //Log.i(TAG, "Нет сети, k > 0" + "; Net = " + isNetworkAvailable(context) + "; nodeDescriptor = " + nodeDescriptor);
                    smart.disconnectSmartSpace(nodeDescriptor);
                    nodeDescriptor = -1;
                    setLoadingActivity();
                } else if ((!isNetworkAvailable(context)) && (passSurveyButtonClickCount == 0)) {
                    Toast toast = Toast.makeText(getApplicationContext(), "Отсутствует подключение к сети", Toast.LENGTH_SHORT);
                    toast.show();
                    passSurveyButtonClickCount++;
                    smart.disconnectSmartSpace(nodeDescriptor);
                    nodeDescriptor = -1;
                } else if ((isNetworkAvailable(context)) && (nodeDescriptor == -1)) {
                    boolean flag;
                    do {
                        flag = ConnectToSmartSpace();
                        Toast toast2 = Toast.makeText(getApplicationContext(), "SIB reconnect", Toast.LENGTH_SHORT);
                        toast2.show();
                    } while (!flag);
                    QuestionnaireHelper.showQuestionnaire(context);
                }
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        assert mProgressBar != null;
        mProgressBar.setVisibility(View.INVISIBLE);

        storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
        SmartCareLibrary.insertPersonName(nodeDescriptor, patientUri, storage.getAccountFirstName() + " " + storage.getAccountSecondName());
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

    // Интерфейс для незарегистрированного пользователя
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

    // Регистрация
    public void registration(String first, String second) {
        if (first.isEmpty() ||second.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(R.string.dialog_title);
            builder.setMessage(R.string.dialog_message);
            builder.setPositiveButton(R.string.dialog_ok, null);
            builder.setNegativeButton(R.string.dialog_cancle, null);
            builder.show();
        } else {
            storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
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

    // Обновить страницу (свайп сверху вниз). Отменяет сигнал тревоги (нажатие кнопки SOS)
    public void refreshAll() {
        //TODO delete alarm
        if (alarmButtonFlag == 1) {
            smart.removeAlarm(nodeDescriptor, alarmUri);
            alarmButtonFlag = 0;
        }
        alarmButton.setBackgroundResource(R.color.colorSuperAccent);
    }

    public void onDestroy() {
        moveTaskToBack(true);
        super.onDestroy();

        smart.removeIndividual(nodeDescriptor, locationUri);
        smart.removeIndividual(nodeDescriptor, authUri);
        smart.removeIndividual(nodeDescriptor, alarmUri);
        smart.removeIndividual(nodeDescriptor, patientUri);

        smart.disconnectSmartSpace(nodeDescriptor);
        gps.stopUsingGPS();

        System.exit(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.petrsu.cardiacare.smartcarepatient/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.petrsu.cardiacare.smartcarepatient/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    // Проверка подключения к сети (есть или нет)
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    // Подключение к интеллектуальному пространству
    public boolean ConnectToSmartSpace() {
        nodeDescriptor = smart.connectSmartSpace("X", "78.46.130.194", 10010);
        if (nodeDescriptor == -1){
            return false;
        }

        patientUri = smart.initPatient(nodeDescriptor);
        if (patientUri == null){
            return false;
        }

        locationUri = smart.initLocation(nodeDescriptor,patientUri);
        if (locationUri == null) {
            return false;
        }

        return true;
    }
}