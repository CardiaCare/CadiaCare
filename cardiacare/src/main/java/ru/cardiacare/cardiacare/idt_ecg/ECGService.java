package ru.cardiacare.cardiacare.idt_ecg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
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
    int pastHours = 0;                            // Количество часов, прошедшее с начала работы сервиса
    static public int ecgValue;                   // Значения ЭКГ (с кардиомонитора)
    static public int heartRate = 0;              // Пульс (с кардиомонитора)
    static public int charge = 0;                 // Уровень заряда батареи кардиомонитора
    static public int periodECGSending = 0;       // Период отправки ЭКГ на сервер

    static public LinkedList<String> ecgFiles;    // Массив, с названиями неотправленных файлов ЭКГ
    static public BufferedWriter bw;              // Буфер для работы с файлами ЭКГ
    static public String ecgFileName;             // Название текущего файла ЭКГ

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
        connectedTime = System.currentTimeMillis() / 1000;
        pastTimeCounter();
        doStart();
        ecgFiles = new LinkedList<>();
        // Если приод отправки ответов на сервер задан пользователем, то отправляем согласно данному периоду
        if (!MainActivity.storage.getPeriodECGSending().equals("")) {
            periodECGSending = Integer.parseInt(MainActivity.storage.getPeriodECGSending()) * 60;
//            Log.i("ECGService", "Есть период в ЛК, periodECGSending = " + periodECGSending);
        }
        // Если период < 1 минуты и > 5 минут, то ставим период по умолчанию (1 минута)
        if ((periodECGSending < 60) || (periodECGSending > 300)) {
            periodECGSending = 60;
            MainActivity.storage.setPeriodECGSending("1");
//            Log.i("ECGService", "Нет периода в ЛК, periodECGSending = " + periodECGSending);
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
                    if ((pastMinutes == 59) && (pastSeconds == 59)) {
                        pastHours++;
                    }
                    if (pastHours > 0) {
                        pastTime = pastHours + ":" + pastMinutes + ":" + pastSeconds;
                    } else {
                        pastTime = pastMinutes + ":" + pastSeconds;
                    }
                    totalTime = (System.currentTimeMillis() / 1000) - connectedTime;
//                    Log.i("ECGService", "totalTime = " + totalTime + ", periodECGSending = " + periodECGSending);
                    // Если пора отправлять ответы на сервер
                    if (totalTime >= periodECGSending) {
//                        Log.i("ECGService", "ТАЙМЕР");
                        // Закрываю файл
                        if (bw != null) {
                            try {
                                bw.close();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        // Создаю новый файл и продолжаем запись данных ЭКГ уже в него
                        ecgFileName = "ecg" + System.currentTimeMillis();
//                        Log.i("EcgBleIdt", "ecgFileName = " + ecgFileName);
                        ecgFiles.add(ecgFileName);
//                        Log.i("EcgBleIdt", "ecgFiles = " + ecgFiles.toString());
                        try {
//                            Log.i("ECGService", "Создаём буффер, TRY");
                            bw = new BufferedWriter(new OutputStreamWriter(mContext.openFileOutput(ecgFileName, MODE_PRIVATE)));
                        } catch (IOException e) {
//                            Log.i("ECGService", "Создаём буффер, CATCH");
                            e.printStackTrace();
                        }
                        // Если есть доступ к сети
                        if (MainActivity.isNetworkAvailable(mContext)) {
                            // Отправляем файл(ы) на сервер
                            ECGPost ecgPost = new ECGPost();
                            ecgPost.execute();
                        }
                        connectedTime = System.currentTimeMillis() / 1000;
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
}