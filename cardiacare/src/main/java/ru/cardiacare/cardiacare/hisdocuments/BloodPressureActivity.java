package ru.cardiacare.cardiacare.hisdocuments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.petrsu.cardiacare.smartcare.hisdocuments.ResultBloodPressure;

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

/* Экран "Результаты измерения артериального давления" */

public class BloodPressureActivity extends AppCompatActivity {

    ListView listView1;
    BPAdapter adapter;
    LinkedList<ResultBloodPressure> bp_data;
    Button addButton;
    int itemRow;

    EditText SYSText;
    EditText DAText;
    SimpleDateFormat sdf;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_results_blood);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sdf = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        String currentDateandTime = sdf.format(new Date());

        context = this;

//        bp_data = new LinkedList<ResultBloodPressure>();
//        bp_data.add(new ResultBloodPressure("110", "80","70", currentDateandTime));
//        bp_data.add(new ResultBloodPressure("115", "80","70", currentDateandTime));
//        bp_data.add(new ResultBloodPressure("120", "80","70", currentDateandTime));
//        bp_data.add(new ResultBloodPressure("125", "80","70", currentDateandTime));
//        bp_data.add(new ResultBloodPressure("130", "80","70", currentDateandTime));

        bp_data = new LinkedList<ResultBloodPressure>();


        LinkedList<ResultBloodPressure> bpl  = new LinkedList<ResultBloodPressure>();

        bpl = readLastBPMeasuremetsFromFile();
        if (bpl == null){
            bp_data.add(new ResultBloodPressure("0","0","0","0:0:0"));
        }
        else bp_data = bpl;

        SYSText = (EditText) findViewById(R.id.systolicEditText);
        DAText = (EditText) findViewById(R.id.diastolicEditText);

        adapter = new BPAdapter(this, R.layout.item_blood_pressure, bp_data);

        listView1 = (ListView)findViewById(R.id.bpListView);
        listView1.setAdapter(adapter);

        listView1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long arg3) {

                itemRow  = position;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle(R.string.dialog_bp_title);
                alertDialog.setMessage(R.string.dialog_bp_del);
                alertDialog.setNegativeButton(R.string.dialog_cancel, null);
                alertDialog.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.remove(bp_data.get(itemRow));
                        adapter.notifyDataSetChanged();
                    }
                });
                alertDialog.show();

                return false;
            }
        });

        addButton = (Button)findViewById(R.id.addBPButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDateandTime = sdf.format(new Date());
                ResultBloodPressure rbp = new ResultBloodPressure(SYSText.getText().toString(),DAText.getText().toString(),"",currentDateandTime.toString());
                bp_data.addFirst(rbp);
                if (bp_data.size() > 5)
                    bp_data.removeLast();
                adapter.notifyDataSetChanged();
                SYSText.setText("");
                DAText.setText("");
            }
        });

    }

    public LinkedList<ResultBloodPressure> readLastBPMeasuremetsFromFile(){
        try
        {
            FileInputStream fin = openFileInput("lbp.txt");
            ObjectInputStream in = new ObjectInputStream(fin);
            LinkedList<ResultBloodPressure> myList = (LinkedList<ResultBloodPressure>) in.readObject();
            in.close();
            fin.close();
            return myList;
        }
        catch(IOException e){
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void writeLastBPMeasuremetsFromFile(LinkedList<ResultBloodPressure> myList){
        try
        {
            FileOutputStream fout  = context.openFileOutput("lbp.txt", context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fout);
            out.writeObject(myList);
            fout.close();
            Log.i("TAG",myList.get(0)+"");
            out.close();
        }
        catch(IOException e){
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
        MainActivity.backgroundFlag = 0;
        bp_data = readLastBPMeasuremetsFromFile();
    }

    @Override
    public void onPause() {
        writeLastBPMeasuremetsFromFile(bp_data);
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        MainActivity.backgroundFlag = 1;
        writeLastBPMeasuremetsFromFile(bp_data);
        super.onBackPressed();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ResultBloodPressureHolder holder = null;

            if(row == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new ResultBloodPressureHolder();
                holder.sysTitle = (TextView) row.findViewById(R.id.sysTextList);
                holder.daTitle = (TextView)row.findViewById(R.id.daTextList);
                holder.dateTitle = (TextView)row.findViewById(R.id.dateTextList);

                row.setTag(holder);
            }
            else
            {
                holder = (ResultBloodPressureHolder)row.getTag();
            }

            ResultBloodPressure resultBloodPressure = data.get(position);
            holder.sysTitle.setText(resultBloodPressure.getSystolicPressure());
            holder.daTitle.setText(resultBloodPressure.getDiastolicPressure());
            holder.dateTitle.setText(resultBloodPressure.getTime());


            return row;
        }

        class ResultBloodPressureHolder
        {
            TextView sysTitle;
            TextView daTitle;
            TextView dateTitle;
        }
    }