package ru.cardiacare.cardiacare.user;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONObject;

import ru.cardiacare.cardiacare.R;

/* Экран "Забыли пароль" */

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText etLogin;
    TextInputLayout TextInputLayout1, TextInputLayout2, TextInputLayout3;
    EditText etPassword;
    EditText etPassword2;
    EditText etInviteCode;
    Button Send, Send2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        etLogin = (EditText) this.findViewById(R.id.etEmail);

        TextInputLayout1 = (TextInputLayout) this.findViewById(R.id.TextInputLayout1);
        etPassword = (EditText) this.findViewById(R.id.etPassword);

        TextInputLayout2  = (TextInputLayout) this.findViewById(R.id.TextInputLayout2);
        etPassword2 = (EditText) this.findViewById(R.id.etPassword2);

        TextInputLayout3  = (TextInputLayout) this.findViewById(R.id.TextInputLayout3);
        etInviteCode = (EditText) this.findViewById(R.id.etInviteCode);

        Send2 = (Button) findViewById(R.id.nextButton);

        Send = (Button) findViewById(R.id.nextButton1);
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                TextInputLayout1.setVisibility(View.VISIBLE);
//                TextInputLayout2.setVisibility(View.VISIBLE);
//                TextInputLayout3.setVisibility(View.VISIBLE);
//                etPassword.setVisibility(View.VISIBLE);
//                etPassword2.setVisibility(View.VISIBLE);
//                etInviteCode.setVisibility(View.VISIBLE);
//                Send2.setVisibility(View.VISIBLE);

                if(emptyCheck(etLogin.getText().toString()))
                    ForgotPassword1(etLogin.getText().toString());
            }
        });

        Send2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emptyCheck2(etPassword.getText().toString(), etPassword2.getText().toString(), etInviteCode.getText().toString()))
                    ForgotPassword2(etPassword.getText().toString(), etPassword2.getText().toString(), etInviteCode.getText().toString());
            }
        });
    }

    void ForgotPassword1(String login) {
        JSONObject json = null;

        String str = "{ \"email\":\"" + login + "\"}";

        try {
            json = new JSONObject(str);

            ForgotPasswordPost forgotPost = new ForgotPasswordPost();
            forgotPost.execute(json);
        }
        catch (Exception e){}
    }

    void ForgotPassword2(String code, String password1, String password2) {
        JSONObject json = null;

        String str = "{ \"password\":\"" + password1 + "\", "
                + "\"code\":\"" + code +"\"}";

        try {
            json = new JSONObject(str);

            ForgotPasswordPUT forgotPut = new ForgotPasswordPUT();
            forgotPut.execute(json);
        }
        catch (Exception e){}
    }

    private boolean emptyCheck(String login) {
        if (login.equals("") || (login.indexOf("@") == -1)) {
            //Toast.makeText(getApplicationContext(), "Please, fill fields", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(R.string.dialog_authorization_message)
                    .setTitle(R.string.dialog_authorization_title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_authorization_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
            return false;
        } else {
            TextInputLayout1.setVisibility(View.VISIBLE);
            TextInputLayout2.setVisibility(View.VISIBLE);
            TextInputLayout3.setVisibility(View.VISIBLE);
            etPassword.setVisibility(View.VISIBLE);
            etPassword2.setVisibility(View.VISIBLE);
            etInviteCode.setVisibility(View.VISIBLE);
            Send2.setVisibility(View.VISIBLE);
            return true;
        }
    }

    private boolean emptyCheck2(String code, String password, String password2) {
        if (password.equals("") || (!password.equals(password2)) || code.equals("")) {
            //Toast.makeText(getApplicationContext(), "Please, fill fields", Toast.LENGTH_LONG).show();
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(R.string.dialog_authorization_message)
                    .setTitle(R.string.dialog_authorization_title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_authorization_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
            return false;
        } else
            return true;
    }
}