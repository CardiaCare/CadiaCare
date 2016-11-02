package ru.cardiacare.cardiacare.user;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

public class Userdata extends ActionBarActivity {


    SharedPreferences sPref;

    public static final String ACCOUNT_PREFERENCES = "accountsettings";

    public static final String ACCOUNT_PREFERENCES_FIRSTNAME = "firstname";
    public static final String ACCOUNT_PREFERENCES_SECONDNAME = "secondname";
    public static final String ACCOUNT_PREFERENCES_PHONENUMBER = "phonenumber";
    public static final String ACCOUNT_PREFERENCES_HEIGHT = "height";
    public static final String ACCOUNT_PREFERENCES_WEIGHT = "weight";
    public static final String ACCOUNT_PREFERENCES_AGE = "age";

    EditText etFirstName;
    EditText etSecondName;
    EditText etPhoneNumber;
    EditText etHeight;
    EditText etWeight;
    EditText etAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userdata);

        Toolbar toolbar = (Toolbar) findViewById(R.id.account_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_account));

//        // кнопка назад в ActionBar
//        //TODO переход на Main
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        // back button in left side of ActionBar
//        if (getActionBar() != null) {
//            getActionBar().setDisplayHomeAsUpEnabled(true);
//        }

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etSecondName = (EditText) findViewById(R.id.etSecondName);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etHeight = (EditText) findViewById(R.id.etHeight);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etAge = (EditText) findViewById(R.id.etAge);

        sPref = getSharedPreferences(ACCOUNT_PREFERENCES, MODE_ENABLE_WRITE_AHEAD_LOGGING);

    }

    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        SharedPreferences.Editor editor = sPref.edit();

        sPref.edit().putString(ACCOUNT_PREFERENCES_FIRSTNAME, etFirstName.getText().toString()).commit();
        Log.i("TAG", "1 " +  sPref.getString(ACCOUNT_PREFERENCES_FIRSTNAME, ""));
        sPref.edit().putString(ACCOUNT_PREFERENCES_SECONDNAME, etSecondName.getText().toString()).commit();
        Log.i("TAG", "1 " +  sPref.getString(ACCOUNT_PREFERENCES_SECONDNAME, ""));
        sPref.edit().putString(ACCOUNT_PREFERENCES_PHONENUMBER, etPhoneNumber.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_HEIGHT, etHeight.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_WEIGHT, etWeight.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_AGE, etAge.getText().toString()).commit();
        //editor.apply();

        Log.i("TAG", "1 " +  sPref.getString(ACCOUNT_PREFERENCES_FIRSTNAME, ""));
        Log.i("TAG", "1 " +  sPref.getString(ACCOUNT_PREFERENCES_SECONDNAME, ""));
        //editor.apply();
        if (MainActivity.backgroundFlag == 0) {
            MainActivity.DisconnectFromSmartSpace();
        }
    }

    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        sPref = getSharedPreferences(ACCOUNT_PREFERENCES, MODE_ENABLE_WRITE_AHEAD_LOGGING);

        if (sPref.contains(ACCOUNT_PREFERENCES_FIRSTNAME)) {
            Log.i("TAG", "2 " +  sPref.getString(ACCOUNT_PREFERENCES_FIRSTNAME, ""));
            etFirstName.setText(sPref.getString(ACCOUNT_PREFERENCES_FIRSTNAME, ""));
            //Log.i("TAG", "1 " +  etFirstName.getText().toString());
        }

        if (sPref.contains(ACCOUNT_PREFERENCES_SECONDNAME)) {
            Log.i("TAG", "2 " +  sPref.getString(ACCOUNT_PREFERENCES_SECONDNAME, ""));
            etSecondName.setText(sPref.getString(ACCOUNT_PREFERENCES_SECONDNAME, ""));
            //Log.i("TAG", "1 " +  etSecondName.getText().toString());
        }

        if (sPref.contains(ACCOUNT_PREFERENCES_PHONENUMBER)) {
            etPhoneNumber.setText(sPref.getString(ACCOUNT_PREFERENCES_PHONENUMBER, ""));
        }

        if (sPref.contains(ACCOUNT_PREFERENCES_HEIGHT)) {
            etHeight.setText(sPref.getString(ACCOUNT_PREFERENCES_HEIGHT, ""));
        }

        if (sPref.contains(ACCOUNT_PREFERENCES_WEIGHT)) {
            etWeight.setText(sPref.getString(ACCOUNT_PREFERENCES_WEIGHT, ""));
        }

        if (sPref.contains(ACCOUNT_PREFERENCES_AGE)) {
            etAge.setText(sPref.getString(ACCOUNT_PREFERENCES_AGE, ""));
        }
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.backgroundFlag = 0;
        MainActivity.ConnectToSmartSpace();
    }
    @Override
    public void onBackPressed() {
        MainActivity.backgroundFlag = 1;
        super.onBackPressed();
    }
}
