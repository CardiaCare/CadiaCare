package ru.cardiacare.cardiacare.hisdocuments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.petrsu.cardiacare.smartcare.hisdocuments.ResultBloodPressure;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран "Результаты измерения артериального давления" */

public class BloodPressureActivity extends AppCompatActivity {

    ResultBloodPressure rbp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_results_blood);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert toolbar != null;
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.backgroundFlag = 1;
                onBackPressed();
            }
        });

        rbp = new ResultBloodPressure("systolicPressure", "diastolicPressure", "pulse");

        EditText etSystolicPressure = (EditText) findViewById(R.id.etSystolicPressure);
        assert etSystolicPressure != null;
        etSystolicPressure.setText(rbp.getSystolicPressure());
        EditText etDiastolicPressure = (EditText) findViewById(R.id.etDiastolicPressure);
        assert etDiastolicPressure != null;
        etDiastolicPressure.setText(rbp.getDiastolicPressure());
        EditText etPulse = (EditText) findViewById(R.id.etPulse);
        assert etPulse != null;
        etPulse.setText(rbp.getPulse());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        MainActivity.backgroundFlag = 0;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        MainActivity.backgroundFlag = 1;
        super.onBackPressed();
    }
}
