package com.example.osekeacademy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
    private boolean signInWithGoogle = false;
    private EditText name;
    private EditText password;
    private TextView signUpText;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = findViewById(R.id.editUsername);
        password = findViewById(R.id.editPassword);
        signUpText = findViewById(R.id.signUpText);
        signUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpPageActivity.class);
                startActivity(intent);
                finish();return;
            }
        });


    }






    public void googleLoginButton(View view){
        if(!signInWithGoogle){
            signInWithGoogle = true;
            name.setHint("Email Address");
        }else{
            signInWithGoogle = false;
            name.setHint("USERNAME");
        }
    }




    public void loginButton(View view) {
        if(signInWithGoogle){
            if(!isEmpty()) {
                Toast.makeText(this, "you are signing in with your google account!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();return;
            }
        }else{
            if(!isEmpty()) {
                Toast.makeText(this, "you are signing in with oseke account!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();return;
            }
        }
    }



    private boolean isEmpty(){
        if(TextUtils.isEmpty(name.getText().toString())){
            name.setError("Required");
            return true;
        }
        if(TextUtils.isEmpty(password.getText().toString())){
            password.setError("Required");
            return true;
        }
        return false;
    }
}
