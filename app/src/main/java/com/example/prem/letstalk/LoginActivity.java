package com.example.prem.letstalk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {
    private EditText mEmail,mPassword;
    private Button mLogin;
    private Toolbar mToolbar;
    private ProgressDialog mProgLogin;
    private FirebaseAuth auth;
    private DatabaseReference mUserDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        auth = FirebaseAuth.getInstance();
        mToolbar=(Toolbar)findViewById(R.id.login_page);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEmail=(EditText)findViewById(R.id.login_email);
        mPassword=(EditText)findViewById(R.id.login_password);
        mProgLogin=new ProgressDialog(this);
        mLogin=(Button)findViewById(R.id.login_button);

        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mProgLogin.setTitle("Logging..");
                mProgLogin.setMessage("Logging To Your Account Please Wait..");
                mProgLogin.setCancelable(false);
                mProgLogin.show();

                LoginUser(email,password);
            }
        });
    }
    private void LoginUser(String email,final String password) {

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mProgLogin.hide();
                            // there was an error
                            if (password.length() < 6) {
                                mPassword.setError("Password Is Too Short");
                            } else {
                                Toast.makeText(LoginActivity.this, "Logged In Failed " + task.isSuccessful(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            mProgLogin.dismiss();
                            String current_user_id=auth.getCurrentUser().getUid();
                            String deviceToken= FirebaseInstanceId.getInstance().getToken();

                            mUserDatabase.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            });


                        }
                    }
                });
    }
}
