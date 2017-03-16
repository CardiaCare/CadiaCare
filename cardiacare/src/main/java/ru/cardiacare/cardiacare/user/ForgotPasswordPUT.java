package ru.cardiacare.cardiacare.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.MainFragments.FragmentAuthorizationScreen;
import ru.cardiacare.cardiacare.R;

public class ForgotPasswordPUT extends AsyncTask<JSONObject, String, String> {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject... params) {
        String flag = "true";
        try {
            OkHttpClient client = new OkHttpClient();

            String json = params[0].toString();
            System.out.println("Test! json " + json);
            // String json = "{ \"email\":" + CreateAccountActivity.etLogin.getText().toString();

            RequestBody body = RequestBody.create(JSON, json);

            System.out.println("Test! body " + body.toString());

            Request request = new Request.Builder()
                    .url("http://api.cardiacare.ru/user/password")
                    .put(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println("Test! response " + response.body().string());

            System.out.println("Test! request " + request.body().toString());

//            client.newCall(request).enqueue(new Callback() {
//                @Override
//                public void onFailure(Request request, IOException e) {
//                    Log.e("Request", request.body().toString());
//                }
//
//                @Override
//                public void onResponse(Response response) throws IOException {
//                    Log.i("Response", response.body().string());
//                }
//            });

            System.out.println("Test! POST");
        } catch (Exception e) {
            System.out.println("Test! exc " + e.getMessage());
            e.printStackTrace();
            flag = "false";
        }
        return flag;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if ("false".equals(result)) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(ForgotPasswordActivity.mContextForgotPasswordActivity, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(R.string.dialog_authorization_message)
                    .setTitle(R.string.dialog_authorization_title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_authorization_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
        } else {
            FragmentAuthorizationScreen.emailFragmentAuthorizationScreen = ForgotPasswordActivity.emailForgotPasswordActivity;
            FragmentAuthorizationScreen.passwordFragmentAuthorizationScreen = ForgotPasswordActivity.passwordForgotPasswordActivity;
            Intent intent = new Intent(ForgotPasswordActivity.mContextForgotPasswordActivity, MainActivity.class);
            ForgotPasswordActivity.mContextForgotPasswordActivity.startActivity(intent);
        }
    }
}