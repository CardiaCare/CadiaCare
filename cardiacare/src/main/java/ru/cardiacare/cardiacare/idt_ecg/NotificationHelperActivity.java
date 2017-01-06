package ru.cardiacare.cardiacare.idt_ecg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

//Обработчик событий из виджета в статус-баре

public class NotificationHelperActivity extends Activity {

    static ECGService ecgService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String action = (String) getIntent().getExtras().get("DO");
        if (action.equals("app")) {
            Intent intent = new Intent(this, ECGActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intent);
        }
        if (action.equals("stopservice")) {
            BluetoothFindActivity.ecgService.stopForeground(true);
            BluetoothFindActivity.stopTimeService();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
