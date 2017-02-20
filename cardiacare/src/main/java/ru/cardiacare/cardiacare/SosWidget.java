package ru.cardiacare.cardiacare;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/* Виджет "ТРЕВОГА" */
// При необходимости - расскомментировать код

//public class SosWidget extends AppWidgetProvider {
//
//    final static String ACTION_WIDGET = "ru.cardiacare.cardiacare.open_from_widget";
//
//    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
//                         int[] appWidgetIds) {
//        super.onUpdate(context, appWidgetManager, appWidgetIds);
//        // Обновление всех экземпляров виджета
//        for (int i : appWidgetIds) {
//            updateWidget(context, appWidgetManager, i);
//        }
//    }
//
//    public void onDeleted(Context context, int[] appWidgetIds) {
//        super.onDeleted(context, appWidgetIds);
//    }
//
//    static void updateWidget(Context ctx, AppWidgetManager appWidgetManager,
//                             int widgetID) {
//        RemoteViews widgetView = new RemoteViews(ctx.getPackageName(),
//                R.layout.widget_sos);
//
//        // Обработка нажатия на виджет
//        Intent widgetIntent = new Intent(ctx, MainActivity.class);
//        widgetIntent.setAction(ACTION_WIDGET);
//        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID);
//        PendingIntent pIntent = PendingIntent.getActivity(ctx, widgetID,
//                widgetIntent, 0);
//        widgetView.setOnClickPendingIntent(R.id.SOSButton, pIntent);
//
//        // Обновление виджета
//        appWidgetManager.updateAppWidget(widgetID, widgetView);
//    }
//
//    public void onReceive(Context context, Intent intent) {
//        super.onReceive(context, intent);
//    }
//}

// Заменить метод onStart() в MainActivity() на следующий:

//@Override
//protected void onStart() {
//        super.onStart();
//        backgroundFlag = 0;
//        Intent intent = getIntent();
//        // Проверяем каким способом запущено приложение (обычным или через виджет)
//        if ((intent.getAction() != null) && (intent.getAction().equalsIgnoreCase("ru.cardiacare.cardiacare.open_from_widget"))) {
//        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
//        Bundle extras = intent.getExtras();
//        if (extras != null) {
//        mAppWidgetId = extras.getInt(
//        AppWidgetManager.EXTRA_APPWIDGET_ID,
//        AppWidgetManager.INVALID_APPWIDGET_ID);
//        }
//        // Если приложение запустили с помощью виджета "ТРЕВОГА"
//        // то отправляем сигнал SOS и открываем экстренный опросник
//        if (mAppWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
//        setLoadingScreen();
//        if (patientUriFlag == 1) {
//        if ((isNetworkAvailable(context)) && (gps.canGetLocation())) {
//        alarmButtonFlag = false;
//        backgroundFlag = 1;
//        QuestionnaireHelper.showAlarmQuestionnaire(context);
//        }
//        } else {
//        Toast toast = Toast.makeText(this,
//        R.string.unregistered_user_toast, Toast.LENGTH_SHORT);
//        toast.show();
//        }
//        }
//        // Если приложение запустили обычным способом
//        } else {
//        // Условие выполняется только для авторизированного пользователя
//        if (patientUriFlag == 1) {
//        // Если с момента последнего прохождения периодического опроса прошла минута, то
//        // делаем иконку опроса красной. Короткий промежуток времени (1 минута) - для демонстрации
//        Long timestamp = System.currentTimeMillis() / 1000;
//        String ts = timestamp.toString();
//        Integer time = Integer.parseInt(ts) - Integer.parseInt(storage.getLastQuestionnairePassDate());
//        Integer period;
//        // Если приод прохождения опроса задан пользователем, то обновляем согласно данному периоду
//        // Иначе ставим период по умолчанию (1 минута)
//        if (!storage.getPeriodPassServey().equals("")) {
//        period = Integer.parseInt(storage.getPeriodPassServey());
//        } else {
//        period = 60;
//        storage.setPeriodPassServey("60");
//        }
//        if (time >= period) {
//        serveyButton.setBackgroundResource(R.drawable.servey);
//        } else {
//        serveyButton.setBackgroundResource(R.drawable.servey_white);
//        }
//        }
//        }
//        }

// Добавить в манифест:
//<receiver
//        android:name=".SosWidget">
//        <intent-filter>
//        <action
//        android:name="android.appwidget.action.APPWIDGET_UPDATE">
//        </action>
//        </intent-filter>
//        <meta-data
//        android:name="android.appwidget.provider"
//        android:resource="@xml/widget_metadata">
//        </meta-data>
//        </receiver>