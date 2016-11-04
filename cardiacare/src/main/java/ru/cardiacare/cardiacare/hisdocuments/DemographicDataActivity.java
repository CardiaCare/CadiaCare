package ru.cardiacare.cardiacare.hisdocuments;

/**
 * Created by Iuliia Zavialova on 07.10.16.
 */


import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import com.petrsu.cardiacare.smartcare.hisdocuments.DemographicData;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;


public class DemographicDataActivity extends AppCompatActivity {

    String searchstring = null;
    String fieldName = null;
    String dateFrom = null;
    String dateTo = null;

    static public String hisRequestUri;
    static public String hisResponseUri;
    static public String hisDocumentUri;

    DemographicData dd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_demographicdata);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        String hisDocumentType = "http://oss.fruct.org/smartcare#DemographicData";


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

        dd = new DemographicData("name", "surname", "patronymic", "birthDate", "sex",
                                "residence", "contactInformation");
        dd = MainActivity.smart.getHisDemographicData (MainActivity.nodeDescriptor, hisDocumentUri);


        EditText etName = (EditText) findViewById(R.id.etName);
        etName.setText(dd.getPatientName());
        EditText etSurname = (EditText) findViewById(R.id.etSurname);
        etSurname.setText(dd.getSurname());
        EditText etPatronymic = (EditText) findViewById(R.id.etPatronymic);
        etPatronymic.setText(dd.getPatronymic());
        EditText etBirthDate = (EditText) findViewById(R.id.etBirthDate);
        etBirthDate.setText(dd.getBirthDate());
        EditText etSex = (EditText) findViewById(R.id.etSex);
        etSex.setText(dd.getSex());
        EditText etResidence = (EditText) findViewById(R.id.etResidence);
        etResidence.setText(dd.getResidence());
        EditText etContactInformation = (EditText) findViewById(R.id.etContactInformation);
        etContactInformation.setText(dd.getContactInformation());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisDocumentUri);
//        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisResponseUri);
//        MainActivity.smart.removeHisRequest(MainActivity.nodeDescriptor, DocumentsActivity.hisUri, hisRequestUri);
//        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisRequestUri);
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
        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisDocumentUri);
        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisResponseUri);
        MainActivity.smart.removeHisRequest(MainActivity.nodeDescriptor, DocumentsActivity.hisUri, hisRequestUri);
        MainActivity.smart.removeIndividual(MainActivity.nodeDescriptor, hisRequestUri);
        if (MainActivity.backgroundFlag == 0) {
            MainActivity.DisconnectFromSmartSpace();
        }
    }
    @Override
    public void onBackPressed() {
        MainActivity.backgroundFlag = 1;
        super.onBackPressed();
    }
}
