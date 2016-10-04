package ru.cardiacare.cardiacare.hisdocuments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import ru.cardiacare.cardiacare.R;

public class ResultsBlood extends AppCompatActivity {

    Integer systolicPressure = 0;
    Integer diastolicPressure = 0;
    Integer pulse = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_blood);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText etSystolicPressure = (EditText) findViewById(R.id.etSystolicPressure);
        etSystolicPressure.setText(systolicPressure.toString());
        EditText etDiastolicPressure = (EditText) findViewById(R.id.etDiastolicPressure);
        etDiastolicPressure.setText(diastolicPressure.toString());
        EditText etPulse = (EditText) findViewById(R.id.etPulse);
        etPulse.setText(pulse.toString());
    }

}
