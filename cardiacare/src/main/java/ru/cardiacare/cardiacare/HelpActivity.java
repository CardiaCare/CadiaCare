package ru.cardiacare.cardiacare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class HelpActivity extends AppCompatActivity {

    Boolean cardText1 = false;
    Boolean cardText2 = false;
    Boolean cardText3 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final CardView cardView1 = (CardView) findViewById(R.id.card_view);
        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardText1 = !cardText1;
                if (cardText1) {
                    TextView textView = (TextView) findViewById(R.id.info_text);
                    textView.setText(R.string.about_pass_survey_long);
                } else {
                    TextView textView = (TextView) findViewById(R.id.info_text);
                    textView.setText(R.string.about_pass_survey_short);
                }
            }
        });

        final CardView cardView2 = (CardView) findViewById(R.id.card_view2);
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardText2 = !cardText2;
                if (cardText2) {
                    TextView textView = (TextView) findViewById(R.id.info_text2);
                    textView.setText(R.string.about_blood_pressure_long);
                } else {
                    TextView textView = (TextView) findViewById(R.id.info_text2);
                    textView.setText(R.string.about_blood_pressure_short);
                }
            }
        });

        final CardView cardView3 = (CardView) findViewById(R.id.card_view3);
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardText3 = !cardText3;
                if (cardText3) {
                    TextView textView = (TextView) findViewById(R.id.info_text3);
                    textView.setText(R.string.about_ecg_long);
                } else {
                    TextView textView = (TextView) findViewById(R.id.info_text3);
                    textView.setText(R.string.about_ecg_short);
                }
            }
        });
    }
}
