package ru.cardiacare.cardiacare.idt_ecg.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;

import ru.cardiacare.cardiacare.idt_ecg.ECGService;

//import ru.cardiacare.cardiacare.idt_ecg.drivers.GattUtils;

@SuppressLint({"InlinedApi"})
public class SensorsUtils {
    private int activatedSensors = 0;
    private Runnable ascureLight = new Runnable() {
        public void run() {
            SensorsUtils.this.activateLightSensor();
        }
    };
    private Runnable ascureUV = new Runnable() {
        public void run() {
            SensorsUtils.this.activateUVSensor();
        }
    };
    public int batteryLevel = -1;
    public String deviceModel;
    public int humidity = -1;
    private intSensorListener humidityListener = null;
    private intSensorListener lightListener = null;
    public double lux = 0.0d;
    private Activity mainActivity;
    public double pressure = -999.0d;
    private intSensorListener pressureListener = new intSensorListener(6);
    private Handler proximityDelayHandler = null;
    private intSensorListener proximityListener = null;
    private SensorManager sensorManager = null;
    private Runnable stopListenProximity = new Runnable() {
        public void run() {
            SensorsUtils sensorsUtils = SensorsUtils.this;
            sensorsUtils.activatedSensors = sensorsUtils.activatedSensors - 1;
            SensorsUtils.this.sensorManager.unregisterListener(SensorsUtils.this.proximityListener);
        }
    };
    public double temp = -999.0d;
    private intSensorListener tempListener = null;
    public double uv = -1.0d;

    class intSensorListener implements SensorEventListener {
        private int sensorType = 0;

        intSensorListener(int type) {
            this.sensorType = type;
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            float value = event.values[0];
            SensorsUtils sensorsUtils;
            switch (this.sensorType) {
                case 5:
                    SensorsUtils.this.lux = (double) value;
                    SensorsUtils.this.sensorManager.unregisterListener(SensorsUtils.this.lightListener);
                    sensorsUtils = SensorsUtils.this;
                    sensorsUtils.activatedSensors = sensorsUtils.activatedSensors - 1;
                    return;
                case 6:
                    SensorsUtils.this.pressure = SensorsUtils.round(((double) value) * 0.75006375541921d, 3);
                    SensorsUtils.this.sensorManager.unregisterListener(SensorsUtils.this.pressureListener);
                    sensorsUtils = SensorsUtils.this;
                    sensorsUtils.activatedSensors = sensorsUtils.activatedSensors - 1;
                    return;
                case 7:
                case 13:
                    SensorsUtils.this.temp = SensorsUtils.round((double) value, 2);
                    SensorsUtils.this.sensorManager.unregisterListener(SensorsUtils.this.tempListener);
                    sensorsUtils = SensorsUtils.this;
                    sensorsUtils.activatedSensors = sensorsUtils.activatedSensors - 1;
                    return;
                case ECGService.GattUtils.FOURTH_BITMASK /*8*/:
                    if (value >= event.sensor.getMaximumRange()) {
                        SensorsUtils.this.sensorManager.unregisterListener(SensorsUtils.this.proximityListener);
                        sensorsUtils = SensorsUtils.this;
                        sensorsUtils.activatedSensors = sensorsUtils.activatedSensors - 1;
                        if (SensorsUtils.this.proximityDelayHandler != null) {
                            SensorsUtils.this.proximityDelayHandler.removeCallbacks(SensorsUtils.this.stopListenProximity);
                        }
                        SensorsUtils.this.proximityDelayHandler = new Handler();
                        SensorsUtils.this.proximityDelayHandler.postDelayed(SensorsUtils.this.ascureLight, 1500);
                        SensorsUtils.this.proximityDelayHandler.postDelayed(SensorsUtils.this.ascureUV, 1500);
                        return;
                    } else if (SensorsUtils.this.proximityDelayHandler == null) {
                        SensorsUtils.this.proximityDelayHandler = new Handler();
                        SensorsUtils.this.proximityDelayHandler.postDelayed(SensorsUtils.this.stopListenProximity, 10000);
                        return;
                    } else {
                        return;
                    }
                case 12:
                    SensorsUtils.this.humidity = (int) value;
                    SensorsUtils.this.sensorManager.unregisterListener(SensorsUtils.this.humidityListener);
                    sensorsUtils = SensorsUtils.this;
                    sensorsUtils.activatedSensors = sensorsUtils.activatedSensors - 1;
                    return;
                default:
                    return;
            }
        }
    }

    public SensorsUtils(Activity activity) {
        this.mainActivity = activity;
        this.sensorManager = (SensorManager) this.mainActivity.getSystemService("sensor");
        if (VERSION.SDK_INT < 14) {
            this.tempListener = new intSensorListener(7);
            return;
        }
        this.tempListener = new intSensorListener(13);
        this.humidityListener = new intSensorListener(12);
    }

    public String getJSONPart() {
        String str = "";
        if (this.temp > -999.0d) {
            str = "\"temp\":\"" + this.temp + "\"";
        }
        if (this.pressure > -999.0d) {
            if (str != "") {
                str = new StringBuilder(String.valueOf(str)).append(",").toString();
            }
            str = new StringBuilder(String.valueOf(str)).append("\"pressure\":\"").append(this.pressure).append("\"").toString();
        }
        if (this.humidity > -1) {
            if (str != "") {
                str = new StringBuilder(String.valueOf(str)).append(",").toString();
            }
            str = new StringBuilder(String.valueOf(str)).append("\"humidity\":\"").append(this.humidity).append("\"").toString();
        }
        if (this.lux > 0.0d) {
            if (str != "") {
                str = new StringBuilder(String.valueOf(str)).append(",").toString();
            }
            str = new StringBuilder(String.valueOf(str)).append("\"lightlevel\":\"").append(this.lux).append("\"").toString();
        }
        if (this.uv < 0.0d) {
            return str;
        }
        if (str != "") {
            str = new StringBuilder(String.valueOf(str)).append(",").toString();
        }
        return new StringBuilder(String.valueOf(str)).append("\"real_uvindex\":\"").append(this.uv).append("\"").toString();
    }

    public String getSystemJSONPart() {
        String str = "";
        if (!this.deviceModel.isEmpty()) {
            str = "\"device\":\"" + this.deviceModel + "\"";
        }
        if (this.batteryLevel < 0) {
            return str;
        }
        if (str != "") {
            str = new StringBuilder(String.valueOf(str)).append(",").toString();
        }
        return new StringBuilder(String.valueOf(str)).append("\"battery\":\"").append(this.batteryLevel).append("\"").toString();
    }

    public boolean isComplete() {
        if (this.activatedSensors > 0) {
            return false;
        }
        return true;
    }

    public void Start(boolean deviceState) {
        this.lux = 0.0d;
        this.temp = -999.0d;
        this.pressure = -999.0d;
        this.humidity = -1;
        this.uv = -1.0d;
        this.batteryLevel = -1;
        this.deviceModel = "";
        if (this.sensorManager.getDefaultSensor(8) != null) {
            this.activatedSensors++;
            this.proximityListener = new intSensorListener(8);
            this.sensorManager.registerListener(this.proximityListener, this.sensorManager.getDefaultSensor(8), 3);
        } else {
            activateLightSensor();
        }
        if (this.sensorManager.getDefaultSensor(6) != null) {
            this.activatedSensors++;
            this.sensorManager.registerListener(this.pressureListener, this.sensorManager.getDefaultSensor(6), 3);
        }
        if (VERSION.SDK_INT >= 14) {
            if (this.sensorManager.getDefaultSensor(13) != null) {
                this.activatedSensors++;
                this.sensorManager.registerListener(this.tempListener, this.sensorManager.getDefaultSensor(13), 3);
            }
            if (this.sensorManager.getDefaultSensor(12) != null) {
                this.activatedSensors++;
                this.sensorManager.registerListener(this.humidityListener, this.sensorManager.getDefaultSensor(12), 3);
            }
        } else if (this.sensorManager.getDefaultSensor(7) != null) {
            this.activatedSensors++;
            this.sensorManager.registerListener(this.tempListener, this.sensorManager.getDefaultSensor(7), 3);
        }
        if (deviceState) {
            Intent batteryStatus = this.mainActivity.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            this.batteryLevel = (int) ((((float) batteryStatus.getIntExtra("level", -1)) * 100.0f) / ((float) batteryStatus.getIntExtra("scale", -1)));
            this.deviceModel = Build.MODEL;
        }
    }

    private void activateLightSensor() {
        if (this.sensorManager.getDefaultSensor(5) != null) {
            this.activatedSensors++;
            this.lightListener = new intSensorListener(5);
            this.sensorManager.registerListener(this.lightListener, this.sensorManager.getDefaultSensor(5), 3);
        }
    }

    private void activateUVSensor() {
    }

    private static double round(double value, int scale) {
        return ((double) Math.round(Math.pow(10.0d, (double) scale) * value)) / Math.pow(10.0d, (double) scale);
    }
}
