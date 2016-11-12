package ru.cardiacare.cardiacare.hisdocuments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран "Документы" */

public class DocumentsActivity extends AppCompatActivity {

    static public String hisUri;
    static public String hisPatientUri;
//    static public long hisSibUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_documents);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.backgroundFlag = 1;
                onBackPressed();
            }
        });

//        hisSibUri = MainActivity.smart.connectSmartSpace("X", "109.195.115.73", 10010);

        Log.i("docs", MainActivity.nodeDescriptor + "");
        hisUri = MainActivity.smart.getHis(MainActivity.nodeDescriptor);

        if (hisUri == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Нет подключения к МИС")
                    .setTitle("Ошибка подключения")
                    .setCancelable(true)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    }).show();
        }

        hisPatientUri = MainActivity.smart.setHisId(MainActivity.nodeDescriptor, hisUri, MainActivity.patientUri);

        if (hisPatientUri == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Незарегистрированный пользователь")
                    .setTitle("Ошибка подключения")

                    .setCancelable(true)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    }).show();
        }

        Button demographicButton = (Button) findViewById(R.id.demographicData);
        demographicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, DemographicDataActivity.class));
            }
        });

        Button laboratoryButton = (Button) findViewById(R.id.laboratoryStudies);
        laboratoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, LaboratoryStudyActivity.class));
            }
        });

        Button resultsDoctorButton = (Button) findViewById(R.id.resultsDoctor);
        resultsDoctorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.backgroundFlag = 1;
                startActivity(new Intent(DocumentsActivity.this, DoctorExaminationActivity.class));
            }
        });

        Button resultsBloodButton = (Button) findViewById(R.id.resultsBlood);
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
        MainActivity.ConnectToSmartSpace();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (MainActivity.backgroundFlag == 0) {
            MainActivity.DisconnectFromSmartSpace();
        }
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
