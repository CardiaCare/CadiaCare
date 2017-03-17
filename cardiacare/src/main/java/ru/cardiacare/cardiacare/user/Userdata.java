package ru.cardiacare.cardiacare.user;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран "Учётная запись" */

public class Userdata extends AppCompatActivity {

    SharedPreferences sPref;

    public static final String ACCOUNT_PREFERENCES = "accountsettings";

    private static final String ACCOUNT_PREFERENCES_EMAIL = "email";
    public static final String ACCOUNT_PREFERENCES_FIRSTNAME = "firstname";
    public static final String ACCOUNT_PREFERENCES_SECONDNAME = "secondname";

    private static final String ACCOUNT_PREFERENCES_DOCTOREMAIL = "emaildoctor";
    private static final String ACCOUNT_PREFERENCES_DOCTORNAME = "namedoctor";

    public static final String ACCOUNT_PREFERENCES_PHONENUMBER = "phonenumber";
    public static final String ACCOUNT_PREFERENCES_HEIGHT = "height";
    public static final String ACCOUNT_PREFERENCES_WEIGHT = "weight";
    public static final String ACCOUNT_PREFERENCES_AGE = "age";
    //    private static final String ACCOUNT_PREFERENCES_PERIODPASSSERVEY = "time";
    private static final String ACCOUNT_PREFERENCES_PERIODECGSENDING = "ecgtime";
    private static final String ACCOUNT_PREFERENCES_ECGFILE = "ecgfile";
    private static final String ACCOUNT_PREFERENCES_PAGEVIEWONMAINACTIVITY = "pageviewonmainactivity";
    private static final String ACCOUNT_PREFERENCES_FEEDBACKREFRESH = "feedbackrefresh";

    TextView etEmail;
    TextView etFirstName;
    TextView etSecondName;

//    TextView doctorEmail;
//    TextView doctorName;

    EditText etPhoneNumber;
    EditText etHeight;
    EditText etWeight;
    EditText etAge;
    //    EditText etPeriodPassSurvey;
    EditText etPeriodECGSending;
    CheckBox cbPageViewOnMainactivity;
    CheckBox cbFeedbackRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_user_data);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.account_activity_toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etEmail = (TextView) findViewById(R.id.tvEmail);
        etFirstName = (TextView) findViewById(R.id.tvFirstName);
        etSecondName = (TextView) findViewById(R.id.tvSecondName);

//        doctorEmail = (TextView) findViewById(R.id.doctorEmail);
        //doctorEmail.setText(MainActivity.storage.getDoctorEmail());
//        doctorName = (TextView) findViewById(R.id.doctorName);
        //doctorName.setText(MainActivity.storage.getDoctorName() + " " + MainActivity.storage.getDoctorSurname());

        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etHeight = (EditText) findViewById(R.id.etHeight);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etAge = (EditText) findViewById(R.id.etAge);
//        etPeriodPassSurvey = (EditText) findViewById(R.id.etPeriodPassSurvey);
        etPeriodECGSending = (EditText) findViewById(R.id.etPeriodECGSending);
        cbPageViewOnMainactivity = (CheckBox) findViewById(R.id.needGraphButton);
        cbFeedbackRefresh = (CheckBox) findViewById(R.id.feedbackRefreshButton);

        sPref = getSharedPreferences(ACCOUNT_PREFERENCES, MODE_ENABLE_WRITE_AHEAD_LOGGING);

        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.doctors);

        JSONArray jArray = null;
        try {
            jArray = new JSONArray(MainActivity.storage.getDoctors());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (jArray != null) {
            for (int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);

                    String email = oneObject.getString("email");
                    String name = oneObject.getString("name") + " " +
                            oneObject.getString("patronymic") + " " +
                            oneObject.getString("surname");

                    LayoutInflater inflater = getLayoutInflater();
                    View doctors_card = inflater.inflate(R.layout.doctors_card, null);

                    TextView doctorEmail = (TextView) doctors_card.findViewById(R.id.doctorEmail);
                    doctorEmail.setText(email);
                    TextView doctorName = (TextView) doctors_card.findViewById(R.id.doctorName);
                    doctorName.setText(name);

                    mainLayout.addView(doctors_card);

                } catch (JSONException e) {
                }
            }
        }
    }

    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sPref.edit();
//        sPref.edit().putString(ACCOUNT_PREFERENCES_EMAIL, etEmail.getText().toString()).commit();
//        sPref.edit().putString(ACCOUNT_PREFERENCES_FIRSTNAME, etFirstName.getText().toString()).commit();
//        sPref.edit().putString(ACCOUNT_PREFERENCES_SECONDNAME, etSecondName.getText().toString()).commit();
//        sPref.edit().putString(ACCOUNT_PREFERENCES_PHONENUMBER, etPhoneNumber.getText().toString()).commit();
//        sPref.edit().putString(ACCOUNT_PREFERENCES_HEIGHT, etHeight.getText().toString()).commit();
//        sPref.edit().putString(ACCOUNT_PREFERENCES_WEIGHT, etWeight.getText().toString()).commit();
//        sPref.edit().putString(ACCOUNT_PREFERENCES_AGE, etAge.getText().toString()).commit();
//        sPref.edit().putString(ACCOUNT_PREFERENCES_PERIODPASSSERVEY, etPeriodPassSurvey.getText().toString()).commit();
        sPref.edit().putString(ACCOUNT_PREFERENCES_PERIODECGSENDING, etPeriodECGSending.getText().toString()).commit();
        sPref.edit().putBoolean(ACCOUNT_PREFERENCES_PAGEVIEWONMAINACTIVITY, cbPageViewOnMainactivity.isChecked()).commit();
        sPref.edit().putBoolean(ACCOUNT_PREFERENCES_FEEDBACKREFRESH, cbFeedbackRefresh.isChecked()).commit();

        editor.apply();

    }

    protected void onResume() {
        super.onResume();
        sPref = getSharedPreferences(ACCOUNT_PREFERENCES, MODE_ENABLE_WRITE_AHEAD_LOGGING);
        if (sPref.contains(ACCOUNT_PREFERENCES_EMAIL)) {
            etEmail.setText(sPref.getString(ACCOUNT_PREFERENCES_EMAIL, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_FIRSTNAME)) {
            etFirstName.setText(sPref.getString(ACCOUNT_PREFERENCES_FIRSTNAME, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_SECONDNAME)) {
            etSecondName.setText(sPref.getString(ACCOUNT_PREFERENCES_SECONDNAME, ""));
        }
//        if (sPref.contains(ACCOUNT_PREFERENCES_DOCTOREMAIL)) {
//            doctorEmail.setText(sPref.getString(ACCOUNT_PREFERENCES_DOCTOREMAIL, ""));
//        }
//        if (sPref.contains(ACCOUNT_PREFERENCES_DOCTORNAME)) {
//            doctorName.setText(MainActivity.storage.getDoctorName() + " "+ MainActivity.storage.getDoctorPatronumic() + " " + MainActivity.storage.getDoctorSurname());
//        }
//        if (sPref.contains(ACCOUNT_PREFERENCES_PHONENUMBER)) {
//            etPhoneNumber.setText(sPref.getString(ACCOUNT_PREFERENCES_PHONENUMBER, ""));
//        }
//        if (sPref.contains(ACCOUNT_PREFERENCES_HEIGHT)) {
//            etHeight.setText(sPref.getString(ACCOUNT_PREFERENCES_HEIGHT, ""));
//        }
//        if (sPref.contains(ACCOUNT_PREFERENCES_WEIGHT)) {
//            etWeight.setText(sPref.getString(ACCOUNT_PREFERENCES_WEIGHT, ""));
//        }
//        if (sPref.contains(ACCOUNT_PREFERENCES_AGE)) {
//            etAge.setText(sPref.getString(ACCOUNT_PREFERENCES_AGE, ""));
//        }
//        if (sPref.contains(ACCOUNT_PREFERENCES_PERIODPASSSERVEY)) {
//            etPeriodPassSurvey.setText(sPref.getString(ACCOUNT_PREFERENCES_PERIODPASSSERVEY, ""));
//        }
        if (sPref.contains(ACCOUNT_PREFERENCES_PERIODECGSENDING)) {
            etPeriodECGSending.setText(sPref.getString(ACCOUNT_PREFERENCES_PERIODECGSENDING, ""));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_PAGEVIEWONMAINACTIVITY)) {
            cbPageViewOnMainactivity.setChecked(sPref.getBoolean(ACCOUNT_PREFERENCES_PAGEVIEWONMAINACTIVITY, false));
        }
        if (sPref.contains(ACCOUNT_PREFERENCES_FEEDBACKREFRESH)) {
            cbFeedbackRefresh.setChecked(sPref.getBoolean(ACCOUNT_PREFERENCES_FEEDBACKREFRESH, false));
        }
    }
}
