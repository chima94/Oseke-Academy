package com.example.osekeacademy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {
	private boolean signInWithGoogle = false;
	private EditText email;
	private EditText password;
	private TextView signUpText;
	private TextView googleSignInText;
	private ProgressBar loginProgressBar;
	private Button signInButton;
	String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
	private FirebaseAuth mAuth;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		email = findViewById(R.id.editEmail);
		password = findViewById(R.id.editPassword);
		signUpText = findViewById(R.id.signUpText);
		googleSignInText = findViewById(R.id.googleSignInTextView);
		loginProgressBar = findViewById(R.id.loginProgressBar);
		signInButton = findViewById(R.id.signInButton);
		mAuth = FirebaseAuth.getInstance();


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
			googleSignInText.setVisibility(View.VISIBLE);
		}else{
			signInWithGoogle = false;
		}
	}




	public void loginButton(View view) {
		if(signInWithGoogle){
			if(!isEmpty()) {
				if (email.getText().toString().trim().matches(emailPattern)) {
					Toast.makeText(this, "you are signing in with your google account!", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
					startActivity(intent);
					finish();
					return;
				}else{
					Toast.makeText(this, "Invalid email address!", Toast.LENGTH_SHORT).show();
				}
			}
		}else{
			if(!isEmpty()) {
				if(email.getText().toString().trim().matches(emailPattern)) {
					Inprogress(true);
					mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
						@Override
						public void onSuccess(AuthResult authResult) {
							Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
							startActivity(intent);
							finish();return;
						}
					}).addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception e) {
							Inprogress(false);
							Toast.makeText(MainActivity.this, "sign in failed"+e.getMessage(), Toast.LENGTH_LONG).show();
						}
					});
				}else{
					Toast.makeText(this, "Invalid email address!", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}



	private boolean isEmpty(){
		if(TextUtils.isEmpty(email.getText().toString())){
			email.setError("Required");
			return true;
		}
		if(TextUtils.isEmpty(password.getText().toString())){
			password.setError("Required");
			return true;
		}
		return false;
	}


	private void Inprogress(boolean x){
		if(x){
			loginProgressBar.setVisibility(View.VISIBLE);
			signInButton.setEnabled(false);
			signUpText.setVisibility(View.INVISIBLE);
		}else{
			loginProgressBar.setVisibility(View.INVISIBLE);
			signInButton.setEnabled(true);
			signUpText.setVisibility(View.VISIBLE);
		}
	}
}
