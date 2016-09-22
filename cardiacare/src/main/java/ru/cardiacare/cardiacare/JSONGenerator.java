package ru.cardiacare.cardiacare;

import org.json.JSONObject;

/**
 * Created by Yulia on 20.04.2015.
 */
public class JSONGenerator {

    public JSONObject generateAuthJSON(String username, String password) {

        JSONObject json = new JSONObject();
        try {
            //JSONObject json = new JSONObject();
            json.put("username", username);
            json.put("password", password);
            //JSONObject url = new JSONObject();
            //url.put("url", "api.cardiacare.ru/index.php/user/auth");

            //jsonMain.put("json",json);
            //jsonMain.put("url", url);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return json;
    }

}
