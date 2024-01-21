package com.example.prem.letstalk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

public class MomentActivity extends AppCompatActivity {

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moment);
        mToolbar = (Toolbar) findViewById(R.id.moment_Appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Let's Talk");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu_moment,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
