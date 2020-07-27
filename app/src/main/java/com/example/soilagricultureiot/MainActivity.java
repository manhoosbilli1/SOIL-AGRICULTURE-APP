package com.example.soilagricultureiot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "";
    Button btnSignup;
    Button btnSignIn;
    EditText tvEmail, tvPass;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnSignup = (Button) findViewById(R.id.btnSignup);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);
        tvEmail = (EditText) findViewById(R.id.tvEmail);
        tvPass = (EditText) findViewById(R.id.tvPass);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myref = mFirebaseDatabase.getReference();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tvEmail.getText().toString();
                String password = tvPass.getText().toString();
                if(!(email.isEmpty() && password.isEmpty())){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "CreateUserWithEmail:success");
                                        Toast.makeText(MainActivity.this, "Sign up Successful",
                                                Toast.LENGTH_SHORT).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        updateUI(user);

                                    } else {
                                        Toast.makeText(MainActivity.this, "Sign up Failed",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });
                }else {
                    tvEmail.setError("Please enter Email");
                    tvEmail.requestFocus();
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = tvEmail.getText().toString();
                String password = tvPass.getText().toString();
                if(!(email.isEmpty() && password.isEmpty())) {
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        Toast.makeText(MainActivity.this,
                                                "Sign In successful", Toast.LENGTH_SHORT).show();
                                        updateUI(user);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Sign In Failed",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null);
                                    }
                                }
                            });

                }else {
                    tvEmail.setError("Please enter Email");
                    tvEmail.requestFocus();
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser currentUser) {
        if (null != mAuth.getCurrentUser()){
            Intent i = new Intent(MainActivity.this, BasicActivity.class);
            startActivity(i);
        }
    }




}