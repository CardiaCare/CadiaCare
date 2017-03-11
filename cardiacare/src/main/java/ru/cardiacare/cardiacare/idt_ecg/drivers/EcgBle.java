package ru.cardiacare.cardiacare.idt_ecg.drivers;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;
import ru.cardiacare.cardiacare.idt_ecg.ECGActivity;
import ru.cardiacare.cardiacare.idt_ecg.ECGPost;
import ru.cardiacare.cardiacare.idt_ecg.ECGService;

public class EcgBle {

    private static final int REQUEST_ENABLE_BT = 1;
    //    private static final long SCAN_PERIOD = 90000; // reconnect device constant TODO
    private static final long SCAN_PERIOD = 1000;

    static private BluetoothManager bluetoothManager;
    static private BluetoothAdapter mBluetoothAdapter;
    static private BleScanner mBluetoothScanner;

    static public EcgReceiveHandler bpReceiveHandler;
    static private Activity mainActivity;

    static public BluetoothGatt mBluetoothGatt = null;
    static public EcgBleDevice driver = null;

    public EcgBle(Activity activity, EcgReceiveHandler handler) {

        bpReceiveHandler = handler;
        mainActivity = activity;

        bluetoothManager = (BluetoothManager) ECGService.mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

    }

    public boolean Start() {
        if (mBluetoothScanner != null && mBluetoothScanner.isScanning())
            return false;
        return startLeScan();
    }

    public boolean Stop() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            return true;
        }
        if (mBluetoothScanner == null || !mBluetoothScanner.isScanning())
            return false;
        return stopLeScan();
    }

    public boolean isActive() {
        if (mBluetoothGatt != null) return true;

        if (mBluetoothScanner == null || !mBluetoothScanner.isScanning())
            return false;

        return true;
    }

    private boolean startLeScan() {
        Log.i("ECGBELT", "startLeScan()");

        if (mBluetoothScanner == null) {
            mBluetoothScanner = new BleScanner(mBluetoothAdapter, mLeScanCallback);
            mBluetoothScanner.startScanning();

            return true;
        }
        return false;
    }

    private void startDelayedLeScan() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startLeScan();
            }
        }, 60);
    }

    private boolean stopLeScan() {
        if (mBluetoothScanner != null) {
            mBluetoothScanner.stopScanning();
            mBluetoothScanner = null;

            Log.i("ECGBELT", "stopLeScan()");

            return true;
        }
        return false;
    }

    private void checkLeDevice(final BluetoothDevice device) {

        Log.i("ECGBELT", "checkLeDevice()");

        if (device.getName().contains("ECG")) {
            driver = new EcgBleIdt(this, ECGActivity.handler);
        } else
            return;

        mainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopLeScan();
                initLeDevice(device, driver);
            }
        });
    }

    private void initLeDevice(final BluetoothDevice device, final EcgBleDevice driver) {
        mBluetoothGatt = device.connectGatt(mainActivity, false, driver.mGattCallback);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
                    checkLeDevice(device);
                }
            };

    private class BleScanner extends Thread {
        private final BluetoothAdapter bluetoothAdapter;
        private final BluetoothAdapter.LeScanCallback mLeScanCallback;

        private volatile boolean isScanning = false;

        BleScanner(BluetoothAdapter adapter, BluetoothAdapter.LeScanCallback callback) {
            bluetoothAdapter = adapter;
            mLeScanCallback = callback;
        }

        public boolean isScanning() {
            return isScanning;
        }

        public void startScanning() {
            synchronized (this) {
                isScanning = true;
                start();
            }
        }

        public void stopScanning() {
            synchronized (this) {
                isScanning = false;
                bluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (this) {
                        if (!isScanning)
                            break;
                        bluetoothAdapter.startLeScan(mLeScanCallback);
                    }
                    sleep(SCAN_PERIOD);
                }
            } catch (InterruptedException ignore) {
            } finally {
                bluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

    static public void onDeviceDisconnected() {
        mBluetoothGatt = null;
        ECGService.connected_flag = false;

        // Закрываю файл
        if (ECGService.bw != null) {
            try {
                ECGService.bw.close();
//                Log.i("EcgBle", "Закрываем последний файл, TRY");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Если есть доступ к сети
        if (MainActivity.isNetworkAvailable(ECGService.mContext)) {
            // Отправляем файл(ы) на сервер
            ECGPost ecgPost = new ECGPost();
            ecgPost.execute();
        } else {
            // Если доступа к сети нет, но есть неотправленные файлы, записываем их имена в SharedPreferences
            if (ECGService.ecgFiles.size() > 0) {
                MainActivity.storage.setECGFile(ECGService.ecgFiles.toString());
                MainActivity.storage.setECGFile(MainActivity.storage.getECGFile().replace( "[", "" ).replace( "]", "" ));
                ECGService.ecgFiles.clear();
            }
        }

        ECGService.myService.stopSelf();
        ECGService.notificationManager.cancel(1);

        Message message = ECGActivity.mHandler.obtainMessage();
        message.sendToTarget();

        Log.i("ECGBELT", "onDeviceDisconnected");
    }
}