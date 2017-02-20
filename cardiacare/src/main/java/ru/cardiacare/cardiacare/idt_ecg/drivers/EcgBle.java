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
import android.provider.Settings;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;
import ru.cardiacare.cardiacare.idt_ecg.BluetoothFindActivity;
import ru.cardiacare.cardiacare.idt_ecg.ECGActivity;
import ru.cardiacare.cardiacare.idt_ecg.ECGService;
import ru.cardiacare.cardiacare.idt_ecg.common.LocationUtils;

public class EcgBle {

    private static final int REQUEST_ENABLE_BT = 1;
    //    private static final long SCAN_PERIOD = 90000; // reconnect device constant TODO
    private static final long SCAN_PERIOD = 1000;

    static private BluetoothManager bluetoothManager;
    static private BluetoothAdapter mBluetoothAdapter;
    static private BleScanner mBluetoothScanner;

    static public EcgReceiveHandler bpReceiveHandler;
    static private Activity mainActivity;

    //public String StoragePath;
//    static public String StorageFileName;
//    static public String StorageFileId;

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
//            driver = new EcgBleIdt(this);
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
                    /*synchronized (this) {
                        bluetoothAdapter.stopLeScan(mLeScanCallback);
                    }*/
                }
            } catch (InterruptedException ignore) {
            } finally {
                bluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }
    }

//    static public FileOutputStream storageFile = null;
//    static private FileInputStream storageFile2 = null;


    static public void onDeviceDisconnected() {
        mBluetoothGatt = null;
//        ECGService.updateOnServer(this.ecg.StorageFileName, "", this.ecg.StorageFileId, this.heartRate);
//        ECGService.updateOnServer();
        ECGService.connected_flag = false;
//        ECGService.sendECGNotification(ECGService.ecgValue, ECGService.heartRate, ECGService.charge);
        ECGService.myService.stopSelf();
        ECGService.notificationManager.cancel(1);
        if ((driver != null) && (EcgBleIdt.bw != null)) {
            try {
                EcgBleIdt.bw.write("\"]},\"created_at\":\"17022017\"}");
                EcgBleIdt.bw.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(ECGService.mContext.openFileInput(EcgBleIdt.StorageFileName)));
                String str = "";
                while ((str = br.readLine()) != null) {
                    Log.d("QQQ", "ecgfile = " + str);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Если есть доступ к сети  и есть что отправлять на сервер
        if ((MainActivity.isNetworkAvailable(ECGService.mContext)) /*&& (!MainActivity.storage.getECGFile().equals(""))*/) {
            // Отправляем данные на сервер
            Log.d("ECGService", "Отправляем данные на сервер");
            // Обнуляем файл с данными ЭКГ
            Log.d("ECGService", "Обнуляем файл с данными ЭКГ");
            // Индикатор отправки на сервер в SharedPreferences устанавливаем равным ""
            MainActivity.storage.setECGFile("");
        } else {
            // Устанавливаем индикатор отправки на сервер в SharedPreferences устанавливаем равным "имя_файла"
            MainActivity.storage.setECGFile("ecgfile");
        }

        Log.i("ECGBELT", "onDeviceDisconnected");

//        if (storageFile != null) {
//            try {
//                storageFile.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            storageFile = null;
//
//            mainActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                        bpReceiveHandler.measurementEnd();
//                }
//            });
//        }
    }

//    static public void onEcgReceived(final int HeartRate, final short array[], final int Frequency) {
//
//        if (storageFile == null) {
//            mainActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                        bpReceiveHandler.measurementStart(LocationUtils.wifi.toString());
//                }
//            });
//
//            Log.i("ECGBELT", "openFile: " + StorageFileName);
//
//            try {
//                storageFile = mainActivity.openFileOutput(StorageFileName, Context.MODE_PRIVATE);
//                //storageFile = new FileOutputStream(new File(StoragePath+StorageFileName));
//            } catch (FileNotFoundException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            String hdr = "CardioDump1 ";
//            try {
//                storageFile.write(hdr.getBytes());
//                storageFile.write(intToBytes(Frequency)); // freq
//                storageFile.write(intToBytes(1)); // channels
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            ByteBuffer buffer = ByteBuffer.allocate(array.length * 2);
//            buffer.order(ByteOrder.LITTLE_ENDIAN);
//            buffer.asShortBuffer().put(array);
//            storageFile.write(buffer.array());
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
////                    bpReceiveHandler.measurementReceived(ECGService.heartRate, array, Frequency);
//            }
//        });
//    }
//
//    static public byte[] intToBytes(final int i) {
//        ByteBuffer bb = ByteBuffer.allocate(4);
//        bb.order(ByteOrder.LITTLE_ENDIAN).putInt(i);
//        return bb.array();
//    }
}