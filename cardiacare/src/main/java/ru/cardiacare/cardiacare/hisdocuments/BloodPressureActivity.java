package ru.cardiacare.cardiacare.hisdocuments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.petrsu.cardiacare.smartcare.hisdocuments.ResultBloodPressure;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/**
 * Created by Iuliia Zavialova on 07.10.16.
 */

public class BloodPressureActivity extends AppCompatActivity {


    String searchstring = null;
    String fieldName = null;
    String dateFrom = null;
    String dateTo = null;

    static public String hisRequestUri;
    static public String hisResponseUri;
    static public String hisDocumentUri;

    ResultBloodPressure rbp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results_blood);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String hisDocumentType = "http://oss.fruct.org/smartcare#BloodPressureMeasurement";


        hisRequestUri = MainActivity.smart.sendHisRequest(MainActivity.nodeDescriptor, DocumentsActivity.hisUri, MainActivity.patientUri,
                hisDocumentType,  searchstring, fieldName,  dateFrom, dateTo);
        hisResponseUri = MainActivity.smart.getHisResponce(MainActivity.nodeDescriptor, hisRequestUri);

        if (hisResponseUri == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Нет ответа от сервера")
                    .setTitle("Ошибка подключения")
                    .setCancelable(true)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    }).show();
        }

        hisDocumentUri = MainActivity.smart.getHisDocument(MainActivity.nodeDescriptor, hisResponseUri);

        if (hisDocumentUri == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Нет соотвтетствующего документа")
                    .setTitle("Ошибка подключения")
                    .setCancelable(true)
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    }).show();
        }

        rbp = new ResultBloodPressure("systolicPressure","diastolicPressure", "pulse");
        rbp = MainActivity.smart.getHisBloodPressureResult(MainActivity.nodeDescriptor, hisDocumentUri);



        EditText etSystolicPressure = (EditText) findViewById(R.id.etSystolicPressure);
        etSystolicPressure.setText(rbp.getSystolicPressure());
        EditText etDiastolicPressure = (EditText) findViewById(R.id.etDiastolicPressure);
        etDiastolicPressure.setText(rbp.getDiastolicPressure());
        EditText etPulse = (EditText) findViewById(R.id.etPulse);
        etPulse.setText(rbp.getPulse());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisDocumentUri);
        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisResponseUri);
        MainActivity.smart.removeHisRequest(MainActivity.nodeDescriptor, DocumentsActivity.hisUri, hisRequestUri);
        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisRequestUri);
    }
}
