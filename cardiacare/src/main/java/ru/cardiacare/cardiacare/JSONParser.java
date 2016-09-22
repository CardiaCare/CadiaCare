package ru.cardiacare.cardiacare;

import org.json.JSONObject;

/**
 * Created by Yulia on 20.04.2015.
 */
public class JSONParser {

    final String LOG_TAG = "myLogs JSONParser";

    public void readAuthResponce(JSONObject reader) {
        try {
            Integer user_id = reader.getInt("user_id");
            String code = reader.getString("code");
            Integer created_at = reader.getInt("created_at");
            Integer type = reader.getInt("type");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



}