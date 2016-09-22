package ru.cardiacare.cardiacare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

/**
 * created by Yamushev Igor on 21.12.14
 * PetrSU, 2014. 22305 group
 */
public class Login extends ActionBarActivity implements OnClickListener{

    EditText etLogin;
    EditText etPassword;
    String UserLogin, UserPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        Toolbar toolbar = (Toolbar) findViewById(R.id.login_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.title_login));

        // кнопка назад в ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // back button in left side of ActionBar
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }


        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);

        Button btEnter = (Button) findViewById(R.id.btEnter);
        btEnter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btEnter:
                extractLogPass();
                break;
            default:
                break;
        }
    }

    private void extractLogPass() {
        UserLogin = etLogin.getText().toString();
        UserPass = etPassword.getText().toString();
        if(etLogin.getText().toString().trim() == "" ) {
            //TODO доделать - для этих логина и пароля получить юзера. Пока заглушка
            startActivity(new Intent(this, Userdata.class));
        }
        else{
            JSONGenerator jsonGen = new JSONGenerator();
            JSONObject json = jsonGen.generateAuthJSON(UserLogin, UserPass);
            //  Log.d("LOG_TAG", json.toString());

            InternetService intServ = new InternetService();
            intServ.execute(json);

            startActivity(new Intent(this, Userdata.class));

        }
    }

    private boolean emptyCheck() {
        if(UserPass.equals("") || UserLogin.equals("")) {
            Toast.makeText(getApplicationContext(), "Please, fill fields", Toast.LENGTH_LONG).show();
            return false;
        } else
            return true;
    }
}