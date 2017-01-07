package ru.cardiacare.cardiacare.idt_ecg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import ru.cardiacare.cardiacare.R;

//Обработчик событий из виджета в статус-баре

public class NotificationHelperActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("QQQ", "NotificationHelperActivity, CREATECREATECREATECREATECREATECREATE");
//        setContentView(R.layout.activity_ecg);

        String action = (String) getIntent().getExtras().get("DO");
        if (action.equals("app")) {
            Log.i("QQQ", "NotificationHelperActivity, OPENOPENOPEN");
            Intent intent = new Intent(this, ECGActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
//            new ServiceNotification(this);
//            ServiceNotification.mNotificationManager.notify(548853, ServiceNotification.notification);

        }
        if (action.equals("stopservice")) {
            Log.i("QQQ", "NotificationHelperActivity, STOPSTOPSTOP");
//            BluetoothFindActivity.ecgService.stopForeground(true);
//            BluetoothFindActivity.stopTimeService();
            ECGActivity.stopTimeService();
        }
        if (action.equals("fromecgactivity")) {
            Log.i("QQQ", "NotificationHelperActivity, fromecgactivityfromecgactivityfromecgactivityfromecgactivityfromecgactivity");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("QQQ", "NotificationHelperActivity, RESUMERESUMERESUMERESUMERESUME");
    }

    @Override
    public void onRestart() {
        super.onRestart();
        Log.i("QQQ", "NotificationHelperActivity, RESTARTRESTARTRESTARTRESTARTRESTART");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("QQQ", "NotificationHelperActivity, PAUSEPAUSEPAUSEPAUSEPAUSE");
    }

    @Override
    public void onStop() {
//        super.onStop();
        Log.i("QQQ", "NotificationHelperActivity, STOPSTOPSTOPSTOPSTOPSTOP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
