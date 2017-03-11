package ru.cardiacare.cardiacare.idt_ecg.drivers;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

import ru.cardiacare.cardiacare.idt_ecg.ECGService;

import static android.content.Context.MODE_PRIVATE;

public class EcgBleIdt extends EcgBleDevice {

    static public Handler mHandler;

    public final UUID BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public final UUID BP_SERVICE = new UUID((0x1810L << 32) | 0x1000, GattUtils.leastSigBits);
    public final UUID INTERMEDIATE_CUFF_PRESSURE = UUID.fromString("00002a36-0000-1000-8000-00805f9b34fb");

    public final UUID CLIENT_CONTROL_CHAR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public boolean isFirstTime = true;
    public boolean isDisconnected = false;

    public byte[] array;

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
                        ECGService.connected_flag = true;
                        Long timestamp = System.currentTimeMillis() / 1000;
                        ECGService.connectedTime = timestamp;
                        Log.i("ECGBELT", "onDescriptorWrite.");
                        ECGService.ecgFileName = "ecg" + System.currentTimeMillis();
//        Log.i("EcgBleIdt", "ecgFileName = " + ecgFileName);
                        ECGService.ecgFiles.add(ECGService.ecgFileName);
//        Log.i("EcgBleIdt", "ecgFiles = " + ecgFiles.toString());
                        try {
//                            Log.i("EcgBleIdt", "Создаём буффер, TRY");
                            ECGService.bw = new BufferedWriter(new OutputStreamWriter(ECGService.mContext.openFileOutput(ECGService.ecgFileName, MODE_PRIVATE)));
                        } catch (IOException e) {
//                            Log.i("EcgBleIdt", "Создаём буффер, CATCH");
                            e.printStackTrace();
                        }
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
                        ECGService.heartRate = hr;
                        int val;

                        BatteryLevel = array[0] & 0xff;
                        ECGService.charge = BatteryLevel - 100;

//                        Log.i("ECGBELT", "Receive Hr=" + String.format("%d", hr));

//                        if (ECGService.bw != null) {
//                            Log.i("QQQ", "bw is open = " + ECGService.bw);
//                        } else {
//                            Log.i("QQQ", "bw is close");
//                        }

//                        Log.i("QQQ", "ecgFileName = " + ECGService.ecgFileName);

                        for (int i = 2; i < 12; i++) {
                            val = byteToUnsignedInt(array[i]);
                            intdata[arrayPos] = val;
                            try {
//                                Log.i("QQQ", "TRY, пишем");
                                ECGService.bw.write(Integer.toString(val));
                                ECGService.bw.write(",");
                            } catch (IOException e) {
//                                Log.i("QQQ", "CATCH, не пишем");
                            }
//                                Log.i("QQQ", "Отправляю на отрисовку: " + intdata[arrayPos]);
                            ECGService.ecgValue = intdata[arrayPos];

                            // Shift and reamp signal
                            val = val - 127;
                            val = (val * 687) / 10;

                            sdata[arrayPos] = (short) val;
                            arrayPos++;
                            if (arrayPos == 30) {
                                ECGService.sendECGNotification(ECGService.ecgValue, ECGService.heartRate, ECGService.charge);
                                arrayPos = 0;
                                mHandler.obtainMessage(1, intdata).sendToTarget();
//                                Log.i("EcgBleIdt", "Отправляем данные на отрисовку");
//                                EcgBle.onEcgReceived(hr, sdata, 200); // 200 Hz
                            }
                        }
                    }
                };
    }

    private int byteToUnsignedInt(byte b) {
        return 0x00 << 24 | b & 0xff;
    }
}