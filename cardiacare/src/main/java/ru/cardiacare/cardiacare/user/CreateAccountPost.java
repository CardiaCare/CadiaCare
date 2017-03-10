package ru.cardiacare.cardiacare.user;


import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.io.IOException;

import ru.cardiacare.cardiacare.MainFragments.FragmentAuthorizationScreen;
import ru.cardiacare.cardiacare.R;

public class CreateAccountPost  extends AsyncTask<JSONObject, String, String> {

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

            String json =  params[0].toString();
            System.out.println("Test! json " + json);
           // String json = "{ \"email\":" + CreateAccountActivity.etLogin.getText().toString();

            RequestBody body = RequestBody.create(JSON, json);

            System.out.println("Test! body " + body.toString());

            Request request = new Request.Builder()
                    .url("http://api.cardiacare.ru/users")
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println("Test! response " + response.body().string());

            System.out.println("Test! request " + request.body().toString());

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("Request", request.body().toString());
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    Log.i("Response", response.body().string());
                }
            });

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

        System.out.println("Test! res create " + result);

        if("false".equals(result)) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(CreateAccountActivity.mContextCreateAccountActivity, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(R.string.dialog_authorization_message)
                    .setTitle(R.string.dialog_authorization_title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_authorization_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
        }else{
            //надо тестить
            FragmentAuthorizationScreen.emailFragmentAuthorizationScreen = CreateAccountActivity.emailCreateAccountActivity;
            FragmentAuthorizationScreen.passwordFragmentAuthorizationScreen = CreateAccountActivity.passwordCreateAccountActivity;
            //должен быть back, но не протестировать
        }
    }
}
