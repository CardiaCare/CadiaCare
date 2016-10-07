package ru.cardiacare.cardiacare.hisdocuments;

/**
 * Created by Iuliia Zavialova on 07.10.16.
 */

        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.Toolbar;
        import android.widget.EditText;

        import com.petrsu.cardiacare.smartcare.hisdocuments.DemographicData;

        import java.util.Date;

        import ru.cardiacare.cardiacare.MainActivity;
        import ru.cardiacare.cardiacare.R;


public class DemographicDataActivity extends AppCompatActivity {

    String name = "Ivan";
    String surname = "Ivanov";
    String patronymic = "Ivanovich";
    Date birthDate = new Date();
    String sex = "Male";
    String residence = "Petrozavods";
    String contactInformation = "8-800-000-00-00";


    String searchstring;
    String fieldName; String dateFrom;
    String dateTo;

    static public String hisRequestUri;
    static public String hisDocumentUri;

    DemographicData dd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographicdata);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        String hisDocumentType = "http://oss.fruct.org/smartcare#DemographicData";


        hisRequestUri = MainActivity.smart.sendHisRequest(DocumentsActivity.hisSibUri, DocumentsActivity.hisUri, DocumentsActivity.hisPatientUri,
                 hisDocumentType,  searchstring, fieldName,  dateFrom, dateTo);

        hisDocumentUri = MainActivity.smart.getHisResponce(DocumentsActivity.hisSibUri, hisRequestUri);

        dd = new DemographicData("name", "surname", "patronymic", "birthDate", "sex",
                                "residence", "contactInformation");
        dd = MainActivity.smart.getHisDemographicData (DocumentsActivity.hisSibUri, hisDocumentUri);


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

}
