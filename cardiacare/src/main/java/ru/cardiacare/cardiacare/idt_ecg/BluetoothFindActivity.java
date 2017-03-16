package ru.cardiacare.cardiacare.idt_ecg;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;

/* Экран "Поиск устройств" */

public class BluetoothFindActivity extends AppCompatActivity {

    public static Context mContext;
    public static Activity activity;
    ProgressDialog dialog;
    private BluetoothAdapter myBluetoothAdapter;
    String mac;
    private ListView myListView;
    String checkedDeviceName;
    private ArrayAdapter<String> BTArrayAdapter;
    boolean bound = false; // Установлено ли подключение к сервису, true - установлено, false - не установлено
    static ServiceConnection sConn;
    static Intent intent;
    static ECGService ecgService;
    static ImageButton buttonRefresh;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Поддерживает устройство работу с BLE или нет
//        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
//            Toast.makeText(this, "Don't support", Toast.LENGTH_SHORT).show();
//            finish();
//        } else Toast.makeText(this, "Support", Toast.LENGTH_SHORT).show();

        Log.i("QQQ", "BluetoothFindActivity, onCreate()");
        mContext = this;
        activity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_bluetooth_find);
        Toolbar toolbar = (Toolbar) findViewById(R.id.bt_find_activity_toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonRefresh = (ImageButton) findViewById(R.id.buttonRefresh);
        buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myBluetoothAdapter.cancelDiscovery();
                BTArrayAdapter.clear();
                myBluetoothAdapter.startDiscovery();
                dialog.show();
                myTimerExecute();
                registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
        });

        // Устанавливаем подключение к сервису
        intent = new Intent(this, ECGService.class);

        dialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(getString(R.string.bluetoothSearching));
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), R.string.bluetooth_toast1,
                    Toast.LENGTH_LONG).show();
        } else {
            on();
            BTArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
            myListView = (ListView) findViewById(R.id.mListView);
            myListView.setAdapter(BTArrayAdapter);

            // Для версии андроида 6 и выше, нужны следующие разрешения
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
                permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
                if (permissionCheck != 0) {
                    this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                }
            }
            myTimerExecute();
//            myListView.setAdapter(BTArrayAdapter);
            myListView.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    boolean isGPS = true;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        final LocationManager manager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            isGPS = false;
                            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                            alertDialog.setTitle(R.string.dialog_gps_title);
                            alertDialog.setMessage(R.string.dialog_gps_message);
                            alertDialog.setPositiveButton(R.string.dialog_gps_positive_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Переход к настройкам GPS
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    mContext.startActivity(intent);
                                }
                            });
                            alertDialog.setNegativeButton(R.string.dialog_gps_negative_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alertDialog.show();
                        } else isGPS = true;
                    }
                    if (isGPS) {
                        MainActivity.myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (MainActivity.myBluetoothAdapter == null || !MainActivity.myBluetoothAdapter.isEnabled()) {
                            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
                            alertDialog.setTitle(R.string.dialog_bluetooth_title);
                            alertDialog.setMessage(R.string.dialog_bluetooth_message);
                            alertDialog.setPositiveButton(R.string.dialog_bluetooth_positive_button,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            MainActivity.myBluetoothAdapter.enable();
                                        }
                                    });

                            alertDialog.setNegativeButton(R.string.dialog_bluetooth_negative_button,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            checkedDeviceName = BTArrayAdapter.getItem(position);
                            if (checkedDeviceName.contains("ECG")) {
                                if (bound == false) {
                                    sConn = new ServiceConnection() {
                                        public void onServiceConnected(ComponentName name, IBinder binder) {
                                            Log.d("QQQ", "MainActivity onServiceConnected");
                                            ecgService = ((ECGService.MyBinder) binder).getService();
                                            bound = true;
//                    ECGService.location.Start(true); // Раньше находилось в onStart()
                                        }

                                        public void onServiceDisconnected(ComponentName name) {
                                            Log.d("QQQ", "MainActivity onServiceDisconnected");
                                            bound = false;
//                    ECGService.location.Stop(); // Раньше находилось в onStop()
                                        }
                                    };
                                    startService(intent);
                                    bindService(intent, sConn, 0);
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.bluetooth_toast6,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });
        }
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void myTimerExecute() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                dialog.dismiss();
            }
        };
        timer.schedule(task, 15000);
    }

    public void on() {
        myBluetoothAdapter.startDiscovery();
        dialog.show();
        registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (BTArrayAdapter.getCount() < 1) {
                    BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    BTArrayAdapter.notifyDataSetChanged();
                } else {
                    boolean flag = true;
                    for (int i = 0; i < BTArrayAdapter.getCount(); i++) {
                        if (device.getAddress().equals(BTArrayAdapter.getItem(i).substring(BTArrayAdapter.getItem(i).length() - 17, BTArrayAdapter.getItem(i).length()))) {
                            flag = false;
                        }
                    }
                    if (flag) {
                        BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                        BTArrayAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        if (bReceiver != null) {
            unregisterReceiver(bReceiver);
        }
        if (bound == true) {
            unbindService(sConn);
        }
    }
}
