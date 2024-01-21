package com.example.prem.letstalk;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class StartActivity extends AppCompatActivity {
    private Button mRegBtn,mLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        mRegBtn=(Button)findViewById(R.id.start_reg_button);
        mLogin=(Button)findViewById(R.id.start_login_button);
        mRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reg_intent=new Intent(StartActivity.this,RegsiterActivity.class);
                startActivity(reg_intent);
            }
        });
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_intent=new Intent(StartActivity.this,LoginActivity.class);
                startActivity(login_intent);
            }
        });
    }
}
