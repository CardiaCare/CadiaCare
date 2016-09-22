package ru.cardiacare.cardiacare;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by Yulia on 24.04.2015.
 */
public class Download extends ActionBarActivity implements View.OnClickListener {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InternetEcgFileService servEcg = new InternetEcgFileService();
        servEcg.execute();
    }

    @Override
    public void onClick(View v) {

    }
}
