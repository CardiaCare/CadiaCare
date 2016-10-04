package ru.cardiacare.cardiacare.hisdocuments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import ru.cardiacare.cardiacare.R;

public class ResultsDoctor extends AppCompatActivity {
    String examinationReason = "Examination reason";
    String visitOrder = "Visit order";
    String diagnoses = "Diagnoses";
    String medications = "Medications";
    Boolean smooking  = false;
    String drinking = "No";
    Float height = new Float(0.0);
    Float weight = new Float(0.0);
    String diseasePredisposition = "Diagnoses";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText etExaminationReason = (EditText) findViewById(R.id.etExaminationReason);
        etExaminationReason.setText(examinationReason);
        EditText etVisitOrder = (EditText) findViewById(R.id.etVisitOrder);
        etVisitOrder.setText(visitOrder);
        EditText etDiagnoses = (EditText) findViewById(R.id.etDiagnoses);
        etDiagnoses.setText(diagnoses);
        EditText etMedications = (EditText) findViewById(R.id.etMedications);
        etMedications.setText(medications);
        EditText etSmooking = (EditText) findViewById(R.id.etSmooking);
        etSmooking.setText(smooking.toString());
        EditText etDrinking = (EditText) findViewById(R.id.etDrinking);
        etDrinking.setText(drinking);
        EditText etHeight = (EditText) findViewById(R.id.etHeight);
        etHeight.setText(height.toString());
        EditText etWeight = (EditText) findViewById(R.id.etWeight);
        etWeight.setText(weight.toString());
        EditText etDiseasePredisposition = (EditText) findViewById(R.id.etDiseasePredisposition);
        etDiseasePredisposition.setText(diseasePredisposition);
    }

}
