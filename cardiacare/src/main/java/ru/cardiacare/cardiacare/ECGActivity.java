package ru.cardiacare.cardiacare;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.os.Handler;
import android.widget.RelativeLayout;

/**
 * created by Zavyalova Yuliya on 21.12.14
 * PetrSU, 2014. 22305 group
 */
public class ECGActivity extends ActionBarActivity {

    final int Data = 1;
    ECGView myView;
    private int[] viewDemoSignal;

    //private BluetoothService mBluetoothService = null;
    private static final String TAG = "ECGActivity";

    private static final float TWO_INCHES = 2f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate ECGActivity Activity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecg);

        Toolbar toolbar = (Toolbar) findViewById(R.id.ecg_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.ecg));

        RelativeLayout v = (RelativeLayout) findViewById(R.id.ecg_view);
        myView = new ECGView(this,setViewWidthInMillimeter());

        v.addView(myView);

        Resources res = getResources();
        viewDemoSignal = res.getIntArray(R.array.demosignal);

        myView.getECGData(viewDemoSignal);


        Handler handler;

        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case Data:
                        int[] readBuf = (int[]) msg.obj;
                        //String strIncom = new String(readBuf, 0, msg.arg1);
                        //mytext.setText("Данные от Arduino: " + strIncom);
                        //Log.i ("TAG","Данные от Alive: " + readBuf.length);
                        myView.getECGData(readBuf);
                }
            };
        };
        //mBluetoothService = new BluetoothService(getApplicationContext(), handler);
    }

    private double setViewWidthInMillimeter() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float mXDpi = metrics.xdpi;
        double ppmm = mXDpi / 25.4f;
        return ppmm;
    }


    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy ECGActivity Activity");
        myView.pulseTimer.cancel();
        myView.myTimer.cancel();
        super.onDestroy();
    }
}
