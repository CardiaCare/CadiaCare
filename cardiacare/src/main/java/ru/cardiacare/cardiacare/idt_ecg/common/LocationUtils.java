package ru.cardiacare.cardiacare.idt_ecg.common;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;

import java.util.List;

public class LocationUtils {
    public double alt = 0.0d;
    public int cid = 0;
    private boolean gps_enabled = false;
    private boolean is_active_gps = false;
    private boolean is_active_network = false;
    public int lac = 0;
    public double lat = 0.0d;
    private LocationListener locListener = null;
    private LocationManager locManager = null;
    public double lon = 0.0d;
    private Activity mainActivity;
    public int mcc = 0;
    public int mnc = 0;
    private boolean network_enabled = false;
    public double precision = 0.0d;
    public int rssi = 0;
    private Handler stopHandler = null;
    private Runnable stopManager;
    static public String wifi = "";
    public int wifirssi = 0;

    class MyLocationListener implements LocationListener {
        MyLocationListener() {
        }

        public void onLocationChanged(Location location) {
            if (location != null) {
                LocationUtils.this.fillLocation(location);
                if (LocationUtils.this.is_active_network) {
                    LocationUtils.this.is_active_network = false;
                } else if (LocationUtils.this.is_active_gps) {
                    LocationUtils.this.is_active_gps = false;
                }
            }
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    public LocationUtils(Activity activity) {
        this.mainActivity = activity;
        if (this.mainActivity != null) {
            this.locManager = (LocationManager) this.mainActivity.getSystemService("location");
        }
    }

    public String getJSONPart() {
        String str = "";
        if (this.lon > 0.0d) {
            str = "\"lon\":\"" + this.lon + "\"";
        }
        if (this.lat > 0.0d) {
            if (str != "") {
                str = new StringBuilder(String.valueOf(str)).append(",").toString();
            }
            str = new StringBuilder(String.valueOf(str)).append("\"lat\":\"").append(this.lat).append("\"").toString();
        }
        if (this.alt > 0.0d) {
            if (str != "") {
                str = new StringBuilder(String.valueOf(str)).append(",").toString();
            }
            str = new StringBuilder(String.valueOf(str)).append("\"alt\":\"").append(this.alt).append("\"").toString();
        }
        if (this.precision > 0.0d) {
            if (str != "") {
                str = new StringBuilder(String.valueOf(str)).append(",").toString();
            }
            str = new StringBuilder(String.valueOf(str)).append("\"precision\":\"").append(this.precision).append("\"").toString();
        }
        if (this.cid > 0) {
            if (str != "") {
                str = new StringBuilder(String.valueOf(str)).append(",").toString();
            }
            str = new StringBuilder(String.valueOf(str)).append("\"cellid\":\"").append(this.lac).append(":").append(this.cid).append(":").append(this.mnc).append(":").append(this.mcc).append("\"").toString();
        }
        if (this.wifi.isEmpty()) {
            return str;
        }
        if (str != "") {
            str = new StringBuilder(String.valueOf(str)).append(",").toString();
        }
        return new StringBuilder(String.valueOf(str)).append("\"wifi\":{\"mac\":\"").append(this.wifi).append("\",\"rssi\":\"").append(this.wifirssi).append("\"}").toString();
    }

    public void Start(boolean finePrecision) {
        this.lon = 0.0d;
        this.lat = 0.0d;
        this.alt = 0.0d;
        this.precision = 0.0d;
        this.mnc = 0;
        this.mcc = 0;
        this.cid = 0;
        this.lac = 0;
        this.rssi = 0;
        this.wifi = "";
        this.wifirssi = 0;
        if (this.locManager != null) {
            CellInfo info;
            this.locListener = new MyLocationListener();
            if (finePrecision) {
                try {
                    this.gps_enabled = this.locManager.isProviderEnabled("gps");
                } catch (Exception e) {
                }
            }
            try {
                this.network_enabled = this.locManager.isProviderEnabled("network");
            } catch (Exception e2) {
            }
            fillLocation(this.locManager.getLastKnownLocation("gps"));
            if (this.gps_enabled) {
                this.is_active_gps = true;
                this.locManager.requestLocationUpdates("gps", 0, 1.0f, this.locListener);
                this.stopHandler = new Handler();
                this.stopManager = new Runnable() {
                    public void run() {
                        LocationUtils.this.locManager.removeUpdates(LocationUtils.this.locListener);
                    }
                };
                this.stopHandler.postDelayed(this.stopManager, 180000);
            }
            if (this.network_enabled) {
                this.is_active_network = true;
                if (this.lon == 0.0d) {
                    fillLocation(this.locManager.getLastKnownLocation("network"));
                }
                this.locManager.requestSingleUpdate("network", this.locListener, null);
                TelephonyManager tel = (TelephonyManager) this.mainActivity.getSystemService("phone");
                if (tel != null) {
                    String networkOperator = tel.getNetworkOperator();
                    if (networkOperator != null) {
                        if (!networkOperator.isEmpty()) {
                            this.mcc = Integer.parseInt(networkOperator.substring(0, 3));
                            this.mnc = Integer.parseInt(networkOperator.substring(3));
                        }
                        CellLocation cell = tel.getCellLocation();
                        if (cell != null) {
                            if (cell instanceof GsmCellLocation) {
                                GsmCellLocation cellLocation = (GsmCellLocation) cell;
                                this.cid = cellLocation.getCid() & 65535;
                                this.lac = cellLocation.getLac() & 65535;
                            } else if (cell instanceof CdmaCellLocation) {
                                CdmaCellLocation cellLocation2 = (CdmaCellLocation) cell;
                                this.cid = cellLocation2.getNetworkId();
                                this.lac = cellLocation2.getSystemId();
                            }
                        }
                        if (this.lac == 65535) {
                            this.lac = 0;
                        }
                        if (this.cid == 65535) {
                            this.cid = -1;
                        }
                        List<CellInfo> infoList = tel.getAllCellInfo();
                        if (infoList != null && infoList.size() > 0) {
                            info = (CellInfo) infoList.get(0);
                            if (info instanceof CellInfoGsm) {
                                this.rssi = ((CellInfoGsm) info).getCellSignalStrength().getDbm();
                            } else if (info instanceof CellInfoCdma) {
                                this.rssi = ((CellInfoCdma) info).getCellSignalStrength().getDbm();
                            } else if (info instanceof CellInfoLte) {
                                this.rssi = ((CellInfoLte) info).getCellSignalStrength().getDbm();
                            }
                        }
                    }
                }
            }
//            WifiManager wifiManager = (WifiManager) this.mainActivity.getSystemService("wifi");
            WifiManager wifiManager = (WifiManager) this.mainActivity.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager != null && wifiManager.isWifiEnabled()) {
                WifiInfo wifiinfo;
                wifiinfo = wifiManager.getConnectionInfo();
                if (wifiinfo != null) {
                    this.wifirssi = wifiinfo.getRssi();
                    if (this.wifirssi != -127) {
                        this.wifi = wifiinfo.getMacAddress();
                    } else {
                        this.wifirssi = 0;
                    }
                }
            }
        }
    }

    public void Stop() {
        if (this.is_active_gps || this.is_active_network) {
            if (this.stopHandler != null) {
                this.stopHandler.removeCallbacks(this.stopManager);
            }
            this.is_active_gps = false;
            this.is_active_network = false;
            this.locManager.removeUpdates(this.locListener);
            this.locListener = null;
        }
    }

    public boolean isActive() {
        if (this.is_active_gps || this.is_active_network) {
            return true;
        }
        return false;
    }

    private void fillLocation(Location location) {
        if (location != null) {
            this.lon = location.getLongitude();
            this.lat = location.getLatitude();
            this.alt = location.getAltitude();
            this.precision = round((double) location.getAccuracy(), 2);
        }
    }

    private static double round(double value, int scale) {
        return ((double) Math.round(Math.pow(10.0d, (double) scale) * value)) / Math.pow(10.0d, (double) scale);
    }
}
