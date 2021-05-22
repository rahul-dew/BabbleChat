package com.overlord.babblechat.password;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.overlord.babblechat.R;

import org.jetbrains.annotations.NotNull;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextView tvMessage;
    private LinearLayout llResetPassword, llMessage;
    private Button btRetry, btClose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        etEmail = findViewById(R.id.etEmail);
        tvMessage = findViewById(R.id.tvMessage);
        llResetPassword = findViewById(R.id.llResetPassword);
        llMessage = findViewById(R.id.llMessage);
        btRetry = findViewById(R.id.btRetry);
        btClose = findViewById(R.id.btRetry);
    }

    public void btResetPasswordClick(View v){
        String email = etEmail.getText().toString().trim();
        if(email.equals("")){
            etEmail.setError(getString(R.string.enter_email));
        }
        else{
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    llResetPassword.setVisibility(View.GONE);
                    llMessage.setVisibility(View.VISIBLE);

                    if(task.isSuccessful()){
                        tvMessage.setText(getString(R.string.reset_password_instruction, email));
                        new CountDownTimer(60000, 1000){
                            @Override
                            public void onTick(long l) {
                                btRetry.setText(getString(R.string.resend_timer,
                                        String.valueOf(l/1000)));
                            }

                            @Override
                            public void onFinish() {
                                btRetry.setText(R.string.retry);
                                btRetry.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        llResetPassword.setVisibility(View.VISIBLE);
                                        llMessage.setVisibility(View.GONE);
                                    }
                                });
                            }
                        }.start();
                    }
                    else{
                        tvMessage.setText(getString(R.string.failed_send_email, task.getException()));
                        btRetry.setText(R.string.retry);
                        btRetry.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                llResetPassword.setVisibility(View.VISIBLE);
                                llMessage.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            });
        }
    }

    public void btCloseClick(View v){
        finish();
    }
}