//package ru.cardiacare.cardiacare.idt_ecg.drivers;
//
//import android.bluetooth.BluetoothGatt;
//import android.bluetooth.BluetoothGattCallback;
//import android.bluetooth.BluetoothGattCharacteristic;
//import android.bluetooth.BluetoothGattService;
//
//import java.util.UUID;
//
//public class EcgBleDevice {
//
//    public EcgBleDevice(EcgBle handle) {
//        bpBle = handle;
//    }
//
//    protected EcgBle bpBle;
//
//    public BluetoothGattCallback mGattCallback = null;
//
//    protected int BatteryLevel = 0;
//    protected String SerialNumber;
//
//    public String getSerialNumber() {
//        return SerialNumber;
//    }
//
//    public int getBatteryLevel() {
//        return BatteryLevel;
//    }
//
//    public BluetoothGattCharacteristic getCharacteristic(BluetoothGatt bluetoothGatt, UUID serviceUuid, UUID characteristicUuid) {
//
//        final BluetoothGattService service = bluetoothGatt.getService(serviceUuid);
//        return service.getCharacteristic(characteristicUuid);
//    }
//}
