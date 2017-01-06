//package ru.cardiacare.cardiacare.idt_ecg.drivers;
//
//import android.app.Activity;
//import android.bluetooth.BluetoothAdapter;
//import android.bluetooth.BluetoothDevice;
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothManager;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.util.Log;
//
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//
//import ru.cardiacare.cardiacare.idt_ecg.ECGActivity;
//import ru.cardiacare.cardiacare.idt_ecg.ECGService;
//import ru.cardiacare.cardiacare.idt_ecg.common.LocationUtils;
//
//public class EcgBle {
//
//    private static final int REQUEST_ENABLE_BT = 1;
//    //    private static final long SCAN_PERIOD = 90000; // reconnect device constant TODO
//    private static final long SCAN_PERIOD = 1000;
//
//    private BluetoothManager bluetoothManager;
//    private BluetoothAdapter mBluetoothAdapter;
//    private BleScanner mBluetoothScanner;
//
//    private EcgReceiveHandler bpReceiveHandler;
//    private Activity mainActivity;
//
//    //public String StoragePath;
//    public String StorageFileName;
//    public String StorageFileId;
//
//    public BluetoothGatt mBluetoothGatt = null;
//    public EcgBleDevice driver = null;
//
//    public EcgBle(Activity activity, EcgReceiveHandler handler) {
//
//        bpReceiveHandler = handler;
//        mainActivity = activity;
//
//        bluetoothManager = (BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE);
//        mBluetoothAdapter = bluetoothManager.getAdapter();
//
//        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            mainActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//    }
//
//    public boolean Start() {
//        if (mBluetoothScanner != null && mBluetoothScanner.isScanning())
//            return false;
//        return startLeScan();
//    }
//
//    public boolean Stop() {
//        if (mBluetoothGatt != null) {
//            mBluetoothGatt.disconnect();
//            return true;
//        }
//        if (mBluetoothScanner == null || !mBluetoothScanner.isScanning())
//            return false;
//        return stopLeScan();
//    }
//
//    public boolean isActive() {
//        if (mBluetoothGatt != null) return true;
//
//        if (mBluetoothScanner == null || !mBluetoothScanner.isScanning())
//            return false;
//
//        return true;
//    }
//
//    private boolean startLeScan() {
//        Log.i("ECGBELT", "startLeScan()");
//
//        if (mBluetoothScanner == null) {
//            mBluetoothScanner = new BleScanner(mBluetoothAdapter, mLeScanCallback);
//            mBluetoothScanner.startScanning();
//
//            return true;
//        }
//        return false;
//    }
//
//    private void startDelayedLeScan() {
//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startLeScan();
//            }
//        }, 60);
//    }
//
//    private boolean stopLeScan() {
//        if (mBluetoothScanner != null) {
//            mBluetoothScanner.stopScanning();
//            mBluetoothScanner = null;
//
//            Log.i("ECGBELT", "stopLeScan()");
//
//            return true;
//        }
//        return false;
//    }
//
//    private void checkLeDevice(final BluetoothDevice device) {
//
//        Log.i("ECGBELT", "checkLeDevice()");
//
//        if (device.getName().contains("ECG")) {
////            driver = new EcgBleIdt(this);
//            driver = new ECGService.EcgBleIdt(this, ECGActivity.handler);
//        } else
//            return;
//
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                stopLeScan();
//                initLeDevice(device, driver);
//            }
//        });
//    }
//
//    private void initLeDevice(final BluetoothDevice device, final EcgBleDevice driver) {
//        mBluetoothGatt = device.connectGatt(mainActivity, false, driver.mGattCallback);
//    }
//
//    private BluetoothAdapter.LeScanCallback mLeScanCallback =
//            new BluetoothAdapter.LeScanCallback() {
//
//                @Override
//                public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
//                    checkLeDevice(device);
//                }
//            };
//
//    private static class BleScanner extends Thread {
//        private final BluetoothAdapter bluetoothAdapter;
//        private final BluetoothAdapter.LeScanCallback mLeScanCallback;
//
//        private volatile boolean isScanning = false;
//
//        BleScanner(BluetoothAdapter adapter, BluetoothAdapter.LeScanCallback callback) {
//            bluetoothAdapter = adapter;
//            mLeScanCallback = callback;
//        }
//
//        public boolean isScanning() {
//            return isScanning;
//        }
//
//        public void startScanning() {
//            synchronized (this) {
//                isScanning = true;
//                start();
//            }
//        }
//
//        public void stopScanning() {
//            synchronized (this) {
//                isScanning = false;
//                bluetoothAdapter.stopLeScan(mLeScanCallback);
//            }
//        }
//
//        @Override
//        public void run() {
//            try {
//                while (true) {
//                    synchronized (this) {
//                        if (!isScanning)
//                            break;
//
//                        bluetoothAdapter.startLeScan(mLeScanCallback);
//                    }
//                    sleep(SCAN_PERIOD);
//
//                    /*synchronized (this) {
//                        bluetoothAdapter.stopLeScan(mLeScanCallback);
//                    }*/
//                }
//            } catch (InterruptedException ignore) {
//            } finally {
//                bluetoothAdapter.stopLeScan(mLeScanCallback);
//            }
//        }
//    }
//
//    private FileOutputStream storageFile = null;
//
//    public void onDeviceDisconnected() {
//        mBluetoothGatt = null;
//
//        Log.i("ECGBELT", "onDeviceDisconnected");
//
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
//                    bpReceiveHandler.measurementEnd();
//                }
//            });
//        }
//
//    }
//
//    public void onEcgReceived(final int HeartRate, final short array[], final int Frequency) {
//
//        if (storageFile == null) {
//            mainActivity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    bpReceiveHandler.measurementStart(LocationUtils.wifi.toString());
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
////                Log.i("EcgBle", "hdr.getBytes() = " + hdr.getBytes());
//                storageFile.write(intToBytes(Frequency)); // freq
//                storageFile.write(intToBytes(1)); // channels
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//
//        }
//
//        try {
//            ByteBuffer buffer = ByteBuffer.allocate(array.length * 2);
//            buffer.order(ByteOrder.LITTLE_ENDIAN);
//            buffer.asShortBuffer().put(array);
//            storageFile.write(buffer.array());
////            Log.i("EcgBle", "buffer.array = " + array);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        mainActivity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                bpReceiveHandler.measurementReceived(HeartRate, array, Frequency);
//            }
//        });
//
//    }
//
//    public byte[] intToBytes(final int i) {
//        ByteBuffer bb = ByteBuffer.allocate(4);
//        bb.order(ByteOrder.LITTLE_ENDIAN).putInt(i);
//        return bb.array();
//    }
//
//}
