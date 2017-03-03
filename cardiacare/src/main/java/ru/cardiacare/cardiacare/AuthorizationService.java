package ru.cardiacare.cardiacare;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.cardiacare.cardiacare.hisdocuments.DoctorGET;

/* Авторизация через сервер */

public class AuthorizationService extends AsyncTask<JSONObject, String, String> {

    public static String result = "";
    public static String token = "";

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject... data) {
        JSONObject json = data[0];
        String url = "http://api.cardiacare.ru/tokens";
        //String url = "http://platov.cardiacare.ru/emr/web/index.php/tokens";
        try {
            URL object = new URL(url);
            HttpURLConnection con = (HttpURLConnection) object.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestMethod("POST");
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
            wr.write(json.toString());
            wr.flush();

            StringBuilder sb = new StringBuilder();
            int HttpResult = con.getResponseCode();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
//            Log.i("LOG", "token = " + sb.toString());
//            Log.i("LOG", "status = " + con.getResponseMessage());
            token = sb.toString();
            result = con.getResponseMessage();
            System.out.println("Test!" + token);
        } catch (Exception e) {}

        /////////////////////////////////////////////
        try {
            JSONObject dataJsonObj = null;
            dataJsonObj = new JSONObject(token);
            token = dataJsonObj.getString("token");

            JSONObject dataUser = null;
            dataUser = new JSONObject(dataJsonObj.getString("user"));

            JSONObject dataPerson = null;
            dataPerson = new JSONObject(dataUser.getString("person"));
            MainActivity.authorization_id_patient = dataPerson.getString("id");

            MainActivity.authorization_name = dataPerson.getString("name");
            MainActivity.authorization_surname = dataPerson.getString("surname");

//            Как появиться поле с идентификатором врача - расскоментировать и проверить поле "doctor" "id" или "person" "doctor_id"
//            JSONObject dataDoctor = null;
//            dataDoctor = new JSONObject(dataUser.getString("doctor"));
//            MainActivity.authorization_id_doctor = dataDoctor.getString("id");

        } catch (Exception e) {}
        /////////////////////////////////

        //System.out.println("Test!" + token + result + json.toString());

        if (result.equals("Created")) {
            return token;
        } else {
            return "error_authorization";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        result = "";
        token = "";

        DoctorGET doctorGET = new DoctorGET();
        doctorGET.execute();
    }
}
