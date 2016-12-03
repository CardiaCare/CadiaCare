package ru.cardiacare.cardiacare.idt_ecg;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import ru.cardiacare.cardiacare.R;
import ru.cardiacare.cardiacare.idt_ecg.common.DateTimeUtl;
import ru.cardiacare.cardiacare.idt_ecg.common.LocationUtils;
import ru.cardiacare.cardiacare.idt_ecg.common.SensorsUtils;
import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgBle;
import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgReceiveHandler;

public class BluetoothFindActivity extends AppCompatActivity implements EcgReceiveHandler {

    public static Context mContext;
    private int HeartRate = 0;
    static public boolean beStarted = false;
    static private LocationUtils location = null;
    static Time beginTime = new Time();
    static private EcgBle ecg = null;
    private SensorsUtils sensors = null;
    ProgressDialog dialog;
    private BluetoothAdapter myBluetoothAdapter;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        this.ecg = new EcgBle(this, this);
        this.location = new LocationUtils(this);
        this.sensors = new SensorsUtils(this);
        new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())).append("/").append("EcgBelt").toString()).mkdirs();
        this.sensors.Start(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_bluetooth_find);

        Intent intent = getIntent();

        dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.bluetoothSearching));

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), R.string.bluetooth_toast1,
                    Toast.LENGTH_LONG).show();
        } else {
            on();
            myTimerExecute();
            myListView = (ListView) findViewById(R.id.mListView);
            BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
            myListView.setAdapter(BTArrayAdapter);
            myListView.setOnItemClickListener(new OnItemClickListener() {


                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    BluetoothFindActivity.doStart();
                }
            });
        }
    }

    protected void onStart() {
        super.onStart();
        this.location.Start(true);
    }

    protected void onStop() {
        this.location.Stop();
        super.onStop();
    }

    private void myTimerExecute() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        };
        timer.schedule(task, 11000);
    }

    public void on() {
        myBluetoothAdapter.startDiscovery();
        dialog.show();
        // Если вернуться стрелочкой "назад" на главный экран с экрана посика устройств,
        // то приложение не упадёт, но выдаст ошибку, указывая на строчку ниже
        registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    // Начать получение ЭКГ
    static public boolean doStart() {
        if (!ecg.Start()) {
            return false;
        }
        beginTime.setToNow();
        ecg.StorageFileName = String.format("%d.cds", new Object[]{Long.valueOf(beginTime.toMillis(true) / 1000)});
        ecg.StorageFileId = String.format("a%de", new Object[]{Long.valueOf(beginTime.toMillis(true) / 1000)});
        Intent intent = new Intent(mContext, ECGActivity.class);
        mContext.startActivity(intent);
        return true;
    }

    static public boolean doStop() {
        if (location.isActive()) {
            location.Stop();
        }
        if (!ecg.Stop()) {
            return false;
        }
        if (beStarted) {
            Editor editor = PreferenceManager.getDefaultSharedPreferences(mContext).edit();
            editor.commit();
            beStarted = false;
        }
        return true;
    }

    // Получение пульса
    public void measurementReceived(int heartrate, short[] array, int Frequency) {
        this.HeartRate = heartrate;
    }

    // Конец получения ЭКГ
    // Вызов функции сборки данных для отправки данных на сервер
    public void measurementEnd() {
//        Log.i("ECGBELT", "measurementEnd()");
        if (this.location.isActive()) {
            this.location.Stop();
        }
        updateOnServer(this.ecg.StorageFileName, "", this.ecg.StorageFileId, this.HeartRate);
    }

    // Начало получения ЭКГ
    public void measurementStart(String mac) {
        this.beStarted = true;
//        this.timerHandler.postDelayed(this.timerRunnable, 500);
        this.beginTime.setToNow();
    }

    // Формирование файлов для отправки на сервер
    public void updateOnServer(String filename, String path, String fileid, int hr) {
        String context = this.location.getJSONPart();
        String sensdata = this.sensors.getJSONPart();
        String systemdata = this.sensors.getSystemJSONPart();
        if (context == "") {
            context = sensdata;
        } else if (sensdata != "") {
            context = new StringBuilder(String.valueOf(context)).append(",").append(sensdata).toString();
        }
        String json = "{ \"app\": \"ecgsend\", ";
        if (context != "") {
            json = new StringBuilder(String.valueOf(json)).append("\"context\": {").append(context).append("},").toString();
        }
        if (systemdata != "") {
            json = new StringBuilder(String.valueOf(json)).append("\"system\": {").append(systemdata).append("},").toString();
        }
        json = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(json)).append("\"object\": {").toString())).append("\"timestamp\": \"").append(DateFormat.format("yyyy-MM-dd'T'HH:mm:ssZ", this.beginTime.toMillis(true))).append("\",").toString())).append("\"utc_offset\": \"").append(DateTimeUtl.getCurrentUTCOffset()).append("\",").toString())).append("\"namespace\": \"ecg\",").toString())).append("\"channels\": \"1\",").toString())).append("\"format\": \"cds\",").toString())).append("\"filename\":\"").append(fileid).append("\",").toString())).append("\"pulse\":\"").append(String.format("%d", new Object[]{Integer.valueOf(hr)})).append("\"").toString())).append("}}").toString();
        Log.i("ECGBELT", "JSON=" + json);
    }
}
