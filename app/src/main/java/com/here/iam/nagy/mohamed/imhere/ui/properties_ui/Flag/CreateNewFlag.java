package com.here.iam.nagy.mohamed.imhere.ui.properties_ui.Flag;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.here.iam.nagy.mohamed.imhere.R;

public class CreateNewFlag extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_flag);

        View decorView = getWindow().getDecorView();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        // Hide status bar
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Hide App name
//        getSupportActionBar().setDisplayShowTitleEnabled(false);

    }

}
