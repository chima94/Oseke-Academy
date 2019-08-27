package com.example.osekeacademy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;


public class MainActivity extends AppCompatActivity {
	private boolean signInWithGoogle = false;
	private EditText email;
	private EditText password;
	private TextView signUpText;
	private ProgressBar loginProgressBar;
	private Button signInButton;
	static final int GOOGLE_SIGN = 1;
	String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
	private FirebaseAuth mAuth;
	private GoogleSignInClient googleSignInClient;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		email = findViewById(R.id.editEmail);
		password = findViewById(R.id.editPassword);
		signUpText = findViewById(R.id.signUpText);
		loginProgressBar = findViewById(R.id.loginProgressBar);
		signInButton = findViewById(R.id.signInButton);
		SignInButton signInButton = findViewById(R.id.sign_in_button);
		signInButton.setSize(SignInButton.SIZE_STANDARD);
		mAuth = FirebaseAuth.getInstance();

		GoogleSignInOptions googleSignOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();
		googleSignInClient = GoogleSignIn.getClient(this, googleSignOption);


		signUpText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(getApplicationContext(), SignUpPageActivity.class);
				startActivity(intent);
				finish();
				return;
			}
		});

		FirebaseUser user = mAuth.getCurrentUser();
		UpdateUI(user);


		signInButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Inprogress(true);
				GoogleSignIn();
			}
		});

	}



	public void loginButton(View view) {
		if (!isEmpty()) {
			if (email.getText().toString().trim().matches(emailPattern)) {
				Inprogress(true);
				mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
					@Override
					public void onSuccess(AuthResult authResult) {
						Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
						startActivity(intent);
						finish();
						return;
					}
				}).addOnFailureListener(new OnFailureListener() {
					@Override
					public void onFailure(@NonNull Exception e) {
						Inprogress(false);
						Toast.makeText(MainActivity.this, "sign in failed" + e.getMessage(), Toast.LENGTH_LONG).show();
					}
				});
			} else {
				Toast.makeText(this, "Invalid email address!", Toast.LENGTH_SHORT).show();
			}
		}
	}


	private void GoogleSignIn() {
		Intent signInIntent = googleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, GOOGLE_SIGN);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == GOOGLE_SIGN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				GoogleSignInAccount account = task.getResult(ApiException.class);
				firebaseAuthWithGoogle(account);
			} catch (ApiException e) {
				e.printStackTrace();
			}
		}
	}


	private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
		AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
		mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
			@Override
			public void onComplete(@NonNull Task<AuthResult> task) {
				if(task.isSuccessful()){
					FirebaseUser user = mAuth.getCurrentUser();
					Inprogress(false);
					UpdateUI(user);
				}else{
					Inprogress(false);
					Toast.makeText(MainActivity.this, "sign in failed!", Toast.LENGTH_SHORT).show();
				}
			}
		});

	}


	private void UpdateUI(FirebaseUser user){
		GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
		if(user != null){
			Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
			startActivity(intent);
			finish();return;
		}

		if(account != null){
			String name = account.getDisplayName();
			Toast.makeText(this, ""+name, Toast.LENGTH_SHORT).show();
		}
	}


	private boolean isEmpty() {
		if (TextUtils.isEmpty(email.getText().toString())) {
			email.setError("Required");
			return true;
		}
		if (TextUtils.isEmpty(password.getText().toString())) {
			password.setError("Required");
			return true;
		}
		return false;
	}


	private void Inprogress(boolean x) {
		if (x) {
			loginProgressBar.setVisibility(View.VISIBLE);
			signInButton.setEnabled(false);
			signUpText.setVisibility(View.INVISIBLE);
		} else {
			loginProgressBar.setVisibility(View.INVISIBLE);
			signInButton.setEnabled(true);
			signUpText.setVisibility(View.VISIBLE);
		}
	}


}


