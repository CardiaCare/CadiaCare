package ru.cardiacare.cardiacare.idt_ecg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;

import ru.cardiacare.cardiacare.R;
import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.idt_ecg.common.DateTimeUtl;
import ru.cardiacare.cardiacare.idt_ecg.common.LocationUtils;
import ru.cardiacare.cardiacare.idt_ecg.common.SensorsUtils;
import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgBle;
import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgBleIdt;
import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgReceiveHandler;

public class ECGService extends Service /*implements EcgReceiveHandler*/ {

    static public Context mContext;
    static public ECGService myService;
    static private EcgBle ecg = null;

    static public boolean connected_flag = false; // Установлено ли подключение к монитору, true - установлено, false - не установлено
    long startTime;                               // Время начала работы сервиса
    static public long connectedTime;             // Время подключения к кардиомонитору
    static String pastTime;                       // Прошедшее время с начала работы сервиса
    static public int ecgValue;                   // Значения ЭКГ (с кардиомонитора)
    static public int heartRate = 0;              // Пульс (с кардиомонитора)
    static public int charge = 0;                 // Уровень заряда батареи кардиомонитора
    static public int periodECGSending;           // Период отправки ЭКГ на сервер


    static public NotificationManager notificationManager;
    static public Notification ecgNotification;

    MyBinder binder = new MyBinder();

    Timer pastTimeTimer;
    TimerTask timerTask;
    long timerInterval = 1000;

    static Time beginTime = new Time();
    static public boolean beStarted = false;

    //    static public LocationUtils location = null;
    private SensorsUtils sensors = null;

    public void onCreate() {
        super.onCreate();
        Log.d("ECGService", "ECGService onCreate");
        mContext = this;
        myService = this;
        this.ecg = new EcgBle(MainActivity.activity, EcgBle.bpReceiveHandler);
//        this.location = new LocationUtils(BluetoothFindActivity.activity);
        this.sensors = new SensorsUtils(BluetoothFindActivity.activity);
        new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())).append("/").append("EcgBelt").toString()).mkdirs();
        this.sensors.Start(true);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        pastTimeTimer = new Timer();
        startTime = System.currentTimeMillis(); // Фиксируем время начала работы сервиса
        pastTimeCounter();
        doStart();
        // Если приод отправки ответов на сервер задан пользователем, то отправляем согласно данному периоду
        // Иначе ставим период по умолчанию (1 минута)
        if (!MainActivity.storage.getPeriodECGSending().equals("")) {
            periodECGSending = Integer.parseInt(MainActivity.storage.getPeriodECGSending());
        } else {
            periodECGSending = 60;
            MainActivity.storage.setPeriodECGSending("60");
        }
    }

    public IBinder onBind(Intent arg0) {
        Log.d("ECGService", "ECGService onBind");
        return binder;
    }

    public boolean onUnbind(Intent intent) {
        Log.d("ECGService", "ECGService onUnbind");
//        return super.onUnbind(intent);
        return true;
    }

    public class MyBinder extends Binder {
        public ECGService getService() {
            return ECGService.this;
        }
    }

    public void onDestroy() {
        // При завершении работы сервиса завершаем работу с монитором
        pastTimeTimer.cancel();
        timerTask.cancel();
        doStop();
        Log.d("ECGService", "ECGService onDestroy");
        super.onDestroy();
    }

    static public ECGService returnService() {
        return myService;
    }

    // Счётчик сколько времени работает сервис
    void pastTimeCounter() {
        if (timerTask != null) timerTask.cancel();
        if (timerInterval > 0) {
            timerTask = new TimerTask() {
                public void run() {
                    long totalTime = System.currentTimeMillis() - startTime;
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(totalTime);
                    int pastMinutes = cal.get(Calendar.MINUTE);
                    int pastSeconds = cal.get(Calendar.SECOND);
                    pastTime = pastMinutes + ":" + pastSeconds;
                    totalTime = (System.currentTimeMillis() / 1000) - connectedTime;
                    // Если пора отправлять ответы на сервер и есть что отправлять
                    if ((totalTime >= periodECGSending) && (!MainActivity.storage.getECGFile().equals(""))) {
                        // Если есть доступ к сети
                        if (MainActivity.isNetworkAvailable(mContext)) {
                            // Отправляем данные на сервер
                            Log.d("ECGService", "Отправляем данные на сервер");
                            ECGPost ecgPost = new ECGPost();
                            ecgPost.execute();
                            // Обнуляем файл с данными ЭКГ (или создаём новый и начинаем писать в него?)
                            Log.d("ECGService", "Обнуляем файл с данными ЭКГ");
                            // Индикатор отправки на сервер в SharedPreferences устанавливаем равным ""
                            MainActivity.storage.setECGFile("");
                            connectedTime = System.currentTimeMillis() / 1000;
                        } else {
                            // Устанавливаем индикатор отправки на сервер в SharedPreferences устанавливаем равным "имя_файла"
                            if (!MainActivity.storage.getECGFile().equals("ecgfile")) {
                                MainActivity.storage.setECGFile("ecgfile");
                            }
                        }
                    }
                }
            };
            pastTimeTimer.schedule(timerTask, 1000, timerInterval);
        }
    }

    // Отправка уведомлений с показаниями, полученными с монитора
    static public void sendECGNotification(int ecgValue, int heartrate, int charge) {
        String notificationText = "Показания с монитора: " + ecgValue + "\nПульс: " + heartrate + "\nЗаряд: " + charge + "%\nПрошло: " + pastTime;

        Intent intent = new Intent(mContext, ECGActivity.class);
        PendingIntent pIntentOpenECG = PendingIntent.getActivity(mContext, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(mContext);
        if (connected_flag == false) {
            builder.setContentTitle("CardiaCare. ECG disconnected");
        } else {
            builder.setContentTitle("CardiaCare. ECG connected");
        }
        builder.setTicker("ECG state change");
        builder.setPriority(Notification.PRIORITY_MAX);
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.addAction(R.drawable.ic_launcher, "Открыть ЭКГ",
                pIntentOpenECG);
        builder.build();
        ecgNotification = builder.getNotification();
        ecgNotification = new Notification.BigTextStyle(builder)
                .bigText(notificationText).build();
        ecgNotification.contentIntent = pIntentOpenECG;
        notificationManager.notify(1, ecgNotification); // 1 - это идентификатор уведомления
        BluetoothFindActivity.ecgService.startForeground(1, ecgNotification);
    }

    // Начать получение ЭКГ
    static public boolean doStart() {
        if (!ecg.Start()) {
            return false;
        }
        beginTime.setToNow();
//        ecg.StorageFileName = String.format("%d.cds", new Object[]{Long.valueOf(beginTime.toMillis(true) / 1000)});
//        ecg.StorageFileId = String.format("a%de", new Object[]{Long.valueOf(beginTime.toMillis(true) / 1000)});
        Intent intent = new Intent(mContext, ECGActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
        return true;
    }

    static public boolean doStop() {
//        if (location.isActive()) {
//            location.Stop();
//        }
        if (!ecg.Stop()) {
            return false;
        }
        if (beStarted) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            editor.commit();
            beStarted = false;
        }
        return true;
    }

    // Формирование файлов для отправки на сервер
//    static public void updateOnServer(/*String filename, String path, String fileid, int hr*/) {
//        // Записывать не в JSON, а в файл
//        String ecgdata = EcgBleIdt.getJSONPart();
//        String ecgjson = "{ \"id\":\"1\", \"patient_id\":\"1\", \"data\": {[\"";
//        ecgjson = new StringBuilder(String.valueOf(ecgjson)).append(ecgdata).append("\"]},").toString();
//        ecgjson = new StringBuilder(String.valueOf(ecgjson)).append("\"created_at\":\"09122016\"}").toString();
//        Log.i("ECGBELT", "ECGJSON=" + ecgjson);
//
////        String context = this.location.getJSONPart();
////        String sensdata = this.sensors.getJSONPart();
////        String systemdata = this.sensors.getSystemJSONPart();
////        if (context == "") {
////            context = sensdata;
////        } else if (sensdata != "") {
////            context = new StringBuilder(String.valueOf(context)).append(",").append(sensdata).toString();
////        }
////        String json = "{ \"app\": \"ecgsend\", ";
////        if (context != "") {
////            json = new StringBuilder(String.valueOf(json)).append("\"context\": {").append(context).append("},").toString();
////        }
////        if (systemdata != "") {
////            json = new StringBuilder(String.valueOf(json)).append("\"system\": {").append(systemdata).append("},").toString();
////        }
////        json = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(json)).append("\"object\": {").toString())).append("\"timestamp\": \"").append(DateFormat.format("yyyy-MM-dd'T'HH:mm:ssZ", this.beginTime.toMillis(true))).append("\",").toString())).append("\"utc_offset\": \"").append(DateTimeUtl.getCurrentUTCOffset()).append("\",").toString())).append("\"namespace\": \"ecg\",").toString())).append("\"channels\": \"1\",").toString())).append("\"format\": \"cds\",").toString())).append("\"filename\":\"").append(fileid).append("\",").toString())).append("\"pulse\":\"").append(String.format("%d", new Object[]{Integer.valueOf(hr)})).append("\"").toString())).append("}}").toString();
////        Log.i("ECGBELT", "JSON=" + json);
//    }

//    public void measurementReceived(int heartrate, short[] array, int Frequency) {
//        this.heartRate = heartrate;
//    }
//    public void measurementEnd() {
//        Log.i("ECGBELT", "measurementEnd()");
//        if (this.location.isActive()) {
//            this.location.Stop();
//        }
//        updateOnServer(this.ecg.StorageFileName, "", this.ecg.StorageFileId, this.heartRate);
//    }
//    public void measurementStart(String mac) {
//        this.beStarted = true;
//        this.timerHandler.postDelayed(this.timerRunnable, 500);
//        this.beginTime.setToNow();
//    }
}