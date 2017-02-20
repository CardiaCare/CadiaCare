package ru.cardiacare.cardiacare.hisdocuments;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.petrsu.cardiacare.smartcare.hisdocuments.ResultDoctorExamination;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Результаты обследования врачом */

public class DoctorExaminationActivity extends AppCompatActivity {

      ResultDoctorExamination rde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_results_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

                rde = new ResultDoctorExamination("Examination reason", "Visit order",
                "Diagnoses", "Medications", "true", "No", "h", "w", "Diagnoses");

        EditText etExaminationReason = (EditText) findViewById(R.id.etExaminationReason);
        assert etExaminationReason != null;
        etExaminationReason.setText(rde.getExaminationReason());
        EditText etVisitOrder = (EditText) findViewById(R.id.etVisitOrder);
        assert etVisitOrder != null;
        etVisitOrder.setText(rde.getVisitOrder());
        EditText etDiagnoses = (EditText) findViewById(R.id.etDiagnoses);
        assert etDiagnoses != null;
        etDiagnoses.setText(rde.getDiagnoses());
        EditText etMedications = (EditText) findViewById(R.id.etMedications);
        assert etMedications != null;
        etMedications.setText(rde.getMedications());
        EditText etSmooking = (EditText) findViewById(R.id.etSmooking);
        assert etSmooking != null;
        etSmooking.setText(rde.getSmooking());
        EditText etDrinking = (EditText) findViewById(R.id.etDrinking);
        assert etDrinking != null;
        etDrinking.setText(rde.getDrinking());
        EditText etHeight = (EditText) findViewById(R.id.etHeight);
        assert etHeight != null;
        etHeight.setText(rde.getHeight());
        EditText etWeight = (EditText) findViewById(R.id.etWeight);
        assert etWeight != null;
        etWeight.setText(rde.getWeight());
        EditText etDiseasePredisposition = (EditText) findViewById(R.id.etDiseasePredisposition);
        assert etDiseasePredisposition != null;
        etDiseasePredisposition.setText(rde.getDiseasePredisposition());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
