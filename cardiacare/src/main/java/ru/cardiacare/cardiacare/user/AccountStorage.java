package ru.cardiacare.cardiacare.user;

import android.content.SharedPreferences;

/* Хранение данных аккаунта */

public class AccountStorage {

    public SharedPreferences sPref;
    public static final String ACCOUNT_PREFERENCES = "accountsettings";

    private static final String ACCOUNT_PREFERENCES_PATIENTID = "id";
    private static final String ACCOUNT_PREFERENCES_DOCTORID = "iddoctor"; // ид лечащего врача
    private static final String ACCOUNT_PREFERENCES_DOCTOREMAIL = "emaildoctor";
    private static final String ACCOUNT_PREFERENCES_DOCTORNAME = "namedoctor";
    private static final String ACCOUNT_PREFERENCES_DOCTORPATR = "patronymicdoctor";
    private static final String ACCOUNT_PREFERENCES_DOCTORSURNAME = "surnamedoctor";
    private static final String ACCOUNT_PREFERENCES_TOKEN = "token"; // Токен доступа, полученный с сервера
    private static final String ACCOUNT_PREFERENCES_EMAIL = "email";
    private static final String ACCOUNT_PREFERENCES_FIRSTNAME = "firstname";
    private static final String ACCOUNT_PREFERENCES_SECONDNAME = "secondname";
    private static final String ACCOUNT_PREFERENCES_PHONENUMBER = "phonenumber";
    private static final String ACCOUNT_PREFERENCES_HEIGHT = "height";
    private static final String ACCOUNT_PREFERENCES_WEIGHT = "weight";
    private static final String ACCOUNT_PREFERENCES_AGE = "age";
    private static final String ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION = "questionnaireversion"; // Версия последней загруженной анкеты
    private static final String ACCOUNT_PREFERENCES_LASTQUESTIONNAIREPASSDATE = "date"; // Дата последнего прохождения периодического опроса
    private static final String ACCOUNT_PREFERENCES_PERIODPASSSERVEY = "time"; // Период прохождения периодического опроса (например, 1 раз в 30 дней), в секундах
    private static final String ACCOUNT_PREFERENCES_PERIODECGSENDING = "ecgtime"; // Период отправки данных с кардиомонитора на сервер, в секундах
    private static final String ACCOUNT_PREFERENCES_ECGFILE = "ecgfile"; // Имя файлов для отправки на сервер
    private static final String ACCOUNT_PREFERENCES_PAGEVIEWONMAINACTIVITY = "pageviewonmainactivity"; // Отображать ли PageView на главном экране
    private static final String ACCOUNT_PREFERENCES_FEEDBACKREFRESH = "feedbackrefresh"; // Сбрасывать ли выбранные ответы после успешной отправки feedback’а на сервер
    private static final String ACCOUNT_PREFERENCES_SYSTOLICBP = "systolicbp"; // Последние 7 значений систолического давления
    private static final String ACCOUNT_PREFERENCES_DIASTOLICBP = "diastolicbp"; // Последние 7 значений диасистолического давления

    private String strId;
    private String strToken;
    private String strEmail;
    private String strFirstName;
    private String strSecondName;
    private String strPhoneNumber;
    private String strHeight;
    private String strWeight;
    private String strAge;
    private String strQuestionnaireVersion;
    private String strLastQuestionnairePassDate;
    private String strPeriodPassServey;
    private String strPeriodECGSending;
    private String strECGFile;
    private Boolean blnPageViewOnMainactivity;
    private Boolean blnFeedbackRefresh;
    private String strSystolicBP;
    private String strDiastolicBP;

    public void setAccountPreferences(String sibName, String sibIp, String sibPort, String patientId, String token, String doctorId, String email, String firstname, String secondname, String phonenumber, String height, String weight, String age, String questionnaireversion, String lastquestionnairepassdate, String periodpassservey, String periodecgsending, String ecgfile, Boolean pageviewonmainactivity, Boolean feedbackrefresh, String systolicbp, String diastolicbp) {
        SharedPreferences.Editor editor = sPref.edit();

        editor.putString(ACCOUNT_PREFERENCES_PATIENTID, patientId);
        editor.putString(ACCOUNT_PREFERENCES_DOCTORID, doctorId);
        editor.putString(ACCOUNT_PREFERENCES_TOKEN, token);
        editor.putString(ACCOUNT_PREFERENCES_EMAIL, email);
        editor.putString(ACCOUNT_PREFERENCES_FIRSTNAME, firstname);
        editor.putString(ACCOUNT_PREFERENCES_SECONDNAME, secondname);
        editor.putString(ACCOUNT_PREFERENCES_PHONENUMBER, phonenumber);
        editor.putString(ACCOUNT_PREFERENCES_HEIGHT, height);
        editor.putString(ACCOUNT_PREFERENCES_WEIGHT, weight);
        editor.putString(ACCOUNT_PREFERENCES_AGE, age);
        editor.putString(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION, questionnaireversion);
        editor.putString(ACCOUNT_PREFERENCES_LASTQUESTIONNAIREPASSDATE, lastquestionnairepassdate);
        editor.putString(ACCOUNT_PREFERENCES_PERIODPASSSERVEY, periodpassservey);
        editor.putString(ACCOUNT_PREFERENCES_PERIODECGSENDING, periodecgsending);
        editor.putString(ACCOUNT_PREFERENCES_ECGFILE, ecgfile);
        editor.putBoolean(ACCOUNT_PREFERENCES_PAGEVIEWONMAINACTIVITY, pageviewonmainactivity);
        editor.putBoolean(ACCOUNT_PREFERENCES_FEEDBACKREFRESH, feedbackrefresh);
        editor.putString(ACCOUNT_PREFERENCES_SYSTOLICBP, systolicbp);
        editor.putString(ACCOUNT_PREFERENCES_DIASTOLICBP, diastolicbp);

        editor.apply();
    }

    public void setVersion(String questionnaireversion) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION, questionnaireversion);
        editor.apply();

        System.out.println("Test! save " + questionnaireversion + " ? " + strQuestionnaireVersion);
    }

    public void setLastQuestionnairePassDate(String lastquestionnairepassdate) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_LASTQUESTIONNAIREPASSDATE, lastquestionnairepassdate);
        editor.apply();
    }

    public void setPeriodPassServey(String periodpassservey) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_PERIODPASSSERVEY, periodpassservey);
        editor.apply();
    }

    public void setPeriodECGSending(String periodecgsending) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_PERIODECGSENDING, periodecgsending);
        editor.apply();
    }

    public void setECGFile(String ecgfile) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_ECGFILE, ecgfile);
        editor.apply();
    }

    public void setPageViewOnMainactivity(Boolean pageviewonmainactivity) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(ACCOUNT_PREFERENCES_PAGEVIEWONMAINACTIVITY, pageviewonmainactivity);
        editor.apply();
    }

    public void setFeedbackRefresh(Boolean feedbackrefresh) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putBoolean(ACCOUNT_PREFERENCES_FEEDBACKREFRESH, feedbackrefresh);
        editor.apply();
    }

    public void setSystolicBP(String systolicbp) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_SYSTOLICBP, systolicbp);
        editor.apply();
    }

    public void setDiastolicBP(String diastolicbp) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_DIASTOLICBP, diastolicbp);
        editor.apply();
    }

    public String getAccountId() {
        if (sPref.contains(ACCOUNT_PREFERENCES_PATIENTID)) {
            strId = sPref.getString(ACCOUNT_PREFERENCES_PATIENTID, "");
        } else strId = "";
        return strId;
    }

    public String getDoctorId() {
        if (sPref.contains(ACCOUNT_PREFERENCES_DOCTORID)) {
            strId = sPref.getString(ACCOUNT_PREFERENCES_DOCTORID, "");
        } else strId = "";
        return strId;
    }

    public String getDoctorEmail() {
        String str;
        if (sPref.contains(ACCOUNT_PREFERENCES_DOCTOREMAIL)) {
            str = sPref.getString(ACCOUNT_PREFERENCES_DOCTOREMAIL, "");
        } else str = "";
        return str;
    }

    public String getDoctorName() {
        String str;
        if (sPref.contains(ACCOUNT_PREFERENCES_DOCTORNAME)) {
            str = sPref.getString(ACCOUNT_PREFERENCES_DOCTORNAME, "");
        } else str = "";
        return str;
    }

    public String getDoctorPatronumic() {
        String str;
        if (sPref.contains(ACCOUNT_PREFERENCES_DOCTORPATR)) {
            str = sPref.getString(ACCOUNT_PREFERENCES_DOCTORPATR, "");
        } else str = "";
        return str;
    }

    public String getDoctorSurname() {
        String str;
        if (sPref.contains(ACCOUNT_PREFERENCES_DOCTORSURNAME)) {
            str = sPref.getString(ACCOUNT_PREFERENCES_DOCTORSURNAME, "");
        } else str = "";
        return str;
    }

    public void setDoctorEmail(String email) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_DOCTOREMAIL, email);
        editor.apply();
    }

    public void setDoctorName(String name) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_DOCTORNAME, name);
        editor.apply();
    }

    public void setDoctorPatronymic(String patronymic) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_DOCTORPATR, patronymic);
        editor.apply();
    }

    public void setDoctorSurname(String surname) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_DOCTORSURNAME, surname);
        editor.apply();
    }


    public String getAccountToken() {
        if (sPref.contains(ACCOUNT_PREFERENCES_TOKEN)) {
            strToken = sPref.getString(ACCOUNT_PREFERENCES_TOKEN, "");
        } else strToken = "";
        return strToken;
    }

    public String getAccountEmail() {
        if (sPref.contains(ACCOUNT_PREFERENCES_EMAIL)) {
            strEmail = sPref.getString(ACCOUNT_PREFERENCES_EMAIL, "");
        } else strEmail = "";
        return strEmail;
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

    public void setQuestionnaireVersion(String version) {
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString(ACCOUNT_PREFERENCES_QUESTIONNAIREVERSION, version);
        editor.apply();

        System.out.println("Test! save " + version + " ? ");
    }

    public String getLastQuestionnairePassDate() {
        if (sPref.contains(ACCOUNT_PREFERENCES_LASTQUESTIONNAIREPASSDATE)) {
            strLastQuestionnairePassDate = sPref.getString(ACCOUNT_PREFERENCES_LASTQUESTIONNAIREPASSDATE, "");
        } else strLastQuestionnairePassDate = "";
        return strLastQuestionnairePassDate;
    }

    public String getPeriodPassServey() {
        if (sPref.contains(ACCOUNT_PREFERENCES_PERIODPASSSERVEY)) {
            strPeriodPassServey = sPref.getString(ACCOUNT_PREFERENCES_PERIODPASSSERVEY, "");
        } else strPeriodPassServey = "";
        return strPeriodPassServey;
    }

    public String getPeriodECGSending() {
        if (sPref.contains(ACCOUNT_PREFERENCES_PERIODECGSENDING)) {
            strPeriodECGSending = sPref.getString(ACCOUNT_PREFERENCES_PERIODECGSENDING, "");
        } else strPeriodECGSending = "";
        return strPeriodECGSending;
    }

    public String getECGFile() {
        if (sPref.contains(ACCOUNT_PREFERENCES_ECGFILE)) {
            strECGFile = sPref.getString(ACCOUNT_PREFERENCES_ECGFILE, "");
        } else strECGFile = "";
        return strECGFile;
    }

    public Boolean getPageViewOnMainactivity() {
        if (sPref.contains(ACCOUNT_PREFERENCES_PAGEVIEWONMAINACTIVITY)) {
            blnPageViewOnMainactivity = sPref.getBoolean(ACCOUNT_PREFERENCES_PAGEVIEWONMAINACTIVITY, false);
        } else blnPageViewOnMainactivity = false;
        return blnPageViewOnMainactivity;
    }

    public Boolean getFeedbackRefresh() {
        if (sPref.contains(ACCOUNT_PREFERENCES_FEEDBACKREFRESH)) {
            blnFeedbackRefresh = sPref.getBoolean(ACCOUNT_PREFERENCES_FEEDBACKREFRESH, false);
        } else blnFeedbackRefresh = false;
        return blnFeedbackRefresh;
    }

    public String getSystolicBP() {
        if (sPref.contains(ACCOUNT_PREFERENCES_SYSTOLICBP)) {
            strSystolicBP = sPref.getString(ACCOUNT_PREFERENCES_SYSTOLICBP, "");
        } else strSystolicBP = "";
        return strSystolicBP;
    }

    public String getDiastolicBP() {
        if (sPref.contains(ACCOUNT_PREFERENCES_DIASTOLICBP)) {
            strDiastolicBP = sPref.getString(ACCOUNT_PREFERENCES_DIASTOLICBP, "");
        } else strDiastolicBP = "";
        return strDiastolicBP;
    }
}