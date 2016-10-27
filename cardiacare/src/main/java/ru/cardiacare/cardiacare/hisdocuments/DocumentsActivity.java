package ru.cardiacare.cardiacare.hisdocuments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

public class DocumentsActivity extends AppCompatActivity {

    static public String hisUri;
    static public String hisPatientUri;
    static public long hisSibUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        hisSibUri = MainActivity.smart.connectSmartSpace("X", "109.195.115.73", 10010);

        hisUri = MainActivity.smart.getHis(hisSibUri);

        hisPatientUri = MainActivity.smart.setHisId(hisSibUri, hisUri);

        Button demographicButton = (Button) findViewById(R.id.demographicData);
        demographicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DocumentsActivity.this, DemographicDataActivity.class));
            }
        });

        Button laboratoryButton = (Button) findViewById(R.id.laboratoryStudies);
        laboratoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DocumentsActivity.this, LaboratoryStudyActivity.class));
            }
        });

        Button resultsDoctorButton = (Button) findViewById(R.id.resultsDoctor);
        resultsDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DocumentsActivity.this, DoctorExaminationActivity.class));
            }
        });

        Button resultsBloodButton = (Button) findViewById(R.id.resultsBlood);
        resultsBloodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DocumentsActivity.this, BloodPressureActivity.class));
            }
        });

    }

}
