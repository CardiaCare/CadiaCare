package ru.cardiacare.cardiacare.idt_ecg;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;
import ru.cardiacare.cardiacare.idt_ecg.common.DateTimeUtl;
import ru.cardiacare.cardiacare.idt_ecg.common.LocationUtils;
import ru.cardiacare.cardiacare.idt_ecg.common.SensorsUtils;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgBle;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgBleDevice;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgReceiveHandler;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.GattUtils;

public class ECGService extends Service {

    long date;
    SimpleDateFormat sdf;
    static String dateString;
    final String LOG_TAG = "myLogs";
    static Context mContext;
    static public int fornotif;
    static public int fornotif2;
    static public int fornotif3;

    static NotificationManager manager;
    static Notification myNotication;
    static Notification myNotication2;

    MyBinder binder = new MyBinder();

    Timer timer;
    TimerTask tTask;
    long interval = 1000;

    static private ECGService.EcgBle ecg = null;
    static Time beginTime = new Time();
    //    static private LocationUtils location = null;
    static public boolean beStarted = false;

    private int HeartRate = 0;
//    private SensorsUtils sensors = null;

    static boolean connected_flag = false; // Установлено ли подключение к монитору
    static public ECGService myService;

    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "MyService onCreate");
        mContext = this;
        myService = this;
        this.ecg = new ECGService.EcgBle(MainActivity.activity, EcgBle.bpReceiveHandler);
        fornotif2 = 0;
        fornotif3 = 0;
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        timer = new Timer();
        schedule();
    }

    static public ECGService returnService() {
        Log.i("QQQ", "ECGService, returnService()");
        return myService;
    }


    public void onDestroy() {
        timer.cancel();
        tTask.cancel();
        doStop();
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
    }

//    public int onStartCommand(Intent intent, int flags, int startId) {
//        return super.onStartCommand(intent, flags, startId);
//    }

    void schedule() {
        if (tTask != null) tTask.cancel();
        if (interval > 0) {
            tTask = new TimerTask() {
                public void run() {
                    date = System.currentTimeMillis();
                    sdf = new SimpleDateFormat("h:mm:ss");
                    dateString = sdf.format(date);
//                    Log.d(LOG_TAG, "time = " + dateString);
                    fornotif3++;
                    sendTimeNotif(dateString, fornotif3);
                }
            };
            timer.schedule(tTask, 1000, interval);
        }
    }

    public IBinder onBind(Intent arg0) {
        Log.d(LOG_TAG, "MyService onBind");
        return binder;
    }

    public class MyBinder extends Binder {
        public ECGService getService() {
            return ECGService.this;
        }
    }

    static void sendNotif(int fornotif, int fornotif2) {
//        Intent intent = new Intent("com.rj.notitfications.SECACTIVITY");
//        PendingIntent pendingIntent = PendingIntent.getService(ECGService.mContext, 1, intent, 0);

        Notification.Builder builder = new Notification.Builder(mContext);

//        builder.setAutoCancel(false);
//        builder.setTicker("this is ticker text");
        if (connected_flag == false) {
            builder.setContentTitle("CardiaCare NOT WORK");
        } else {
            builder.setContentTitle("CardiaCare WORK");
        }
        builder.setContentText("Показания с монитора = " + fornotif);
        builder.setSmallIcon(R.drawable.ic_launcher);
//        builder.setContentIntent(pendingIntent);
//        builder.setOngoing(true);
        builder.setSubText("Уведомление " + fornotif2);   //API level 16
//        builder.setNumber(100);
        builder.build();

        myNotication = builder.getNotification();
        myNotication.flags |= Notification.FLAG_AUTO_CANCEL; // ставим флаг, чтобы уведомление пропало после нажатия
        manager.notify(1, myNotication);
//        BluetoothFindActivity.ecgService.startForeground (1, myNotication);

    }

    static void sendTimeNotif(String dateString, int fornotif3) {
        Notification.Builder builder2 = new Notification.Builder(mContext);

        builder2.setContentTitle("CardiaCareTime");
        builder2.setContentText("Время = " + dateString);
        builder2.setSmallIcon(R.drawable.ic_launcher);
//        builder2.setOngoing(true);
        builder2.setSubText("Уведомление " + fornotif3);   //API level 16
        builder2.build();

        myNotication2 = builder2.getNotification();
        myNotication2.flags |= Notification.FLAG_AUTO_CANCEL; // ставим флаг, чтобы уведомление пропало после нажатия
        manager.notify(11, myNotication2);
//        BluetoothFindActivity.ecgService.startForeground (11, myNotication2);
    }
    /*********************************************************************************************************************
     *********************************************************************************************************************
     ************************************************EcgBleDevice.java****************************************************
     *********************************************************************************************************************
     *********************************************************************************************************************/
    static public class EcgBleDevice {

        public EcgBleDevice(EcgBle handle) {
            bpBle = handle;
        }

        protected EcgBle bpBle;

        public BluetoothGattCallback mGattCallback = null;

        protected int BatteryLevel = 0;
        protected String SerialNumber;

        public String getSerialNumber() {
            return SerialNumber;
        }

        public int getBatteryLevel() {
            return BatteryLevel;
        }

        public BluetoothGattCharacteristic getCharacteristic(BluetoothGatt bluetoothGatt, UUID serviceUuid, UUID characteristicUuid) {

            final BluetoothGattService service = bluetoothGatt.getService(serviceUuid);
            return service.getCharacteristic(characteristicUuid);
        }
    }
    /*********************************************************************************************************************
     *********************************************************************************************************************
     **************************************************EcgBleIdt.java*****************************************************
     *********************************************************************************************************************
     *********************************************************************************************************************/
    static public class EcgBleIdt extends EcgBleDevice {

        static public Handler mHandler;

        public final UUID BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
        public final UUID BP_SERVICE = new UUID((0x1810L << 32) | 0x1000, GattUtils.leastSigBits);
        public final UUID INTERMEDIATE_CUFF_PRESSURE = UUID.fromString("00002a36-0000-1000-8000-00805f9b34fb");

        public final UUID CLIENT_CONTROL_CHAR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

        public boolean isFirstTime = true;
        public boolean isDisconnected = false;

        public byte[] array;
        static public String ecgstr = "";

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        public EcgBleIdt(EcgBle handle, Handler handler) {
            super(handle);

            isFirstTime = true;
            isDisconnected = false;

            mHandler = handler;

            Log.i("ECGBELT", "new EcgBleIdt");

            mGattCallback =
                    new BluetoothGattCallback() {

                        private BluetoothGattCharacteristic characteristicMeasure;

                        private short sdata[] = new short[30];
                        private int intdata[] = new int[30];

                        private int arrayPos = 0;
                        //private byte rdata[] = new byte[20];
                        //private int arrayAbsPos = 0;


                        @Override
                        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                            super.onDescriptorWrite(gatt, descriptor, status);
                            connected_flag = true;
                            Log.i("ECGBELT", "onDescriptorWrite.");
                        }

                        @Override
                        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                            super.onCharacteristicWrite(gatt, characteristic, status);
                        }

                        @Override
                        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                            if (newState == BluetoothProfile.STATE_CONNECTED) {

                                if (isFirstTime) { // bug - call Connect after .disconnect()
                                    Log.i("ECGBELT", "Connected to GATT server.");

                                    if (EcgBle.mBluetoothGatt != null)
                                        EcgBle.mBluetoothGatt.discoverServices();
                                }
                                isFirstTime = false;

                            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                                Log.i("ECGBELT", "STATE_DISCONNECTED.");
                                if (!isDisconnected) {
                                    isDisconnected = true;
                                    EcgBle.onDeviceDisconnected();
                                }
                            }
                        }

                        @Override
                        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                            super.onServicesDiscovered(gatt, status);

                            if (status == BluetoothGatt.GATT_SUCCESS) {

                                characteristicMeasure = getCharacteristic(gatt, BP_SERVICE, INTERMEDIATE_CUFF_PRESSURE);

                                final BluetoothGattDescriptor config = characteristicMeasure.getDescriptor(CLIENT_CONTROL_CHAR);

                                if (config != null) {
                                    gatt.setCharacteristicNotification(characteristicMeasure, true);

                                    config.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    gatt.writeDescriptor(config);
                                } else {
                                    Log.i("ECGBELT", "onCharacteristicRead. Null descriptor");
                                }


                            } else {
                                //Log.w(TAG, "onServicesDiscovered received: " + status);
                            }
                        }

                        @Override
                        public void onCharacteristicRead(BluetoothGatt gatt,
                                                         BluetoothGattCharacteristic characteristic,
                                                         int status) {
                            Log.i("ECGBELT", "onCharacteristicRead. UID=" + characteristic.getUuid());
                        }

                        @Override
                        public void onCharacteristicChanged(BluetoothGatt gatt,
                                                            BluetoothGattCharacteristic characteristic) {

                            if (isDisconnected) return;

                            array = characteristic.getValue();

                            final int hr = array[1] & 0xff;
                            int val;

                            BatteryLevel = array[0] & 0xff;

//                        Log.i("ECGBELT", "Receive Hr=" + String.format("%d", hr));

                            for (int i = 2; i < 12; i++) {
                                val = byteToUnsignedInt(array[i]);
                                intdata[arrayPos] = val;
                                ecgstr = new StringBuilder(String.valueOf(ecgstr)).append(val).toString();
                                ecgstr = new StringBuilder(String.valueOf(ecgstr)).append(", ").toString();
//                                Log.i("QQQ", "Отправляю на отрисовку: " + intdata[arrayPos]);
                                fornotif = intdata[arrayPos];

                                // Shift and reamp signal
                                val = val - 127;
                                val = (val * 687) / 10;

                                sdata[arrayPos] = (short) val;
                                arrayPos++;
                                if (arrayPos == 30) {
                                    fornotif2++;
                                    sendNotif(fornotif, fornotif2);
                                    arrayPos = 0;
                                    mHandler.obtainMessage(1, intdata).sendToTarget();
//                                    Log.i("QQQ", "Отправляю в хандлер");
                                    EcgBle.onEcgReceived(hr, sdata, 200); // 200 Hz
                                }
                            }
                        }
                    };
        }

        private int byteToUnsignedInt(byte b) {
            return 0x00 << 24 | b & 0xff;
        }

        static public String getJSONPart() {
            String result_str = "";

            result_str = new StringBuilder(String.valueOf(result_str)).append(ecgstr).toString();
            return result_str;
        }
    }
    /*********************************************************************************************************************
     *********************************************************************************************************************
     ***************************************************EcgBle.java*******************************************************
     *********************************************************************************************************************
     *********************************************************************************************************************/
    static public class EcgBle {

        private static final int REQUEST_ENABLE_BT = 1;
        //    private static final long SCAN_PERIOD = 90000; // reconnect device constant TODO
        private static final long SCAN_PERIOD = 1000;

        static private BluetoothManager bluetoothManager;
        static private BluetoothAdapter mBluetoothAdapter;
        static private BleScanner mBluetoothScanner;

        static private EcgReceiveHandler bpReceiveHandler;
        static private Activity mainActivity;

        //public String StoragePath;
        static public String StorageFileName;
        static public String StorageFileId;

        static public BluetoothGatt mBluetoothGatt = null;
        static  public EcgBleDevice driver = null;

        public EcgBle(Activity activity, EcgReceiveHandler handler) {

            bpReceiveHandler = handler;
            mainActivity = activity;

//            bluetoothManager = (BluetoothManager) mainActivity.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();

            if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                mainActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
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
                driver = new ECGService.EcgBleIdt(this, ECGActivity.handler);
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

        static private FileOutputStream storageFile = null;

        static public void onDeviceDisconnected() {
            mBluetoothGatt = null;
            connected_flag = false;
            sendNotif(fornotif, fornotif2);
            myService.stopSelf();

            Log.i("ECGBELT", "onDeviceDisconnected");

            if (storageFile != null) {
                try {
                    storageFile.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                storageFile = null;

                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        bpReceiveHandler.measurementEnd();
                    }
                });
            }

        }

        static public void onEcgReceived(final int HeartRate, final short array[], final int Frequency) {

            if (storageFile == null) {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        bpReceiveHandler.measurementStart(LocationUtils.wifi.toString());
                    }
                });

                Log.i("ECGBELT", "openFile: " + StorageFileName);

                try {
                    storageFile = mainActivity.openFileOutput(StorageFileName, Context.MODE_PRIVATE);
                    //storageFile = new FileOutputStream(new File(StoragePath+StorageFileName));
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                String hdr = "CardioDump1 ";
                try {
                    storageFile.write(hdr.getBytes());
//                Log.i("EcgBle", "hdr.getBytes() = " + hdr.getBytes());
                    storageFile.write(intToBytes(Frequency)); // freq
                    storageFile.write(intToBytes(1)); // channels
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            try {
                ByteBuffer buffer = ByteBuffer.allocate(array.length * 2);
                buffer.order(ByteOrder.LITTLE_ENDIAN);
                buffer.asShortBuffer().put(array);
                storageFile.write(buffer.array());
//            Log.i("EcgBle", "buffer.array = " + array);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    bpReceiveHandler.measurementReceived(HeartRate, array, Frequency);
                }
            });

        }

        static public byte[] intToBytes(final int i) {
            ByteBuffer bb = ByteBuffer.allocate(4);
            bb.order(ByteOrder.LITTLE_ENDIAN).putInt(i);
            return bb.array();
        }
    }
    /*********************************************************************************************************************
     *********************************************************************************************************************
     ***************************************************GattUtils.java*******************************************************
     *********************************************************************************************************************
     *********************************************************************************************************************/
    static public class GattUtils {
        public static final long leastSigBits = 0x800000805f9b34fbL;

        public static final int FIRST_BITMASK = 0x01;
        public static final int SECOND_BITMASK = FIRST_BITMASK << 1;
        public static final int THIRD_BITMASK = FIRST_BITMASK << 2;
        public static final int FOURTH_BITMASK = FIRST_BITMASK << 3;
        public static final int FIFTH_BITMASK = FIRST_BITMASK << 4;
        public static final int SIXTH_BITMASK = FIRST_BITMASK << 5;
        public static final int SEVENTH_BITMASK = FIRST_BITMASK << 6;
        public static final int EIGTH_BITMASK = FIRST_BITMASK << 7;

        public static final int FORMAT_UINT8 = 17;
        public static final int FORMAT_UINT16 = 18;
        public static final int FORMAT_UINT32 = 20;
        public static final int FORMAT_SINT8 = 33;
        public static final int FORMAT_SINT16 = 34;
        public static final int FORMAT_SINT32 = 36;
        public static final int FORMAT_SFLOAT = 50;
        public static final int FORMAT_FLOAT = 52;

        public static UUID toUuid(String uuidString) {
            return UUID.fromString(uuidString);
        }

        public static UUID toUuid(long assignedNumber) {
            return new UUID((assignedNumber << 32) | 0x1000, leastSigBits);
        }

        public static String toUuid128(long assignedNumber) {
            return toUuid(assignedNumber).toString();
        }

        public static String toUuid16(int assignedNumber) {
            return Integer.toHexString(assignedNumber);
        }

        public static Integer getIntValue(byte[] value, int format, int position) {
            if (value == null)
                return null;
            if (position + (format & 0xF) > value.length)
                return null;
            switch (format) {
                case FORMAT_UINT8:
                    return Integer.valueOf(value[position] & 0xFF);
                case FORMAT_UINT16:
                    return Integer.valueOf(add(value[position], value[(position + 1)]));
                case FORMAT_UINT32:
                    return Integer.valueOf(add(value[position], value[(position + 1)],
                            value[(position + 2)], value[(position + 3)]));
                case FORMAT_SINT8:
                    return Integer.valueOf(signed(value[position] & 0xFF, 8));
                case FORMAT_SINT16:
                    return Integer.valueOf(signed(
                            add(value[position], value[(position + 1)]), 16));
                case FORMAT_SINT32:
                    return Integer.valueOf(signed(
                            add(value[position], value[(position + 1)],
                                    value[(position + 2)], value[(position + 3)]), 32));
            }
            return null;
        }

        public static Float getFloatValue(byte[] value, int format, int position) {
            if (value == null)
                return null;
            if (position + (format & 0xF) > value.length)
                return null;
            int i;
            int mantissa;
            int exponent;
            switch (format) {
                case FORMAT_SFLOAT:
                    i = value[(position + 1)];
                    position = value[position];
                    mantissa = signed((position & 0xFF) + ((i & 0xFF & 0xF) << 8), 12);
                    exponent = signed((i & 0xFF) >> 4, 4);
                    return Float
                            .valueOf((float) (mantissa * Math.pow(10.0D, exponent)));
                case FORMAT_FLOAT:
                    exponent = value[(position + 3)];
                    mantissa = value[(position + 2)];
                    i = value[(position + 1)];
                    position = value[position];
                    return Float
                            .valueOf((float) ((format = signed((position & 0xFF)
                                    + ((i & 0xFF) << 8) + ((mantissa & 0xFF) << 16), 24)) * Math
                                    .pow(10.0D, exponent)));
            }
            return null;
        }

        public static String getStringValue(byte[] value, int position) {
            if (value == null)
                return null;
            if (position > value.length)
                return null;
            byte[] arrayOfByte = new byte[value.length - position];
            for (int i = 0; i != value.length - position; i++) {
                arrayOfByte[i] = value[(position + i)];
            }
            return new String(arrayOfByte);
        }

        public static Time getDateTimeValue(byte[] value, int position) {
            if (value == null)
                return null;
            Time dtm = new Time();

            dtm.set(Integer.valueOf(value[position + 6] & 0xFF) // seconds
                    , Integer.valueOf(value[position + 5] & 0xFF)
                    , Integer.valueOf(value[position + 4] & 0xFF)
                    , Integer.valueOf(value[position + 3] & 0xFF) // days
                    , Integer.valueOf(value[position + 2] & 0xFF)
                    , Integer.valueOf(add(value[position], value[(position + 1)])));

            return dtm;
        }

        private static int add(byte byte1, byte byte2) {
            return (byte1 & 0xFF) + ((byte2 & 0xFF) << 8);
        }

        private static int add(byte byte1, byte byte2, byte byte3, byte byte4) {
            return (byte1 & 0xFF) + ((byte2 & 0xFF) << 8) + ((byte3 & 0xFF) << 16)
                    + ((byte4 & 0xFF) << 24);
        }

        private static int signed(int value, int length) {
            if ((value & 1 << length - 1) != 0)
                value = -1 * ((1 << length - 1) - (value & (1 << length - 1) - 1));
            return value;
        }

        /**
         * Convert hex byte array from BLE API to byte array.
         *
         * @param hexByteArray
         * @return
         */
        public static byte[] hexByteArrayToByteArray(byte[] hexByteArray) {
            return hexStringToByteArray(new String(hexByteArray));
        }

        /**
         * Convert string from BLE API to a byte array.
         *
         * @param hexString
         * @return
         */
        public static byte[] hexStringToByteArray(String hexString) {
            int len = hexString.length();
            byte[] data = new byte[len / 2];
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character
                        .digit(hexString.charAt(i + 1), 16));
            }
            return data;
        }
    }
    /*********************************************************************************************************************
     *********************************************************************************************************************
     ***************************************************EcgReceiveHandler.java*******************************************************
     *********************************************************************************************************************
     *********************************************************************************************************************/
    public interface EcgReceiveHandler {
        void measurementStart(String mac);
        void measurementReceived(int HeartRate, short array[], int Frequency);
        void measurementEnd();
    }

    // Начать получение ЭКГ
    static public boolean doStart() {
        if (!ecg.Start()) {
            return false;
        }
        beginTime.setToNow();
        ecg.StorageFileName = String.format("%d.cds", new Object[]{Long.valueOf(beginTime.toMillis(true) / 1000)});
        ecg.StorageFileId = String.format("a%de", new Object[]{Long.valueOf(beginTime.toMillis(true) / 1000)});
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


    // Получение пульса
    public void measurementReceived(int heartrate, short[] array, int Frequency) {
        this.HeartRate = heartrate;
    }

    // Конец получения ЭКГ
    // Вызов функции сборки данных для отправки данных на сервер
    public void measurementEnd() {
//        Log.i("ECGBELT", "measurementEnd()");
//        if (this.location.isActive()) {
//            this.location.Stop();
//        }
//        updateOnServer(this.ecg.StorageFileName, "", this.ecg.StorageFileId, this.HeartRate);
    }

    // Начало получения ЭКГ
    public void measurementStart(String mac) {
        this.beStarted = true;
//        this.timerHandler.postDelayed(this.timerRunnable, 500);
        this.beginTime.setToNow();
    }

//    // Формирование файлов для отправки на сервер
//    public void updateOnServer(String filename, String path, String fileid, int hr) {
//        String context = this.location.getJSONPart();
//        String sensdata = this.sensors.getJSONPart();
//        String systemdata = this.sensors.getSystemJSONPart();
//        if (context == "") {
//            context = sensdata;
//        } else if (sensdata != "") {
//            context = new StringBuilder(String.valueOf(context)).append(",").append(sensdata).toString();
//        }
//        String json = "{ \"app\": \"ecgsend\", ";
//        if (context != "") {
//            json = new StringBuilder(String.valueOf(json)).append("\"context\": {").append(context).append("},").toString();
//        }
//        if (systemdata != "") {
//            json = new StringBuilder(String.valueOf(json)).append("\"system\": {").append(systemdata).append("},").toString();
//        }
//        json = new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(json)).append("\"object\": {").toString())).append("\"timestamp\": \"").append(DateFormat.format("yyyy-MM-dd'T'HH:mm:ssZ", this.beginTime.toMillis(true))).append("\",").toString())).append("\"utc_offset\": \"").append(DateTimeUtl.getCurrentUTCOffset()).append("\",").toString())).append("\"namespace\": \"ecg\",").toString())).append("\"channels\": \"1\",").toString())).append("\"format\": \"cds\",").toString())).append("\"filename\":\"").append(fileid).append("\",").toString())).append("\"pulse\":\"").append(String.format("%d", new Object[]{Integer.valueOf(hr)})).append("\"").toString())).append("}}").toString();
//        Log.i("ECGBELT", "JSON=" + json);
//
////        String ecgdata = "100, 101, 102";
//        String ecgdata = ECGService.EcgBleIdt.getJSONPart();
//        String ecgjson = "{ \"id\":\"1\", \"patient_id\":\"1\", \"data\": {[\"";
//        ecgjson = new StringBuilder(String.valueOf(ecgjson)).append(ecgdata).append("\"]},").toString();
//        ecgjson = new StringBuilder(String.valueOf(ecgjson)).append("\"created_at\":\"09122016\"}").toString();
//        Log.i("ECGBELT", "ECGJSON=" + ecgjson);
//    }
}