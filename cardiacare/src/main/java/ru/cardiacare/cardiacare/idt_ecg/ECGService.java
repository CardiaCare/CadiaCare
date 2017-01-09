package ru.cardiacare.cardiacare.idt_ecg;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
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
import android.text.format.Time;
import android.util.Log;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgBle;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgBleDevice;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgReceiveHandler;
//import ru.cardiacare.cardiacare.idt_ecg.drivers.GattUtils;

public class ECGService extends Service {

    static Context mContext;
    static public ECGService myService;
    static private ECGService.EcgBle ecg = null;

    static boolean connected_flag = false; // Установлено ли подключение к монитору, true - установлено, false - не установлено
    long startTime;                        // Время начала работы сервиса
    static String pastTime;                // Прошедшее время с начала работы сервиса
    static public int ecgValue;            // Значения ЭКГ (с кардиомонитора)
    static private int heartRate = 0;      // Пульс (с кардиомонитора)
    static public int charge = 0;          // Уровень заряда батареи кардиомонитора

    static NotificationManager notificationManager;
    static Notification ecgNotification;

    MyBinder binder = new MyBinder();

    Timer pastTimeTimer;
    TimerTask timerTask;
    long timerInterval = 1000;

    static Time beginTime = new Time();
    static public boolean beStarted = false;

    //    static private LocationUtils location = null;
    //    private SensorsUtils sensors = null;

    public void onCreate() {
        super.onCreate();
        Log.d("ECGService", "ECGService onCreate");
        mContext = this;
        myService = this;
        this.ecg = new ECGService.EcgBle(MainActivity.activity, EcgBle.bpReceiveHandler);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        pastTimeTimer = new Timer();
        startTime = System.currentTimeMillis(); // Фиксируем время начала работы сервиса
        pastTimeCounter();
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
                }
            };
            pastTimeTimer.schedule(timerTask, 1000, timerInterval);
        }
    }

    // Отправка уведомлений с показаниями, полученными с монитора
    static void sendECGNotification(int ecgValue, int heartrate, int charge) {
        Notification.Builder builder = new Notification.Builder(mContext);
        if (connected_flag == false) {
            builder.setContentTitle("CardiaCare. ECG disconnected");
        } else {
            builder.setContentTitle("CardiaCare. ECG connected");
        }
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentText("Показания с монитора: " + ecgValue + ". Пульс: " + heartrate);
        builder.setSubText("Заряд: " + charge + "%. Прошло: " + pastTime);   //API level 16
        builder.build();
        ecgNotification = builder.getNotification();
        ecgNotification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, ecgNotification); // 1 - это идентификатор уведомления
    }

    /*********************************************************************************************************************
     * ********************************************************************************************************************
     * ***********************************************EcgBleDevice.java****************************************************
     * ********************************************************************************************************************
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
     * ********************************************************************************************************************
     * *************************************************EcgBleIdt.java*****************************************************
     * ********************************************************************************************************************
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
                            heartRate = hr;
                            int val;

                            BatteryLevel = array[0] & 0xff;
                            charge = BatteryLevel - 100;

//                        Log.i("ECGBELT", "Receive Hr=" + String.format("%d", hr));

                            for (int i = 2; i < 12; i++) {
                                val = byteToUnsignedInt(array[i]);
                                intdata[arrayPos] = val;
                                ecgstr = new StringBuilder(String.valueOf(ecgstr)).append(val).toString();
                                ecgstr = new StringBuilder(String.valueOf(ecgstr)).append(", ").toString();
//                                Log.i("QQQ", "Отправляю на отрисовку: " + intdata[arrayPos]);
                                ecgValue = intdata[arrayPos];

                                // Shift and reamp signal
                                val = val - 127;
                                val = (val * 687) / 10;

                                sdata[arrayPos] = (short) val;
                                arrayPos++;
                                if (arrayPos == 30) {
                                    sendECGNotification(ecgValue, heartRate, charge);
                                    arrayPos = 0;
                                    mHandler.obtainMessage(1, intdata).sendToTarget();
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
     * ********************************************************************************************************************
     * **************************************************EcgBle.java*******************************************************
     * ********************************************************************************************************************
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
        static public EcgBleDevice driver = null;

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
            sendECGNotification(ecgValue, heartRate, charge);
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
//                    bpReceiveHandler.measurementReceived(heartRate, array, Frequency);
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
     * ********************************************************************************************************************
     * **************************************************GattUtils.java*******************************************************
     * ********************************************************************************************************************
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
     * ********************************************************************************************************************
     * **************************************************EcgReceiveHandler.java*******************************************************
     * ********************************************************************************************************************
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
        this.heartRate = heartrate;
    }

    // Конец получения ЭКГ
    // Вызов функции сборки данных для отправки данных на сервер
    public void measurementEnd() {
//        Log.i("ECGBELT", "measurementEnd()");
//        if (this.location.isActive()) {
//            this.location.Stop();
//        }
//        updateOnServer(this.ecg.StorageFileName, "", this.ecg.StorageFileId, this.heartRate);
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