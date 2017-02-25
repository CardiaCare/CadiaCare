package ru.cardiacare.cardiacare;

/* Главный экран */

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.petrsu.cardiacare.smartcare.survey.Feedback;

import org.json.JSONObject;

import ru.cardiacare.cardiacare.MainFragments.FragmentAuthorizationScreen;
import ru.cardiacare.cardiacare.MainFragments.FragmentExampleGraph1;
import ru.cardiacare.cardiacare.MainFragments.FragmentExampleGraph2;
import ru.cardiacare.cardiacare.MainFragments.FragmentRegisteredScreenBigIcons;
import ru.cardiacare.cardiacare.MainFragments.FragmentRegisteredScreenSmallIcons;
import ru.cardiacare.cardiacare.ecgviewer_old.ECGActivity;
import ru.cardiacare.cardiacare.idt_ecg.ECGService;
import ru.cardiacare.cardiacare.survey.QuestionnaireHelper;
import ru.cardiacare.cardiacare.user.AccountStorage;
import ru.cardiacare.cardiacare.user.Userdata;

public class MainActivity extends AppCompatActivity {

    static public String TAG = "MainActivity";
    public Context context = this;
    static public Context mContext;
    static public Activity activity;
    static public BluetoothAdapter myBluetoothAdapter;
    static public String authorization_token = "";
    static public String authorization_id_patient = "";
    static public String authorization_id_doctor = "";
    static public AccountStorage storage;
    static public Feedback feedback;
    static public Feedback alarmFeedback;
    Toolbar toolbar;
    ViewPager viewPager;
    static FragmentTransaction fTrans;
    FragmentManager fManager;
    static FragmentRegisteredScreenBigIcons fragmentRegisteredScreenBigIcons;
    static FragmentRegisteredScreenSmallIcons fragmentRegisteredScreenSmallIcons;
    FragmentAuthorizationScreen fragmentAuthorizationScreen;
    FragmentExampleGraph1 fragmentExampleGraph1;
    FragmentExampleGraph2 fragmentExampleGraph2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // Установка ТОЛЬКО вертикальной ориентации. Такая строка должна быть прописана в КАЖДОЙ активности
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        storage = new AccountStorage();
        storage.sPref = getSharedPreferences(AccountStorage.ACCOUNT_PREFERENCES, MODE_PRIVATE);
        feedback = new Feedback("", "Student", "feedback");
        alarmFeedback = new Feedback("", "Student", "alarmFeedback");
        activity = this;
        mContext = this;

        fManager = getSupportFragmentManager();
        fragmentRegisteredScreenSmallIcons = new FragmentRegisteredScreenSmallIcons();
        fragmentRegisteredScreenBigIcons = new FragmentRegisteredScreenBigIcons();
        fragmentExampleGraph1 = new FragmentExampleGraph1();
        fragmentExampleGraph2 = new FragmentExampleGraph2();
        fTrans = fManager.beginTransaction();
        // Если нет токена, то открываем экран авторизации, иначе - экран авторизированного пользователя
        if (storage.getAccountToken().equals("")) {
            fragmentAuthorizationScreen = new FragmentAuthorizationScreen();
            fTrans.add(R.id.frgmCont, fragmentAuthorizationScreen, FragmentAuthorizationScreen.TAG);
        }
        fTrans.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Условия выполняются только для авторизированного пользователя
        if (!storage.getAccountToken().equals("")) {
            setMainScreenForAuthorizedUser();
            // Если файл с данными ЭКГ не был отправлен во время последнего сеанса, то отправляем его
            if ((!storage.getECGFile().equals(""))) {
                if (isNetworkAvailable(context)) {
                    // Отправляем данные на сервер
                    Log.d(TAG, "Отправляем данные на сервер");
                    // Обнуляем файл с данными ЭКГ (или создаём новый и начинаем писать в него?)
                    Log.d(TAG, "Обнуляем файл с данными ЭКГ");
                    // Индикатор отправки на сервер в SharedPreferences устанавливаем равным ""
                    MainActivity.storage.setECGFile("");
                }
            }

            // Если с момента последнего прохождения периодического опроса прошла минута, то
            // делаем иконку опроса красной. Короткий промежуток времени (1 минута) - для демонстрации
            Long timestamp = System.currentTimeMillis() / 1000;
            String ts = timestamp.toString();
            Integer time = Integer.parseInt(ts) - Integer.parseInt(storage.getLastQuestionnairePassDate());
            Integer period;
            // Если приод прохождения опроса задан пользователем, то обновляем согласно данному периоду
            // Иначе ставим период по умолчанию (1 минута)
            if (!storage.getPeriodPassServey().equals("")) {
                period = Integer.parseInt(storage.getPeriodPassServey());
            } else {
                period = 60;
                storage.setPeriodPassServey("60");
            }
            //TODO Восстановить изменение цвета иконки опросника по истечению заданного интервала
//            if (time >= period) {
//                serveyButton.setBackgroundResource(R.drawable.servey);
//            } else {
//                serveyButton.setBackgroundResource(R.drawable.servey_white);
//            }
        }
    }

    @Override
    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        alertDialog.setTitle(R.string.dialog_main_back_title);
        if (isMyServiceRunning(ECGService.class)) {
            alertDialog.setMessage(R.string.dialog_main_back_serviceMessage);
        } else {
            alertDialog.setMessage(R.string.dialog_main_back_message);
        }
        alertDialog.setPositiveButton(R.string.dialog_main_back_positive_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton(R.string.dialog_main_back_negative_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Тулбар
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // О приложении (справка)
            case R.id.menuAbout:
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                break;
            // Пройти опрос
            case R.id.passSurvey:
                if (isNetworkAvailable(context)) {
                    QuestionnaireHelper.showQuestionnaire(context);
                } else {
                    wiFiAlertDialog();
                }
                break;
            // ЭКГ
            case R.id.ecg:
                Intent intent4 = new Intent(this, ECGActivity.class);
                startActivity(intent4);
                break;
            // Учетная запись
            case R.id.menuUserData:
                //TODO Переделать (Откуда берутся настройки юзера БД?)
                startActivity(new Intent(this, Userdata.class));
                break;
            // Документы
//            case R.id.documentsData:
//                if (isNetworkAvailable(context)) {
//                    backgroundFlag = 1;
//                    startActivity(new Intent(this, DocumentsActivity.class));
//                } else {
//                    setLoadingScreen();
//                }
//                break;
            // Выход
            case R.id.exitAccount:
                //TODO Переделать (Как выходить из аккаунта без доступа к сети? Мы можем удалять токен в приложении, но не на сервере.)
                authorization_token = MainActivity.storage.getAccountToken();
                storage.setAccountPreferences("", "", "", "", "", "","", "", "", "", "", "", "", "", "0", "", "", "", false);
                fTrans = fManager.beginTransaction();
                if (fragmentRegisteredScreenBigIcons != null) {
                    fTrans.remove(fragmentRegisteredScreenBigIcons);
                }
                if (viewPager != null) {
                    viewPager.setVisibility(View.GONE);
                    fTrans.remove(fragmentRegisteredScreenSmallIcons);
                }
                fragmentAuthorizationScreen = new FragmentAuthorizationScreen();
                fTrans.add(R.id.frgmCont, fragmentAuthorizationScreen, FragmentAuthorizationScreen.TAG);
                fTrans.commit();
                toolbar.setVisibility(View.GONE);
                deleteFile("feedback.json");
                deleteFile("alarmFeedback.json");
                if (isNetworkAvailable(context)) {
                    DeleteToken deletetoken = new DeleteToken();
                    deletetoken.execute();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {

                case 0:
                    return FragmentExampleGraph1.newInstance();
                case 1:
                    return FragmentExampleGraph2.newInstance();
                default:
                    return FragmentExampleGraph1.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    // Проверка подключения к интернету
    // Если подключение установлено, возвращает True, иначе False
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    // Проверка запущен ли сервис
    // Если сервис работает,  возращает True, иначе False
    public static boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    // Авторизация
    public void authorization(String email, String password) {
        // Если не все поля заполнены, то выводим диалог об ошибке
        if ((email.isEmpty()) || (password.isEmpty())) {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
            builder.setMessage(R.string.dialog_authorization_message)
                    .setTitle(R.string.dialog_authorization_title)
                    .setCancelable(true)
                    .setPositiveButton(R.string.dialog_authorization_positive_button, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    }).show();
            // Если все поля заполнены, формируем запрос на авторизацию и отправляем его на сервер
        } else {
            JSONGenerator jsonGen = new JSONGenerator();
            JSONObject json = jsonGen.generateAuthJSON(email, password);
            AuthorizationService intServ = new AuthorizationService();
            intServ.execute(json);

            // Получаем ответ от сервера
            try {
                authorization_token = intServ.get();
            } catch (Exception e) {
            }

            // Если авторизация успешна, то сохраняем пользовательские данные и открываем основной экран
            if (!authorization_token.equals("error_authorization")) {
                storage.setAccountPreferences("", "", "", authorization_id_patient, authorization_token, authorization_id_doctor,email, "", "", "", "", "", "", "", "0", "", "", "", false);
                fTrans = fManager.beginTransaction();
                fTrans.remove(fragmentAuthorizationScreen);
                fTrans.commit();
                setMainScreenForAuthorizedUser();
                // Если авторизация не успешна, то выводим диалог об ошибке
            } else {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                builder.setMessage(R.string.dialog_authorization_message)
                        .setTitle(R.string.dialog_authorization_title)
                        .setCancelable(true)
                        .setPositiveButton(R.string.dialog_authorization_positive_button, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).show();
            }
        }
    }

    // WiFi диалог
    static public void wiFiAlertDialog() {
        final WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        alertDialog.setTitle(R.string.dialog_wifi_title);
        alertDialog.setMessage(R.string.dialog_wifi_message);
        alertDialog.setPositiveButton(R.string.dialog_wifi_positive_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        wifiManager.setWifiEnabled(true);
                        mContext.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                });
        alertDialog.setNegativeButton(R.string.dialog_wifi_negative_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void setMainScreenForAuthorizedUser() {
        toolbar = (Toolbar) findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setVisibility(View.VISIBLE);
        viewPager = (ViewPager) activity.findViewById(R.id.viewPager);
        fTrans = fManager.beginTransaction();
        // Если установлена галочка "показывать графики" в личном кабинете
        if (storage.getPageViewOnMainactivity()) {
            if (fManager.findFragmentByTag(FragmentRegisteredScreenBigIcons.TAG) != null) {
                fTrans.remove(fragmentRegisteredScreenBigIcons);
            }
            if (fManager.findFragmentByTag(FragmentRegisteredScreenSmallIcons.TAG) == null) {
                fTrans.add(R.id.frgmCont, fragmentRegisteredScreenSmallIcons, FragmentRegisteredScreenSmallIcons.TAG);
                viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
                viewPager.setVisibility(View.VISIBLE);
            }
        } else {
            viewPager.setVisibility(View.GONE);
            if (fManager.findFragmentByTag(FragmentRegisteredScreenSmallIcons.TAG) != null) {
                fTrans.remove(fragmentRegisteredScreenSmallIcons);
            }
            if (fManager.findFragmentByTag(FragmentRegisteredScreenBigIcons.TAG) == null) {
                fTrans.add(R.id.frgmCont, fragmentRegisteredScreenBigIcons, FragmentRegisteredScreenBigIcons.TAG);
            }
        }
        fTrans.commit();
    }
}