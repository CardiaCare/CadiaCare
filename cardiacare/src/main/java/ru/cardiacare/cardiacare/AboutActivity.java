package ru.cardiacare.cardiacare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/* Страница справки */

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Справка");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_about);
        Toolbar toolbar = (Toolbar) findViewById(R.id.about_activity_toolbar);
        setSupportActionBar(toolbar);

    }
}