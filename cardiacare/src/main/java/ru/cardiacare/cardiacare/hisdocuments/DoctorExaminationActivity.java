package ru.cardiacare.cardiacare.hisdocuments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.petrsu.cardiacare.smartcare.hisdocuments.ResultDoctorExamination;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/**
 * Created by Iuliia Zavialova on 07.10.16.
 */

public class DoctorExaminationActivity extends AppCompatActivity {

    String searchstring = null;
    String fieldName = null;
    String dateFrom = null;
    String dateTo = null;

    static public String hisRequestUri;
    static public String hisDocumentUri;

    ResultDoctorExamination rde;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String hisDocumentType = "http://oss.fruct.org/smartcare#DoctorExamination";


        hisRequestUri = MainActivity.smart.sendHisRequest(MainActivity.nodeDescriptor, DocumentsActivity.hisUri, MainActivity.patientUri,
                hisDocumentType,  searchstring, fieldName,  dateFrom, dateTo);


        hisDocumentUri = MainActivity.smart.getHisResponce(MainActivity.nodeDescriptor, hisRequestUri);


        if (hisDocumentUri == null){

        }

        rde = new ResultDoctorExamination("Examination reason","Visit order",
                "Diagnoses","Medications","true","No","h","w","Diagnoses");
        rde = MainActivity.smart.getHisDoctorExamination(MainActivity.nodeDescriptor, hisDocumentUri);

        EditText etExaminationReason = (EditText) findViewById(R.id.etExaminationReason);
        etExaminationReason.setText(rde.getExaminationReason());
        EditText etVisitOrder = (EditText) findViewById(R.id.etVisitOrder);
        etVisitOrder.setText(rde.getVisitOrder());
        EditText etDiagnoses = (EditText) findViewById(R.id.etDiagnoses);
        etDiagnoses.setText(rde.getDiagnoses());
        EditText etMedications = (EditText) findViewById(R.id.etMedications);
        etMedications.setText(rde.getMedications());
        EditText etSmooking = (EditText) findViewById(R.id.etSmooking);
        etSmooking.setText(rde.getSmooking());
        EditText etDrinking = (EditText) findViewById(R.id.etDrinking);
        etDrinking.setText(rde.getDrinking());
        EditText etHeight = (EditText) findViewById(R.id.etHeight);
        etHeight.setText(rde.getHeight());
        EditText etWeight = (EditText) findViewById(R.id.etWeight);
        etWeight.setText(rde.getWeight());
        EditText etDiseasePredisposition = (EditText) findViewById(R.id.etDiseasePredisposition);
        etDiseasePredisposition.setText(rde.getDiseasePredisposition());
    }

}
