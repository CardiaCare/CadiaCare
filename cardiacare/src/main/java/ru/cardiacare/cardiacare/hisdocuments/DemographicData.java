package ru.cardiacare.cardiacare.hisdocuments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;

import java.util.Date;

import ru.cardiacare.cardiacare.R;

public class DemographicData extends AppCompatActivity {

    String name = "Ivan";
    String surname = "Ivanov";
    String patronymic = "Ivanovich";
    Date birthDate = new Date();
    String sex = "Male";
    String residence = "Petrozavods";
    String contactInformation = "8-800-000-00-00";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demographicdata);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText etName = (EditText) findViewById(R.id.etName);
        etName.setText(name);
        EditText etSurname = (EditText) findViewById(R.id.etSurname);
        etSurname.setText(surname);
        EditText etPatronymic = (EditText) findViewById(R.id.etPatronymic);
        etPatronymic.setText(patronymic);
        EditText etBirthDate = (EditText) findViewById(R.id.etBirthDate);
        etBirthDate.setText(birthDate.toString());
        EditText etSex = (EditText) findViewById(R.id.etSex);
        etSex.setText(sex);
        EditText etResidence = (EditText) findViewById(R.id.etResidence);
        etResidence.setText(residence);
        EditText etContactInformation = (EditText) findViewById(R.id.etContactInformation);
        etContactInformation.setText(contactInformation);
    }

}
