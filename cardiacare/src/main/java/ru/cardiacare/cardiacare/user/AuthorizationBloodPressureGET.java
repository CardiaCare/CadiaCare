package ru.cardiacare.cardiacare.user;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.petrsu.cardiacare.smartcare.hisdocuments.ResultBloodPressure;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Credentials;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.hisdocuments.BloodPressureActivity;

/* Получение данных давления при авторизации */

public class AuthorizationBloodPressureGET extends AsyncTask<JSONObject, String, String> {

    private Context context;

    public AuthorizationBloodPressureGET(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(JSONObject... params) {

        String ret = "";
        try {
            OkHttpClient client = new OkHttpClient();

            String credential = Credentials.basic(MainActivity.storage.getAccountToken(), "");

            Request request = new Request.Builder()
                    .url("http://api.cardiacare.ru/patients/" + MainActivity.storage.getAccountId() + "/bloodpressure")
                    .addHeader("Authorization", credential)
                    .build();

            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            switch (ret = response.body().string()) {
            }
            System.out.println("Test! response " + ret);

//            BloodPressureActivity.bp_data2 = new LinkedList<ResultBloodPressure>();
//            BloodPressureActivity.bp_data2.add(new ResultBloodPressure("", "","0", ""));
//
//            //JSONArray jArray = new JSONObject(response.body().string()).getJSONArray("");
//
//            JSONArray jArray = new JSONArray(response.body().string());
//
//            for (int i=0; i < jArray.length(); i++)
//            {
//                try {
//                    JSONObject oneObject = jArray.getJSONObject(i);
//                    // Pulling items from the array
//                    //String oneObjectsItem = oneObject.getString("STRINGNAMEinTHEarray");
//                    //String oneObjectsItem2 = oneObject.getString("anotherSTRINGNAMEINtheARRAY");
//                    BloodPressureActivity.bp_data2.add(new ResultBloodPressure(oneObject.getString("systolic"), oneObject.getString("diastolic"),"0", oneObject.getString("created_at")));
//                } catch (JSONException e) {
//                    // Oops
//                }
//            }

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    //Log.e("Request", request.body().toString());
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
        }
        return ret;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //BloodPressureActivity.bp_data.remove();

        BloodPressureActivity.bp_data.clear();

        //BloodPressureActivity.bp_data = new LinkedList<ResultBloodPressure>();
        //BloodPressureActivity.bp_data.add(new ResultBloodPressure("", "","0", ""));

        //JSONArray jArray = new JSONObject(response.body().string()).getJSONArray("");

        JSONArray jArray = null;
        try {
            jArray = new JSONArray(result);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //for (int i=0; i < jArray.length(); i++)
        for (int i = jArray.length() - 1; i >= 0; i--) {
            try {
                JSONObject oneObject = jArray.getJSONObject(i);
                //String oneObjectsItem = oneObject.getString("STRINGNAMEinTHEarray");
                //String oneObjectsItem2 = oneObject.getString("anotherSTRINGNAMEINtheARRAY");

                String date = oneObject.getString("created_at").substring(0, 16);
                //date = date.substring(0,11) + (Integer.parseInt(date.substring(11,13)) + Integer.parseInt(TimeZone.getTimeZone("GMT").toString())) + date.substring(13,16);

                Calendar mCalendar = new GregorianCalendar();
                TimeZone mTimeZone = mCalendar.getTimeZone();
                int mGMTOffset = mTimeZone.getRawOffset();
                //date = String.valueOf(TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS));
                int sm = Integer.parseInt(String.valueOf(TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS)));

                date = date.substring(0, 11) + (Integer.parseInt(date.substring(11, 13)) - 1 + sm) + date.substring(13, 16);

                BloodPressureActivity.bp_data.add(new ResultBloodPressure(oneObject.getString("systolic"), oneObject.getString("diastolic"), "0", date, Integer.parseInt(oneObject.getString("id"))));
            } catch (JSONException e) {
            }
        }

        if ((MainActivity.systolicBP != null) && (MainActivity.diastolicBP != null)) {
            MainActivity.systolicBP.clear();
            MainActivity.diastolicBP.clear();

            for (int j = 0; j < BloodPressureActivity.bp_data.size(); j++) {
            }

            if (BloodPressureActivity.bp_data.size() >= 7) {
                for (int i = 6; i >= 0; i--) {
                    MainActivity.systolicBP.add(Integer.parseInt(BloodPressureActivity.bp_data.get(i).getSystolicPressure()));
                    MainActivity.diastolicBP.add(Integer.parseInt(BloodPressureActivity.bp_data.get(i).getDiastolicPressure()));
                }
            } else {
                for (int i = BloodPressureActivity.bp_data.size() - 1; i >= 0; i--) {
                    MainActivity.systolicBP.add(Integer.parseInt(BloodPressureActivity.bp_data.get(i).getSystolicPressure()));
                    MainActivity.diastolicBP.add(Integer.parseInt(BloodPressureActivity.bp_data.get(i).getDiastolicPressure()));
                }
                for (int i = BloodPressureActivity.bp_data.size(); i < 7; i++) {
                    MainActivity.systolicBP.add(0);
                    MainActivity.diastolicBP.add(0);
                }
            }
            MainActivity.storage.setSystolicBP(MainActivity.systolicBP.toString());
            MainActivity.storage.setDiastolicBP(MainActivity.diastolicBP.toString());
            if (MainActivity.storage.getPageViewOnMainactivity()) {
                context.startActivity(new Intent(context, MainActivity.class));
            }
        }
    }
}
