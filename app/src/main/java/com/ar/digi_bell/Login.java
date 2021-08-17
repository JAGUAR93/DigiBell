package com.ar.digi_bell;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ar.digi_bell.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {
    EditText emailId, password,  loginPass, phoneNo;
    Button btnSignIn, tvSignUp,loginNumBtn, loginEmailBtn, code, btnSignInPhn;
    FirebaseAuth mFirebaseAuth;
     private String codeSent;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

            mFirebaseAuth = FirebaseAuth.getInstance();
            emailId = findViewById(R.id.emailIdL);
            password = findViewById(R.id.passwordL);
            btnSignIn = findViewById(R.id.loginCnfrmx);
            tvSignUp = findViewById(R.id.signuplx);
            loginNumBtn = findViewById(R.id.loginNum);
            loginPass = findViewById(R.id.passwordPhn);
            phoneNo = findViewById(R.id.phnNO);
            loginEmailBtn = findViewById(R.id.loginEmailBtn);
            code =  findViewById(R.id.code);
            btnSignInPhn = findViewById(R.id.btnSignInPhn);
            mAuthStateListener = firebaseAuth -> {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                    Toast.makeText(Login.this,"You are logged in",Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Login.this, ScanReceive.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(Login.this,"Please Login",Toast.LENGTH_SHORT).show();
                }
            };



            btnSignIn.setOnClickListener(v -> {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                }
                else  if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else  if(email.isEmpty() && pwd.isEmpty()){
                    Toast.makeText(Login.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else  if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(Login.this, task -> {
                        if(!task.isSuccessful()){
                            Toast.makeText(Login.this,"Login Error, Please Login Again",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Intent intToHome = new Intent(Login.this,ScanReceive.class);
                            startActivity(intToHome);
                        }
                    });

                }
                else{
                    Toast.makeText(Login.this,"Error Occurred!",Toast.LENGTH_SHORT).show();

                }

            });

            tvSignUp.setOnClickListener(v -> {
                Intent intSignUp = new Intent(Login.this, SignUp.class);
                startActivity(intSignUp);
                overridePendingTransition(0, 0);
            });

            loginNumBtn.setOnClickListener(v -> {
                emailId.setVisibility(View.INVISIBLE);
                password.setVisibility(View.INVISIBLE);
                loginNumBtn.setVisibility(View.INVISIBLE);
                loginPass.setVisibility(View.VISIBLE);
                phoneNo.setVisibility(View.VISIBLE);
                loginEmailBtn.setVisibility(View.VISIBLE);
                code.setVisibility(View.VISIBLE);
                btnSignInPhn.setVisibility(View.VISIBLE);
                btnSignIn.setVisibility(View.INVISIBLE);
            });

            loginEmailBtn.setOnClickListener(v -> {
                emailId.setVisibility(View.VISIBLE);
                password.setVisibility(View.VISIBLE);
                loginNumBtn.setVisibility(View.VISIBLE);
                loginPass.setVisibility(View.INVISIBLE);
                phoneNo.setVisibility(View.INVISIBLE);
                loginEmailBtn.setVisibility(View.INVISIBLE);
                code.setVisibility(View.INVISIBLE);
                btnSignInPhn.setVisibility(View.INVISIBLE);
                btnSignIn.setVisibility(View.VISIBLE);
            });

            code.setOnClickListener(v->{
                sendVerificationCode();
            });

            btnSignInPhn.setOnClickListener(v->{
                String phNo = phoneNo.getText().toString();
                String vCode = loginPass.getText().toString().trim();
                if(phNo.isEmpty()){
                    emailId.setError("Please enter Phone Number");
                    emailId.requestFocus();
                }
                else  if(vCode.isEmpty()){
                    password.setError("Please enter verification Code");
                    password.requestFocus();
                }else  if(phNo.isEmpty() && vCode.isEmpty()){
                    Toast.makeText(Login.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();
                }
                else  if(!(phNo.isEmpty() && vCode.isEmpty())){
                    verifySignInCode();

                    if (vCode.isEmpty() || vCode.length() < 6) {
                        loginPass.setError("Enter valid code");
                        loginPass.requestFocus();
                        return;
                    }
                    verifyVerificationCode(vCode);
                }

            });



        }

    private void verifySignInCode() {
        String vCode = loginPass.getText().toString();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, vCode);
   signInWithPhoneAuthCredential(credential);
    }


    private void sendVerificationCode() {
        String phone = phoneNo.getText().toString();
        if(phone.isEmpty()){
            phoneNo.setError("Phone Number is required");
            phoneNo.requestFocus();
            return;
        }
        if(phone.length() != 10){
            phoneNo.setError("Please enter a valid phone");
            phoneNo.requestFocus();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91"+phone, 60, TimeUnit.SECONDS, this, mCallbacks
        );
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if(code != null){
                phoneNo.setText(code);
                verifyVerificationCode(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
            Toast.makeText(Login.this, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e("TAG",e.getMessage() );
        }

        @Override
        public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void verifyVerificationCode(String code){

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(Login.this,
                        task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(Login.this, ScanReceive.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);

                            } else {

                                String message = "Something is wrong, we will fix it soon...";

                                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                    message = "Invalid code entered...";
                                }
                                Toast.makeText(Login.this,message,Toast.LENGTH_SHORT).show();
                            }
                        });
    }
}