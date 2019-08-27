package com.example.osekeacademy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class SignUpPageActivity extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;
    private TextView signUpTextView;
    private FirebaseDatabase database;
    private ProgressBar progressBar;
    private Button buttonSignUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        username = findViewById(R.id.signUpUsername);
        email = findViewById(R.id.signUpEmail);
        password = findViewById(R.id.signUpPassword);
        confirmPassword = findViewById(R.id.signUpConfirmPassword);
        signUpTextView = findViewById(R.id.signUpTextView);
        progressBar = findViewById(R.id.signUpProgressbar);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();return;
            }
        });

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();return;
        }

    }


    private boolean isEmpty(){
        if(TextUtils.isEmpty(username.getText().toString())){
            username.setError("Required");
            return true;
        }
        if(TextUtils.isEmpty(email.getText().toString())){
            email.setError("Required");
            return true;
        }if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("Required");
            return true;
        }if(TextUtils.isEmpty(confirmPassword.getText().toString())){
            confirmPassword.setError("Required");
            return true;
        }

        return false;
    }




    public void signUpButton(View view){
        if(!isEmpty()) {
            if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                Toast.makeText(this, "password and confirm passsword does not match!", Toast.LENGTH_SHORT).show();
            }if(!email.getText().toString().trim().matches(emailPattern)){
                email.setError("required");
            }
            else{
                Inprogress(true);
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    UserClass user = new UserClass();
                                    user.setEmail(email.getText().toString());
                                    user.setUsername(username.getText().toString());
                                    String key = task.getResult().getUser().getUid();
                                   new FirebaseHelperClass().AddUser(user, key, new FirebaseHelperClass.DataStatus() {
                                       @Override
                                       public void dataIsLoaded(List<UserClass> user, List<String> key) {

                                       }

                                       @Override
                                       public void dataIsInserted() {
                                           Inprogress(false);
                                           Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                           startActivity(intent);
                                           finish();return;

                                       }

                                       @Override
                                       public void dataIsUpdated() {

                                       }

                                       @Override
                                       public void dataIsDeleted() {

                                       }
                                   });
                                }

                                // ...
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Inprogress(false);
                        Toast.makeText(SignUpPageActivity.this, "sign in failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            }
        }

        private void Inprogress(boolean x){
            if(x){
                progressBar.setVisibility(View.VISIBLE);
                buttonSignUp.setEnabled(false);
                signUpTextView.setVisibility(View.INVISIBLE);
            }else{
                progressBar.setVisibility(View.INVISIBLE);
                buttonSignUp.setEnabled(true);
                signUpTextView.setVisibility(View.VISIBLE);
            }
        }
    }

