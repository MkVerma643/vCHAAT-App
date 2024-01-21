package com.example.prem.letstalk;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private EditText mStatus;
    private Button mSaveStatus;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mStatusDataBase;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mProgress=new ProgressDialog(this);

        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        String current_uid=mCurrentUser.getUid();

        mStatusDataBase= FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar=(Toolbar)findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value=getIntent().getStringExtra("status_value");

        mStatus=(EditText)findViewById(R.id.edit_your_status);
        mSaveStatus=(Button)findViewById(R.id.modify_your_status);

        mStatus.setText(status_value);

        mSaveStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgress=new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes..");
                mProgress.setMessage("Your status is Updating Please Wait..");
                mProgress.show();

                String status=mStatus.getText().toString();

                mStatusDataBase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"Some Error Has Occured",Toast.LENGTH_LONG).show();
                        }
                    }
                });

            }
        });
    }
}
