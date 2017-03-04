package ru.cardiacare.cardiacare.hisdocuments;

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

public class BloodPressureGET extends AsyncTask<JSONObject, String, String> {

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

            //System.out.println("Test! response " + response.body().string());
            switch (ret = response.body().string()) {
            }
            System.out.println("Test! response " + ret);
//            ////////////////////////////////////////////////////////////////////////////////////////
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
//            ////////////////////////////////////////////////////////////////////////////////////////
            //System.out.println("Test! request " + request.body().toString());

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
        //return null;
        return ret;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        ////////////////////////////////////////////////////////////////////////////////////////
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
                // Pulling items from the array
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
                // Oops
            }
        }
        ////////////////////////////////////////////////////////////////////////////////////////

        BloodPressureActivity.refresh();
    }
}
