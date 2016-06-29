package com.petrsu.cardiacare.smartcarepatient;

import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;

import com.petrsu.cardiacare.smartcare.*;

public class MainActivity extends AppCompatActivity{


    static SmartCareLibrary smart;

    static protected long nodeDescriptor;
    static protected String patientUri;
    static protected String authUri;
    static protected String locationUri;
    static protected String alarmUri;

    // Native code part end

    Toolbar mToolbar;
    Button alarmButton;
    Button nextButton;
    EditText etFirstName;
    EditText etSecondName;
    AccountStorage storage;

    public static boolean registratedState = false;

    protected static final String TAG = "location";

    LocationService gps;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*****************************
         * SS init
         *****************************/
        smart = new SmartCareLibrary();

        nodeDescriptor = smart.connectSmartSpace("X", "78.46.130.194", 10010);
        if (nodeDescriptor == -1){
            return;
        }


        patientUri = smart.initPatient(nodeDescriptor);
        if (patientUri == null){
            return;
        }

        //authUri = initAuthRequest(nodeDescriptor, patientUri);
        //if (authUri == null){
        //    return;
        //}

        //if (getAuthResponce(nodeDescriptor, authUri) != 0){
        //    AlertDialog.Builder builder =
        //            new AlertDialog.Builder(MainActivity.this, R.style.AppCompatAlertDialogStyle);
        //    builder.setTitle(R.string.auth_dialog_title);
         //   builder.setMessage(R.string.auth_dialog_message);
        //    builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
        //        @Override
        //        public void onClick(DialogInterface dialog, int which) {
        //            onDestroy();
        //        }
        //    });
        //    builder.show();
        //}

        locationUri = smart.initLocation(nodeDescriptor,patientUri);
        if (locationUri == null) {
            return;
        }


        /*****************************
         * get lastKnown location
         *****************************/
        gps = new LocationService(MainActivity.this);
        if(gps.canGetLocation() != false) {
            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            smart.sendLocation(nodeDescriptor, patientUri, locationUri, Double.toString(latitude), Double.toString(longitude));
        }else{
            gps.showSettingsAlert();
        }


        //
        storage = new AccountStorage();
        storage.sPref = getSharedPreferences(storage.ACCOUNT_PREFERENCES, MODE_PRIVATE);

        if (storage.getAccountFirstName().isEmpty() || storage.getAccountSecondName().isEmpty()){
           setUnregisteredActivity();
        }else{
            setRegisteredActivity();
        }
    }

    public void setRegisteredActivity(){
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        alarmButton = (Button)findViewById(R.id.alarmButton);
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);
        alarmButton.setWidth(Math.round(metricsB.widthPixels / 2.5f));
        alarmButton.setHeight(metricsB.heightPixels / 5);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alarmButton.setBackgroundColor(0x77a71000);
                alarmUri = smart.sendAlarm(nodeDescriptor, patientUri);
            }
        });


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
            storage.setAccountPreferences(first,second, "", "", "", "");
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

}