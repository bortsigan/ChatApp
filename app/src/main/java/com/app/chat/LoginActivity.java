package com.app.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {

    MaterialEditText email, password;
    Button btnLogin;

    FirebaseAuth auth;
    DatabaseReference dbReference;

    TextView forgot_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        auth = FirebaseAuth.getInstance();

        email       = findViewById(R.id.email);
        password    = findViewById(R.id.password);
        btnLogin    = findViewById(R.id.btn_login);
        forgot_password = findViewById(R.id.forgot_password);

        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String emailTxt     = email.getText().toString();
                String passwordTxt   = password.getText().toString();

                if (emailTxt.isEmpty() || passwordTxt.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Missing Email or Password", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(emailTxt, passwordTxt)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    System.out.println("not successful : " + (task.isSuccessful()) );
                                    if (task.isSuccessful())  {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(
                                                LoginActivity.this,
                                                "Authentication failed",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                }
                            });
                }
            }
        });
    }
}