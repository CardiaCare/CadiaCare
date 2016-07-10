package com.petrsu.cardiacare.smartcarepatient;

import android.content.SharedPreferences;

/* Хранение данных аккаунта */

public class AccountStorage {

    SharedPreferences sPref;
    public static final String ACCOUNT_PREFERENCES = "accountsettings";

    public static final String ACCOUNT_PREFERENCES_FIRSTNAME = "firstname";
    public static final String ACCOUNT_PREFERENCES_SECONDNAME = "secondname";
    public static final String ACCOUNT_PREFERENCES_PHONENUMBER = "phonenumber";
    public static final String ACCOUNT_PREFERENCES_HEIGHT = "height";
    public static final String ACCOUNT_PREFERENCES_WEIGHT = "weight";
    public static final String ACCOUNT_PREFERENCES_AGE = "age";
    public static final String ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION = "questionnaireversion"; //Версия последней загруженной анкеты

    String strFirstName;
    String strSecondName;
    String strPhoneNumber;
    String strHeight;
    String strWeight;
    String strAge;
    String strQuestionnaireVersion;

    public void setAccountPreferences(String firstname, String secondname, String phonenumber, String height, String weight, String age, String questionnaireversion) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_FIRSTNAME, firstname);
        editor.putString(ACCOUNT_PREFERENCES_SECONDNAME, secondname);
        editor.putString(ACCOUNT_PREFERENCES_PHONENUMBER, phonenumber);
        editor.putString(ACCOUNT_PREFERENCES_HEIGHT, height);
        editor.putString(ACCOUNT_PREFERENCES_WEIGHT, weight);
        editor.putString(ACCOUNT_PREFERENCES_AGE, age);
        editor.putString(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION, questionnaireversion);
        editor.apply();
    }

    public void setVersion(String questionnaireversion) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION, questionnaireversion);
        editor.apply();
    }

    public String getAccountFirstName() {
        if (sPref.contains(ACCOUNT_PREFERENCES_FIRSTNAME)) {
            strFirstName = sPref.getString(ACCOUNT_PREFERENCES_FIRSTNAME, "");
        } else strFirstName = "";
        return strFirstName;
    }

    public String getAccountSecondName() {
        if (sPref.contains(ACCOUNT_PREFERENCES_SECONDNAME)) {
            strSecondName = sPref.getString(ACCOUNT_PREFERENCES_SECONDNAME, "");
        } else strSecondName = "";
        return strSecondName;
    }

    public String getAccountPhoneNumber() {
        if (sPref.contains(ACCOUNT_PREFERENCES_PHONENUMBER)) {
            strPhoneNumber = sPref.getString(ACCOUNT_PREFERENCES_PHONENUMBER, "");
        } else strPhoneNumber = "";
        return strPhoneNumber;
    }

    public String getAccountHeight() {
        if (sPref.contains(ACCOUNT_PREFERENCES_HEIGHT)) {
            strHeight = sPref.getString(ACCOUNT_PREFERENCES_HEIGHT, "");
        } else strHeight = "";
        return strHeight;
    }

    public String getAccountWeight() {
        if (sPref.contains(ACCOUNT_PREFERENCES_WEIGHT)) {
            strWeight = sPref.getString(ACCOUNT_PREFERENCES_WEIGHT, "");
        } else strWeight = "";
        return strWeight;
    }

    public String getAccountAge() {
        if (sPref.contains(ACCOUNT_PREFERENCES_AGE)) {
            strAge = sPref.getString(ACCOUNT_PREFERENCES_AGE, "");
        } else strAge = "";
        return strAge;
    }

    public String getQuestionnaireVersion() {
        if (sPref.contains(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION)) {
            strQuestionnaireVersion = sPref.getString(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION, "");
        } else strQuestionnaireVersion = "";
        return strQuestionnaireVersion;
    }
}