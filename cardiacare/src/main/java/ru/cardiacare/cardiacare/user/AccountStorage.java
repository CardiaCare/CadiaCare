package ru.cardiacare.cardiacare.user;

import android.content.SharedPreferences;

/* Хранение данных аккаунта */

public class AccountStorage {

    public SharedPreferences sPref;
    public static final String ACCOUNT_PREFERENCES = "accountsettings";

    private static final String ACCOUNT_PREFERENCES_PATIENTID = "id";
    private static final String ACCOUNT_PREFERENCES_FIRSTNAME = "firstname";
    private static final String ACCOUNT_PREFERENCES_SECONDNAME = "secondname";
    private static final String ACCOUNT_PREFERENCES_PHONENUMBER = "phonenumber";
    private static final String ACCOUNT_PREFERENCES_HEIGHT = "height";
    private static final String ACCOUNT_PREFERENCES_WEIGHT = "weight";
    private static final String ACCOUNT_PREFERENCES_AGE = "age";
    private static final String ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION = "questionnaireversion"; // Версия последней загруженной анкеты

    private String strId;
    private String strFirstName;
    private String strSecondName;
    private String strPhoneNumber;
    private String strHeight;
    private String strWeight;
    private String strAge;
    private String strQuestionnaireVersion;

    public void setAccountPreferences(String patientId, String firstname, String secondname, String phonenumber, String height, String weight, String age, String questionnaireversion) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_PATIENTID, patientId);
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

    public String getAccountId() {
        if (sPref.contains(ACCOUNT_PREFERENCES_PATIENTID)) {
            strId = sPref.getString(ACCOUNT_PREFERENCES_PATIENTID, "");
        } else strId = "";
        return strId;
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

    String getAccountPhoneNumber() {
        if (sPref.contains(ACCOUNT_PREFERENCES_PHONENUMBER)) {
            strPhoneNumber = sPref.getString(ACCOUNT_PREFERENCES_PHONENUMBER, "");
        } else strPhoneNumber = "";
        return strPhoneNumber;
    }

    String getAccountHeight() {
        if (sPref.contains(ACCOUNT_PREFERENCES_HEIGHT)) {
            strHeight = sPref.getString(ACCOUNT_PREFERENCES_HEIGHT, "");
        } else strHeight = "";
        return strHeight;
    }

    String getAccountWeight() {
        if (sPref.contains(ACCOUNT_PREFERENCES_WEIGHT)) {
            strWeight = sPref.getString(ACCOUNT_PREFERENCES_WEIGHT, "");
        } else strWeight = "";
        return strWeight;
    }

    String getAccountAge() {
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