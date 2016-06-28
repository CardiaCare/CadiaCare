package com.petrsu.cardiacare.smartcarepatient;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class UserAccount extends AppCompatActivity {

    EditText etFirstName;
    EditText etSecondName;
    EditText etPhoneNumber;
    EditText etHeight;
    EditText etWeight;
    EditText etAge;

    AccountStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_account);

        Toolbar toolbar = (Toolbar) findViewById(R.id.account_activity_toolbar);
        setSupportActionBar(toolbar);
        // кнопка назад в ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etSecondName = (EditText) findViewById(R.id.etSecondName);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etHeight = (EditText) findViewById(R.id.etHeight);
        etWeight = (EditText) findViewById(R.id.etWeight);
        etAge = (EditText) findViewById(R.id.etAge);

        storage = new AccountStorage();
        storage.sPref = getSharedPreferences(storage.ACCOUNT_PREFERENCES,  MODE_PRIVATE);


    }

    @Override
    public void onBackPressed() {
        MainActivity.smart.updatePersonName(MainActivity.nodeDescriptor, MainActivity.patientUri, etFirstName.getText() + " "+ etSecondName.getText());

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if ( item.getItemId() == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);*/
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
        // TODO Auto-generated method stub
        super.onPause();
        storage.sPref = getSharedPreferences(storage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
        storage.setAccountPreferences(etFirstName.getText().toString(),
                etSecondName.getText().toString(),
                etPhoneNumber.getText().toString(),
                etHeight.getText().toString(),
                etWeight.getText().toString(),
                etAge.getText().toString());
    }


    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        storage.sPref = getSharedPreferences(storage.ACCOUNT_PREFERENCES,  MODE_PRIVATE);
        etFirstName.setText(storage.getAccountFirstName());

        etSecondName.setText(storage.getAccountSecondName());

        etPhoneNumber.setText(storage.getAccountPhoneNumber());

        etHeight.setText(storage.getAccountHeight());

        etWeight.setText(storage.getAccountWeight());

        etAge.setText(storage.getAccountAge());


    }
}
