package ru.cardiacare.cardiacare.user;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONObject;

import ru.cardiacare.cardiacare.JSONGenerator;
import ru.cardiacare.cardiacare.R;

/* Экран "Создать аккаунт" */

public class CreateAccountActivity extends AppCompatActivity{

    EditText etLogin;
    EditText etPassword;
    EditText etPassword2;
    EditText etInviteCode;
    Button Registration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        etLogin = (EditText) this.findViewById(R.id.etEmail);
        etPassword = (EditText) this.findViewById(R.id.etPassword);
        etPassword2 = (EditText) this.findViewById(R.id.etPassword2);
        etInviteCode = (EditText) this.findViewById(R.id.etInviteCode);

        Registration = (Button) findViewById(R.id.nextButton);
        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extractLogPass(etLogin.getText().toString(), etPassword.getText().toString(), etPassword2.getText().toString(), etInviteCode.getText().toString());
            }
        });

    }

    void extractLogPass(String login, String password, String password2, String code) {
        JSONObject json = null;

//        etLogin = (EditText) this.findViewById(R.id.etLogin);
//        etPassword = (EditText) this.findViewById(R.id.etPassword);
//        etPassword2 = (EditText) this.findViewById(R.id.etPassword2);
//        etInviteCode = (EditText) this.findViewById(R.id.etInviteCode);
//
//        String login = "";
//        login = etLogin.getText().toString();
//        String password = "";
//        password = etPassword.getText().toString();
//        String code = "";
//        code = etInviteCode.getText().toString();

            String str = "{ \"email\":\"" + login + "\", "
                    + "\"password\":\"" + password + "\", "
                    + "\"inviteCode\":\"" + code + "\"}";

        try {
            json = new JSONObject(str);

            CreateAccountPost ecgPost = new CreateAccountPost();
            ecgPost.execute(json);

        }
        catch (Exception e){}
    }
}