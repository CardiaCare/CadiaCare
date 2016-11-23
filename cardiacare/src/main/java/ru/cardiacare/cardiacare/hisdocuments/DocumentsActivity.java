package ru.cardiacare.cardiacare.hisdocuments;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран "Документы" */

public class DocumentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_documents);
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

//        hisSibUri = MainActivity.smart.connectSmartSpace("X", "109.195.115.73", 10010);

        Button demographicButton = (Button) findViewById(R.id.demographicData);
        assert demographicButton != null;
        demographicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, DemographicDataActivity.class));
            }
        });

        Button laboratoryButton = (Button) findViewById(R.id.laboratoryStudies);
        assert laboratoryButton != null;
        laboratoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, LaboratoryStudyActivity.class));
            }
        });

        Button resultsDoctorButton = (Button) findViewById(R.id.resultsDoctor);
        assert resultsDoctorButton != null;
        resultsDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, DoctorExaminationActivity.class));
            }
        });

        Button resultsBloodButton = (Button) findViewById(R.id.resultsBlood);
        assert resultsBloodButton != null;
        resultsBloodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, BloodPressureActivity.class));
            }
        });
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

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.backgroundFlag = 0;

    }
}
