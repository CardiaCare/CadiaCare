package ru.cardiacare.cardiacare.user;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Регистрация пользователя */

public class UserAccount extends AppCompatActivity {

    EditText etSibName;
    EditText etSibIp;
    EditText etSibPort;
    EditText etEmail;
    EditText etFirstName;
    EditText etSecondName;
    EditText etPhoneNumber;
    EditText etHeight;
    EditText etWeight;
    EditText etAge;
    //    EditText etPeriodPassSurvey;
    EditText etPeriodECGSending;
    CheckBox cbPageViewOnMainactivity;
    CheckBox cbFeedbackRefresh;

    AccountStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.account_activity_toolbar);
        setSupportActionBar(toolbar);

        assert toolbar != null;
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etEmail = (EditText) findViewById(R.id.etEmail);
        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etSecondName = (EditText) findViewById(R.id.etSecondName);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etHeight = (EditText) findViewById(R.id.etHeight);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etAge = (EditText) findViewById(R.id.etAge);
//        etPeriodPassSurvey = (EditText) findViewById(R.id.etPeriodPassSurvey);
        etPeriodECGSending = (EditText) findViewById(R.id.etPeriodECGSending);
        cbPageViewOnMainactivity = (CheckBox) findViewById(R.id.needGraphButton);
        cbFeedbackRefresh = (CheckBox) findViewById(R.id.feedbackRefreshButton);

        storage = new AccountStorage();
        storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if ( item.getItemId() == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onPause() {
        super.onPause();
        storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
        String version = storage.getQuestionnaireVersion();
        String lastquestionnairepassdate = storage.getLastQuestionnairePassDate();
//        String periodpasssurvey = storage.getPeriodPassSurvey();
        String periodecgsending = storage.getPeriodECGSending();
        String ecgfile = storage.getECGFile();
        String systolicbp = storage.getSystolicBP();
        String diastolicbp = storage.getDiastolicBP();
        storage.setAccountPreferences(
                etSibName.getText().toString(),
                etSibIp.getText().toString(),
                etSibPort.getText().toString(),
                "",
                MainActivity.authorization_token,
                MainActivity.authorization_id_patient,
                etEmail.getText().toString(),
                etFirstName.getText().toString(),
                etSecondName.getText().toString(),
                etPhoneNumber.getText().toString(),
                etHeight.getText().toString(),
                etWeight.getText().toString(),
                etAge.getText().toString(),
                version,
                lastquestionnairepassdate,
//                periodpasssurvey,
                periodecgsending,
                ecgfile,
                cbPageViewOnMainactivity.isChecked(),
                cbFeedbackRefresh.isChecked(),
                systolicbp,
                diastolicbp);
    }

    protected void onResume() {
        super.onResume();
        storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
        etEmail.setText(storage.getAccountEmail());
        etFirstName.setText(storage.getAccountFirstName());
        etSecondName.setText(storage.getAccountSecondName());
        etPhoneNumber.setText(storage.getAccountPhoneNumber());
        etHeight.setText(storage.getAccountHeight());
        etWeight.setText(storage.getAccountWeight());
        etAge.setText(storage.getAccountAge());
//        etPeriodPassSurvey.setText(storage.getPeriodPassSurvey());
        etPeriodECGSending.setText(storage.getPeriodECGSending());
        cbPageViewOnMainactivity.setChecked(storage.getPageViewOnMainactivity());
        cbFeedbackRefresh.setChecked(storage.getFeedbackRefresh());
    }
}
