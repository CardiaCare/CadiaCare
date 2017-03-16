package ru.cardiacare.cardiacare.MainFragments;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;
import ru.cardiacare.cardiacare.hisdocuments.BloodPressureActivity;
import ru.cardiacare.cardiacare.idt_ecg.ECGService;
import ru.cardiacare.cardiacare.survey.QuestionnaireHelper;
import ru.cardiacare.cardiacare.user.Userdata;

// Интерфейс для зарегистрированного пользователя
// Маленькие иконки, используется в паре с ViewPager

public class FragmentRegisteredScreenSmallIcons extends Fragment {

    public static final String TAG = "FragmentRegisteredScreenSmallIcons";

    String[] textArray;
    public static Resources resources;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resources = getResources();
        textArray = new String[]{resources.getText(R.string.pass_survey).toString(), resources.getText(R.string.bp).toString(), resources.getText(R.string.ecg).toString(), resources.getText(R.string.account).toString()};
        View view = inflater.inflate(R.layout.fragment_menu_icons, null);
        GridView gridview = (GridView) view.findViewById(R.id.gridview);
        gridview.setAdapter(new AdapterSmallIcons(MainActivity.mContext, textArray));
        gridview.setOnItemClickListener(gridviewOnItemClickListener);
        gridview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    return true;
                }
                return false;
            }
        });
        return view;
    }

    public FragmentRegisteredScreenSmallIcons() {
    }

    private GridView.OnItemClickListener gridviewOnItemClickListener = new GridView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            switch (position) {
                // Пройти опрос
                case 0:
                    if (MainActivity.isNetworkAvailable(MainActivity.mContext)) {
                        QuestionnaireHelper.showQuestionnaire(MainActivity.mContext);
                    } else {
                        MainActivity.wiFiAlertDialog();
                    }
                    break;
                // Дневник давления
                case 1:
                    if (MainActivity.isNetworkAvailable(MainActivity.mContext)) {
                        startActivity(new Intent(MainActivity.mContext, BloodPressureActivity.class));
                    } else {
                        MainActivity.wiFiAlertDialog();
                    }
                    break;
                // ЭКГ
                case 2:
                    boolean isGPS = true;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        final LocationManager manager = (LocationManager) MainActivity.mContext.getSystemService(Context.LOCATION_SERVICE);
                        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            isGPS = false;
                            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.mContext, R.style.AppCompatAlertDialogStyle);
                            alertDialog.setTitle(R.string.dialog_gps_title);
                            alertDialog.setMessage(R.string.dialog_gps_message);
                            alertDialog.setPositiveButton(R.string.dialog_gps_positive_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Переход к настройкам GPS
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    MainActivity.mContext.startActivity(intent);
                                }
                            });
                            alertDialog.setNegativeButton(R.string.dialog_gps_negative_button, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alertDialog.show();
                        } else {
                            isGPS = true;
                        }
                    }
                    if (isGPS) {
                        MainActivity.myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (MainActivity.myBluetoothAdapter == null || !MainActivity.myBluetoothAdapter.isEnabled()) {
                            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(MainActivity.mContext, R.style.AppCompatAlertDialogStyle);
                            alertDialog.setTitle(R.string.dialog_bluetooth_title);
                            alertDialog.setMessage(R.string.dialog_bluetooth_message);
                            alertDialog.setPositiveButton(R.string.dialog_bluetooth_positive_button,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            MainActivity.myBluetoothAdapter.enable();
                                        }
                                    });

                            alertDialog.setNegativeButton(R.string.dialog_bluetooth_negative_button,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                            alertDialog.show();
                        } else {
                            // Если монитор уже работает в фоновом режиме, то сразу открываем ECGActivity, иначе BluetoothFindActivity
                            if (MainActivity.isMyServiceRunning(ECGService.class)) {
                                Intent intent = new Intent(MainActivity.mContext, ru.cardiacare.cardiacare.idt_ecg.ECGActivity.class);
                                startActivity(intent);
                            } else {
                                Intent intentBluetoothFind = new Intent(MainActivity.mContext, ru.cardiacare.cardiacare.idt_ecg.BluetoothFindActivity.class);
                                startActivity(intentBluetoothFind);
                            }
                        }
                    }
                    break;
                // Личный кабинет
                case 3:
                    startActivity(new Intent(MainActivity.mContext, Userdata.class));
                    break;
                default:
                    break;
            }
        }
    };
}
