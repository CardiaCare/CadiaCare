package ru.cardiacare.cardiacare.idt_ecg;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
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
import ru.cardiacare.cardiacare.idt_ecg.common.LocationUtils;
import ru.cardiacare.cardiacare.idt_ecg.common.SensorsUtils;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgReceiveHandler;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgBle;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgBleIdt;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgReceiveHandler;

public class BluetoothFindActivity extends AppCompatActivity /*implements ECGService.EcgReceiveHandler*/ {

    public static Context mContext;
    static private LocationUtils location = null;
    private SensorsUtils sensors = null;
    ProgressDialog dialog;
    private BluetoothAdapter myBluetoothAdapter;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;
    boolean bound = false; // Установлено ли подключение к сервису, true - установлено, false - не установлено
    static ServiceConnection sConn;
    static Intent intent;
    static ECGService ecgService;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("QQQ", "BluetoothFindActivity, onCreate()");
        mContext = this;
        this.location = new LocationUtils(this);
        this.sensors = new SensorsUtils(this);
        new File(new StringBuilder(String.valueOf(Environment.getExternalStorageDirectory().toString())).append("/").append("EcgBelt").toString()).mkdirs();
        this.sensors.Start(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_bluetooth_find);

        // Устанавливаем подключение к сервису
        intent = new Intent(this, ECGService.class);
        if (bound == false) {
            sConn = new ServiceConnection() {

                public void onServiceConnected(ComponentName name, IBinder binder) {
                    Log.d("QQQ", "MainActivity onServiceConnected");
                    ecgService = ((ECGService.MyBinder) binder).getService();
                    bound = true;
                }

                public void onServiceDisconnected(ComponentName name) {
                    Log.d("QQQ", "MainActivity onServiceDisconnected");
                    bound = false;
                }
            };
            startService(intent);
            bindService(intent, sConn, 0);
        }

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
                    ecgService.doStart();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
}
