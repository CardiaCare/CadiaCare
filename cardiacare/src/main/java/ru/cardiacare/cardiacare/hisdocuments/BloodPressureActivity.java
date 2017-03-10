package ru.cardiacare.cardiacare.hisdocuments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.petrsu.cardiacare.smartcare.hisdocuments.ResultBloodPressure;

import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import ru.cardiacare.cardiacare.MainActivity;
import ru.cardiacare.cardiacare.R;
import ru.cardiacare.cardiacare.idt_ecg.ECGService;
import ru.cardiacare.cardiacare.survey.QuestionnaireActivity;

/* Экран "Результаты измерения артериального давления" */

public class BloodPressureActivity extends AppCompatActivity {

    ListView listView1;
    static BPAdapter adapter;
    static public LinkedList<ResultBloodPressure> bp_data;
    static LinkedList<ResultBloodPressure> bp_data2;
    FloatingActionButton addButton;
    int itemRow;

    EditText SYSText;
    EditText DAText;
    SimpleDateFormat sdf;
    static Context context;
    static Intent intent;

    static void refresh() {
        //Intent intent = context.getIntent();
        // context.finish();
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        context.startActivity(intent);
        adapter.notifyDataSetChanged();
        //  adapter = new BPAdapter(context, R.layout.item_blood_pressure, bp_data);

        if ((MainActivity.systolicBP != null) && (MainActivity.diastolicBP != null)) {
            MainActivity.systolicBP.clear();
            MainActivity.diastolicBP.clear();

            for (int j = 0; j < bp_data.size(); j++) {
//            Log.i("BPActivity", "systolic = " + bp_data.get(j).getSystolicPressure() + ", diastolic = " + bp_data.get(j).getDiastolicPressure());
            }

            if (bp_data.size() >= 7) {
                for (int i = 6; i >= 0; i--) {
                    MainActivity.systolicBP.add(Integer.parseInt(bp_data.get(i).getSystolicPressure()));
                    MainActivity.diastolicBP.add(Integer.parseInt(bp_data.get(i).getDiastolicPressure()));
                }
            } else {
                for (int i = bp_data.size() - 1; i >= 0; i--) {
                    MainActivity.systolicBP.add(Integer.parseInt(bp_data.get(i).getSystolicPressure()));
                    MainActivity.diastolicBP.add(Integer.parseInt(bp_data.get(i).getDiastolicPressure()));
                }
                for (int i = bp_data.size(); i < 7; i++) {
                    MainActivity.systolicBP.add(0);
                    MainActivity.diastolicBP.add(0);
                }
            }
            MainActivity.storage.setSystolicBP(MainActivity.systolicBP.toString());
            MainActivity.storage.setDiastolicBP(MainActivity.diastolicBP.toString());
//        Log.i("BPActivity", "systolic = " + MainActivity.systolicBP.toString() + "\n diastolic = " + MainActivity.diastolicBP.toString());
//        Log.i("BPActivity", "storageSystolic = " + MainActivity.storage.getSystolicBP() + "\n storageDiastolic = " + MainActivity.storage.getDiastolicBP());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BloodPressureGET bloodGet = new BloodPressureGET();
        bloodGet.execute();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_results_blood);
        setTitle("");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBlood);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BloodPressureActivity.this, MainActivity.class));
            }
        });

        //sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentDateandTime = sdf.format(new Date());

        context = this;
        intent = getIntent();

//        bp_data = new LinkedList<ResultBloodPressure>();
//        bp_data.add(new ResultBloodPressure("110", "80","70", currentDateandTime));
//        bp_data.add(new ResultBloodPressure("115", "80","70", currentDateandTime));
//        bp_data.add(new ResultBloodPressure("120", "80","70", currentDateandTime));
//        bp_data.add(new ResultBloodPressure("125", "80","70", currentDateandTime));
//        bp_data.add(new ResultBloodPressure("130", "80","70", currentDateandTime));

        bp_data = new LinkedList<ResultBloodPressure>();


        LinkedList<ResultBloodPressure> bpl = new LinkedList<ResultBloodPressure>();

        bpl = readLastBPMeasuremetsFromFile();
        if (bpl == null) {
            bp_data.add(new ResultBloodPressure("0", "0", "0", "0:0:0", 0));
        } else bp_data = bpl;

        SYSText = (EditText) findViewById(R.id.systolicEditText);
        DAText = (EditText) findViewById(R.id.diastolicEditText);

        adapter = new BPAdapter(this, R.layout.item_blood_pressure, bp_data);
        adapter.setNotifyOnChange(true);
        listView1 = (ListView) findViewById(R.id.bpListView);
        listView1.setAdapter(adapter);

        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {

                itemRow = position;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(R.string.dialog_bp_title);
                alertDialog.setMessage(R.string.dialog_bp_del);
                alertDialog.setNegativeButton(R.string.dialog_cancel, null);
                alertDialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        /////////////delete
                        JSONObject json = null;


                        try {
                            String str = "{\"id\":" + bp_data.get(itemRow).getIdPressure() + "}";
                            json = new JSONObject(str);
                            BloodPressureDELETE bloodDelete = new BloodPressureDELETE();
                            bloodDelete.execute(json);
                            adapter.remove(bp_data.get(itemRow));
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                        }


                    }
                });
                alertDialog.show();

                return false;
            }
        });

        addButton = (FloatingActionButton) findViewById(R.id.addBPButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isBPcorrect = false;
                try {
                    isBPcorrect = POSTsysdias(Integer.parseInt(SYSText.getText().toString()), Integer.parseInt(DAText.getText().toString()));
                } catch (Exception e) {
                }
                if (isBPcorrect) {
                    System.out.println("Test! blood ");
                    String currentDateandTime = sdf.format(new Date());
                    ResultBloodPressure rbp = new ResultBloodPressure(SYSText.getText().toString(), DAText.getText().toString(), "", currentDateandTime.toString(), 0);
                    bp_data.addFirst(rbp);
//                if (bp_data.size() > 5)
//                    bp_data.removeLast();
                    adapter.notifyDataSetChanged();
                    SYSText.setText("");
                    DAText.setText("");
                } else {
                    SYSText.setText("");
                    DAText.setText("");
                    android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
                    alertDialog.setTitle(R.string.dialog_bpdata_title);
                    alertDialog.setMessage(R.string.dialog_bpdata_message);
                    alertDialog.setNegativeButton(R.string.dialog_bpdata_negative_button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }
            }
        });

    }

    boolean POSTsysdias(int systolic, int diastolic) {
        JSONObject json = null;

        String str = "{ \"systolic\":" + systolic + ", "
                + "\"diastolic\":" + diastolic + "} ";
        if ((systolic <= diastolic) || (systolic <= 10) || (diastolic <= 10) || (systolic > 300) || (diastolic > 300)) {
            return false; // Некорректные значения давления
        } else {
            try {
                json = new JSONObject(str);

                BloodPressurePOST bloodPost = new BloodPressurePOST();
                bloodPost.execute(json);
            } catch (Exception e) {
            }
        }
        return true;
    }

    public LinkedList<ResultBloodPressure> readLastBPMeasuremetsFromFile() {
        try {
            FileInputStream fin = openFileInput("lbp.txt");
            ObjectInputStream in = new ObjectInputStream(fin);
            LinkedList<ResultBloodPressure> myList = (LinkedList<ResultBloodPressure>) in.readObject();
            in.close();
            fin.close();
            return myList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void writeLastBPMeasuremetsFromFile(LinkedList<ResultBloodPressure> myList) {
        try {
            FileOutputStream fout = context.openFileOutput("lbp.txt", context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(myList);
            fout.close();
//            Log.i("TAG",myList.get(0)+"");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        writeLastBPMeasuremetsFromFile(bp_data);
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bp_data = readLastBPMeasuremetsFromFile();
    }

    @Override
    public void onPause() {
        writeLastBPMeasuremetsFromFile(bp_data);
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        writeLastBPMeasuremetsFromFile(bp_data);
        startActivity(new Intent(this, MainActivity.class));
    }
}

class BPAdapter extends ArrayAdapter<ResultBloodPressure> {

    Context context;
    int layoutResourceId;
    LinkedList<ResultBloodPressure> data;

    public BPAdapter(Context context, int layoutResourceId, LinkedList<ResultBloodPressure> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        //_NotifyOnChange = true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ResultBloodPressureHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ResultBloodPressureHolder();
            holder.sysTitle = (TextView) row.findViewById(R.id.sysTextList);
            holder.daTitle = (TextView) row.findViewById(R.id.daTextList);
            holder.dateTitle = (TextView) row.findViewById(R.id.dateTextList);

            row.setTag(holder);
        } else {
            holder = (ResultBloodPressureHolder) row.getTag();
        }

        ResultBloodPressure resultBloodPressure = data.get(position);
        holder.sysTitle.setText(resultBloodPressure.getSystolicPressure());
        holder.daTitle.setText(resultBloodPressure.getDiastolicPressure());
        holder.dateTitle.setText(resultBloodPressure.getTime());


        return row;
    }

    class ResultBloodPressureHolder {
        TextView sysTitle;
        TextView daTitle;
        TextView dateTitle;
    }
}