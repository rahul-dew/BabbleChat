package com.overlord.babblechat.signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.overlord.babblechat.R;
import com.overlord.babblechat.common.NodeNames;
import com.overlord.babblechat.login.LoginActivity;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etName, etPassword, etConfirmPassword;
    String email, name, password, confirmPassword;
    private DatabaseReference databaseReference;

    private FirebaseUser firebaseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etName = findViewById(R.id.etName);
    }

    private void updateOnlyName(){
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(etName.getText().toString().trim()).build();

        firebaseUser.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference().child(NodeNames.USERS);
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put(NodeNames.NAME, etName.getText().toString().trim());
                    hashMap.put(NodeNames.EMAIL, etEmail.getText().toString().trim());
                    hashMap.put(NodeNames.ONLINE, "true");
                    hashMap.put(NodeNames.PHOTO, "");

                    databaseReference.child(userID).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(SignupActivity.this, R.string.user_created_success,
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            }
                            else{

                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignupActivity.this,
                            getString(R.string.failed_profile_update, task.getException()) ,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void btnSignupClick(View v){
        email = etEmail.getText().toString().trim();
        name = etName.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        confirmPassword = etConfirmPassword.getText().toString().trim();

        if(email.equals("")){
            etEmail.setError(getString(R.string.enter_email));
        }
        else if (name.equals("")){
            etName.setError(getString(R.string.enter_name));
        }
        else if (password.equals("")){
            etPassword.setError(getString(R.string.enter_password));
        }
        else if (confirmPassword.equals("")){
            etConfirmPassword.setError(getString(R.string.confirm_password));
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            etEmail.setError(getString(R.string.enter_correct_email));
        }
        else if(!password.equals(confirmPassword)){
            etConfirmPassword.setError(getString(R.string.password_mismatch));
        }
        else{
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                firebaseUser = firebaseAuth.getCurrentUser();
                                updateOnlyName();
                            }
                            else{
                                Toast.makeText(SignupActivity.this,
                                        getString(R.string.user_signup_failed, task.getException()),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}