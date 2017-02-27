package ru.cardiacare.cardiacare.idt_ecg;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;
import ru.cardiacare.cardiacare.hisdocuments.BloodPressureActivity;
import ru.cardiacare.cardiacare.idt_ecg.drivers.EcgBleIdt;

public class ECGActivity extends AppCompatActivity {

    ECGView myView;
    static public Handler handler;
    public static Context mContext;
    static public Handler mHandler;

    private static final float TWO_INCHES = 2f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_ecg);
        Toolbar toolbar = (Toolbar) findViewById(R.id.ecg_activity_toolbar);
        setSupportActionBar(toolbar);
        assert toolbar != null;
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

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

        EcgBleIdt.mHandler = ECGActivity.handler;

        final Button StopButton = (Button) findViewById(R.id.stopButton);
        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ECGActivity.stopTimeService();
//                android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
//                alertDialog.setTitle(R.string.dialog_ecg_stop_title);
//                alertDialog.setMessage(R.string.dialog_ecg_stop_message);
//                alertDialog.setPositiveButton(R.string.dialog_ecg_stop_positive_button,
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                            }
//                        });
//                alertDialog.show();
            }
        });

        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message message) {
                ru.cardiacare.cardiacare.idt_ecg.ECGActivity.stopECGAlertDialog();
            }
        };
    }

    // Диалог, уведомляющий о прекращении работы с монитором
    static public void stopECGAlertDialog() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(mContext, R.style.AppCompatAlertDialogStyle);
        alertDialog.setTitle(R.string.dialog_ecg_stop_title);
        alertDialog.setMessage(R.string.dialog_ecg_stop_message);
        alertDialog.setPositiveButton(R.string.dialog_ecg_stop_positive_button,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mContext.startActivity(new Intent(mContext, MainActivity.class));
                    }
                });
        alertDialog.show();
    }

    private double setViewWidthInMillimeter() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float mXDpi = metrics.xdpi;
        double ppmm = mXDpi / 25.4f;
        return ppmm;
    }

    static public void stopTimeService() {
        ECGService ecgService = ECGService.returnService();
        ecgService.stopForeground(true);
//        ecgService.unbindService(BluetoothFindActivity.sConn);
        ecgService.stopSelf();
        ECGService.notificationManager.cancel(1);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    @Override
    protected void onDestroy() {
        myView.pulseTimer.cancel();
        myView.myTimer.cancel();
        super.onDestroy();
    }
}
