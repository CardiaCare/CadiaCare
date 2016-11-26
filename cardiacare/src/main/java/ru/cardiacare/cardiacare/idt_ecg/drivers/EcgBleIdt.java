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

import java.util.UUID;

public class EcgBleIdt extends EcgBleDevice {

    public Handler mHandler;

    public static final UUID BATTERY_SERVICE = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    public static final UUID BP_SERVICE = new UUID((0x1810L << 32) | 0x1000, GattUtils.leastSigBits);
    public static final UUID INTERMEDIATE_CUFF_PRESSURE = UUID.fromString("00002a36-0000-1000-8000-00805f9b34fb");

    public static final UUID CLIENT_CONTROL_CHAR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public boolean isFirstTime = true;
    public boolean isDisconnected = false;

    static public byte[] array;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    EcgBleIdt(EcgBle handle, Handler handler) {
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

                                if (bpBle.mBluetoothGatt != null)
                                    bpBle.mBluetoothGatt.discoverServices();
                            }
                            isFirstTime = false;

                        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {

                            Log.i("ECGBELT", "STATE_DISCONNECTED.");
                            if (!isDisconnected) {
                                isDisconnected = true;
                                bpBle.onDeviceDisconnected();
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

//                        byte[] array = characteristic.getValue();
                        array = characteristic.getValue();

                        final int hr = array[1] & 0xff;
                        int val;

                        BatteryLevel = array[0] & 0xff;

                        final int Data = 1;

//                        Log.i("ECGBELT", "Receive Hr=" + String.format("%d", hr));
//                        Log.i("ECGBELT", "Receive array=" + array);

                        for (int i = 2; i < 12; i++) {
//                            Log.i("EchBleldt", "array[i] = " + array[i]);

//                            mHandler = new Handler(Looper.getMainLooper());
//                            mHandler.obtainMessage(0, -1).sendToTarget();
//                            Message msg = mHandler.obtainMessage(0);
//                            Bundle bundle = new Bundle();
//                            bundle.putString("ECG_BLE", "ECG_BLE");
//                            msg.setData(bundle);
//                            mHandler.sendMessage(msg);
//                            mHandler.obtainMessage(Data, array).sendToTarget();

                            val = byteToUnsignedInt(array[i]);

                            // Shift and reamp signal
                            val = val - 127;
                            val = (val * 687) / 10;
//                            Log.i("EchBleldt", "sdata[arrayPos] = " + val);

                            sdata[arrayPos] = (short) val;
                            intdata[arrayPos] = val;
                            arrayPos++;
                            if (arrayPos == 30) {
                                arrayPos = 0;
                                mHandler.obtainMessage(1, intdata).sendToTarget();
                                bpBle.onEcgReceived(hr, sdata, 200); // 200 Hz
//                                Log.i("EchBleldt", "sdata = " + sdata);
                            }
                        }
                    }
                };
    }

    private static int byteToUnsignedInt(byte b) {
        return 0x00 << 24 | b & 0xff;
    }

}
