/*
 * Copyright (c) 2014, Petrozavodsk State University
 * Copyright (c) 2014, Open Innovations Framework Program FRUCT
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names of the copyright holders nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

package ru.cardiacare.cardiacare;

/**
 * This activity implements the main window of the app, allowing the user to
 * choose appropriate method of ECG signal obtaining.
 *
 * @author Alexander Borodin
 * @author Yulia Zavyalova
 * @since 1.0
 */

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.petrsu.cardiacare.smartcare.servey.Feedback;
import com.petrsu.cardiacare.smartcare.servey.Questionnaire;
import com.petrsu.cardiacare.smartcare.SmartCareLibrary;

import ru.cardiacare.cardiacare.bluetooth.BluetoothFindActivity;
import ru.cardiacare.cardiacare.ecgviewer.ECGActivity;
import ru.cardiacare.cardiacare.hisdocuments.DocumentsActivity;
import ru.cardiacare.cardiacare.location.GPSLoad;
import ru.cardiacare.cardiacare.location.LocationService;
import ru.cardiacare.cardiacare.servey.QuestionnaireHelper;
import ru.cardiacare.cardiacare.user.AccountStorage;
import ru.cardiacare.cardiacare.user.Login;
import ru.cardiacare.cardiacare.user.Userdata;

public class MainActivity extends AppCompatActivity {

    //private static final String TAG = "MainActivity";
    //Button btnStart;
    Button btnDisconnect;
    Button btnCont;
    ListView connectListView;

    private ArrayAdapter<String> connectListArrayAdapter;

    public static boolean connectedState = false;

    // true if user has already logged-in else false
    public static boolean loginState = false;

    static public SmartCareLibrary smart;
    static public long nodeDescriptor = -1;
    static public String patientUri;
    static public String locationUri;
    static protected String alarmUri;

    static public String TAG = "SS-main";
    static public Questionnaire questionnaire;
    static public Feedback feedback;
    static public LocationService gps;
    public int passSurveyButtonClickCount = 0; //количество нажатий на кнопку PASS SURVEY при отключенном интернете
    static public int gpsEnabledFlag = 1; //включена ли передача геоданных, 1 - вкл/0 - выкл
    static public int alarmButtonFlag = 0; //была ли нажата кнопка SOS, 1 - была нажата/0 - не была

    static public AccountStorage storage;

    static public ProgressBar mProgressBar;

    public Context context = this;

    Toolbar mToolbar;
    Button nextButton;
    EditText etFirstName;
    EditText etSecondName;
    SwipeRefreshLayout mSwipeRefreshLayout;
    static public Button QuestionnaireButton;//ля блокировки
    static public Button alarmButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate Main Activity");
        super.onCreate(savedInstanceState);

        smart = new SmartCareLibrary();
        setLoadingActivity();
        feedback = new Feedback("1 test", "Student", "feedback");

        //if (connectedState == false) {
//            setRegisteredActivity();
//        } else {
//            //стартовое окно при подключенном блютус девайсе
//            //FIXME не работает
//            setConnectedToDriverState();
//        }

        if (isNetworkAvailable(this)) {
            locationUri = smart.initLocation(nodeDescriptor, patientUri);
            if (locationUri == null) {
                return;
            }
        } else {
            setLoadingActivity();
        }

    }

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

            storage = new AccountStorage();
            storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);

            if (storage.getAccountFirstName().isEmpty() || storage.getAccountSecondName().isEmpty()) {
                setUnregisteredActivity();
            } else {
                setRegisteredActivity();
            }
        } else {
            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this);
            alertDialog.setTitle("Отсутствует подключение к сети");
            alertDialog.setMessage("Включите wifi и перезапустите приложение");
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

    // Интерфейс для незарегистрированного пользователя
    public void setUnregisteredActivity() {
        setContentView(R.layout.activity_main_account_connection);
        Log.i(TAG, "setUnregisteredActivity see");

        patientUri = smart.initPatient(nodeDescriptor);
        if (patientUri == null) {
            return;
        }

        //mToolbar = (Toolbar) findViewById(R.id.toolbar);//нужен ли?
        //setSupportActionBar(mToolbar);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registration(etFirstName.getText().toString(), etSecondName.getText().toString());
            }
        });
    }

    // Регистрация
    public void registration(String first, String second) {
        if (first.isEmpty() || second.isEmpty()) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this, R.style.AppBaseTheme);
            builder.setTitle(R.string.dialog_title);
            builder.setMessage(R.string.dialog_message);
            builder.setPositiveButton(R.string.dialog_ok, null);
            builder.setNegativeButton(R.string.dialog_cancle, null);
            builder.show();
        } else {
            storage.setAccountPreferences(patientUri, first, second, "", "", "", "", "");
            setRegisteredActivity();
        }
    }

    // Интерфейс для зарегистрированного пользователя
    public void setRegisteredActivity() {
        setContentView(R.layout.main);

        if (patientUri == null) {
            patientUri = storage.getAccountId();
            smart.initPatientWithId(nodeDescriptor, patientUri);
        }
        //registerReceiver(connectReceiver, new IntentFilter(???));

        //btnStart = (Button) findViewById(R.id.start);
        //btnStart.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

        connectListView = (ListView) findViewById(R.id.ConnectListView);

        connectListArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        connectListView.setAdapter(connectListArrayAdapter);
        connectListArrayAdapter.add("Alive Bluetooth Monitor");
        connectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO выбор способа подключения
                Intent intentBluetoothFind = new Intent(getApplicationContext(), BluetoothFindActivity.class);
                //TODO change methods
                startActivity(intentBluetoothFind);
            }
        });


        ImageButton serveyButton = (ImageButton) findViewById(R.id.serveyButton);
        serveyButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                QuestionnaireHelper.showQuestionnaire(context);
            }
        });

        ImageButton docsButton = (ImageButton) findViewById(R.id.docsButton);
        docsButton.setOnClickListener(new ImageButton.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DocumentsActivity.class));
            }
        });


        alarmButton = (Button) findViewById(R.id.alarmButton);
//        Display display = getWindowManager().getDefaultDisplay();
//        DisplayMetrics metricsB = new DisplayMetrics();
//        display.getMetrics(metricsB);

        alarmButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmButton.setEnabled(false);//блокируем от повторного нажатия
                alarmButton.setBackgroundColor(0x77a71000);
                alarmUri = smart.sendAlarm(nodeDescriptor, patientUri);
                alarmButtonFlag = 1;
                ///sosopros
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
                        Toast toast2 = Toast.makeText(context, "SIB reconnect", Toast.LENGTH_SHORT);
                        toast2.show();
                    } while (!flag);
                    QuestionnaireHelper.showQuestionnaire(context);
                }
                ///
            }
        });

        SmartCareLibrary.insertPersonName(nodeDescriptor, patientUri, storage.getAccountFirstName() + " " + storage.getAccountSecondName());

    }

    public void setConnectedToDriverState() {
        setContentView(R.layout.main_connected);
        btnDisconnect = (Button) findViewById(R.id.disconnect);
        btnDisconnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                connectedState = false;
                setRegisteredActivity();
            }
        });

        btnCont = (Button) findViewById(R.id.continueConnection);
        btnCont.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentECG = new Intent(context, ECGActivity.class);
                //TODO change methods
                startActivity(intentECG);
                //startActivityForResult(intent,1);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar_connected);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));

    }

    final BroadcastReceiver connectReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            connectListArrayAdapter.add("Alive Bluetooth Monitor");
            connectListArrayAdapter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy Main Activity");
        // TODO unregisterReceiver(connectReceiver);

        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if ( item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);*/
        switch (item.getItemId()) {
            case R.id.ecg:
                Intent intent4 = new Intent(this, ECGActivity.class);
                startActivity(intent4);
                break;
            case R.id.menuAbout:
                //About about = new About();
                //about.aboutDialog(this);
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            case R.id.passSurvey:
                QuestionnaireHelper.showQuestionnaire(context);
                break;
            case R.id.exitAccount:
                storage.setAccountPreferences("", "", "", "", "", "", "", "");
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                deleteFile("feedback.json");
                break;
            case R.id.menuHelp:
                Intent intent2 = new Intent(this, Help.class);
                startActivity(intent2);
                break;
            case R.id.documentsData:
                startActivity(new Intent(this, DocumentsActivity.class));
                break;

            case R.id.menuUserData:
                //TODO как-то передедать (откуда беруться настройки юзера БД?)
                if (!loginState) {
                    Intent intent3 = new Intent(this, Login.class);
                    startActivity(intent3);
                } else {
                    startActivity(new Intent(this, Userdata.class));
                }
                break;
            /*case R.id.menuExit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Пожалуйста, подтвердите.")
                        .setTitle("Вы действительно хотите выйти?")
                        .setCancelable(true)
                        .setNegativeButton("Нет", null)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        }).show();
                return true;*/
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setLoginState(boolean state) {
        loginState = state;
    }


    @Override
    protected void onResume() {
        super.onResume();
//        if (connectedState == false) {
//            setRegisteredActivity();
//        } else {
//            setConnectedToDriverState();
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("TAG", "onActivityResult ");
        if (data == null) {
            return;
        }
        String adress = data.getStringExtra("adress");
        Log.i("TAG", "adress " + adress);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //openQuitDialog();
    }

    /**
     * TODO
     * FIXME Так делать нежелательно. Кнопка "назад" должна выходить из приложения без дополнительного уведомления пользователя
     * Если нужно что-то выгрузить из памяти или закончить работу перед выходом, есть методы onDestroy, onFinish и т.п.
     * TODO
     */
    private void openQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Пожалуйста, подтвердите.")
                .setTitle("Вы действительно хотите выйти?")
                .setCancelable(true)
                .setNegativeButton("Нет", null)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }).show();
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
        if (nodeDescriptor == -1) {
            return false;
        }
        return true;
    }
}