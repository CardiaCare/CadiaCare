package ru.cardiacare.cardiacare.idt_ecg;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.RelativeLayout;

import ru.cardiacare.cardiacare.R;

public class ECGActivity extends AppCompatActivity {

    ECGView myView;
    static public Handler handler;

    private static final String TAG = "ECGActivity";
    private static final float TWO_INCHES = 2f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate ECGActivity Activity");
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ecg);

        RelativeLayout v = (RelativeLayout) findViewById(R.id.ecg_view);
        myView = new ECGView(this, setViewWidthInMillimeter());

        assert v != null;
        v.addView(myView);


        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case 1:
//                        Log.i("QQQ", "HANDLER WORK");
                        int[] readBuf = (int[]) msg.obj;
                        myView.getECGData(readBuf);
                }
            }

            ;
        };
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
