package com.example.gk_group9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG=RegisterActivity.class.getName();
    private EditText edtusername,edtpassword,edtrepass,edtphonenumber;
    private Button btnregister,btnback;
    private boolean passwordVisible;
    private boolean repasswordVisible;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        edtusername = findViewById(R.id.edtusername);
        edtpassword = findViewById(R.id.edtpassword);
        edtrepass = findViewById(R.id.edtrepass);
        edtphonenumber=findViewById(R.id.edtphonenumber);
        btnregister = findViewById(R.id.btnregister);
        btnback = findViewById(R.id.btnback);
        mAuth=FirebaseAuth.getInstance();
        edtpassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>=edtpassword.getRight()-edtpassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection =edtpassword.getSelectionEnd();
                        if(passwordVisible){
                            edtpassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);
                            edtpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else{
                            edtpassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_24,0);
                            edtpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        edtpassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        edtrepass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>=edtrepass.getRight()-edtrepass.getCompoundDrawables()[Right].getBounds().width()){
                        int selection =edtrepass.getSelectionEnd();
                        if(repasswordVisible){
                            edtrepass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);
                            edtrepass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            repasswordVisible=false;
                        }else{
                            edtrepass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_24,0);
                            edtrepass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            repasswordVisible=true;
                        }
                        edtrepass.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void register(){
        String username,password,repass,phonenumber;
        username=edtusername.getText().toString();
        password=edtpassword.getText().toString();
        repass=edtrepass.getText().toString();
        phonenumber=edtphonenumber.getText().toString();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this,"Username can't be blank",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Password can't not be blank",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(phonenumber)){
            Toast.makeText(this,"Phone number can't not be blank",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(repass)){
            Toast.makeText(this,"Password does not match",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!User.isValidPassword(password)){
            Toast.makeText(this,"Password must be at least 8 characters and must contain at least one lowercase letter, one uppercase letter, one digit and one special character (@, #, $, %, ^, &, +, =). In addition, the password must not contain spaces and special characters.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!User.isValidPhoneNumber(phonenumber)){
            Toast.makeText(this,"phone numbers in the format : +[2-digit country code] [9-digit number]",Toast.LENGTH_SHORT).show();
            return;
        }
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userFound = false;
                long userCount = dataSnapshot.getChildrenCount();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String password1 = userSnapshot.child("password").getValue(String.class);
                    String username1 = userSnapshot.child("username").getValue(String.class);
                    User user= new User(username1,password1);
                    if (user.getUsername().equals(username)) {
                        userFound = true;
                        break;
                    }
                }
                if (userFound) {
                    Toast.makeText(getApplicationContext(),"Username already exists",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    User newUser = new User(username,password,phonenumber);

                    onClickSendOtpCode(phonenumber,newUser);

//                    newUser.setCreatedAt(getCurrentDateTime());
//
//                    // Tự động tăng khóa
//                    String userKey = myRef.push().getKey();
//                    myRef.child(userKey).setValue(newUser);
//                    Toast.makeText(getApplicationContext(),"Register Success",Toast.LENGTH_SHORT).show();
//                    Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            // Thực hiện chuyển đến Activity mới tại đây
//                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
//                            startActivity(intent);
//                        }
//                    }, 1000);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"System error",Toast.LENGTH_SHORT).show();
                System.out.println("Read data failed: " + databaseError.getMessage());
            }
        });

    }

    private void onClickSendOtpCode(String phonenumber,User user){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phonenumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(RegisterActivity.this,"Verification Failed",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                super.onCodeSent(verificationId, forceResendingToken);
                                goToEnterOtpActivity(phonenumber,verificationId,user);
                                System.out.println("luc gui "+verificationId);
                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            goToMainActivity(user.getPhoneNumber());
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                Toast.makeText(RegisterActivity.this,"The verification code entered was valid",Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }
    private void goToMainActivity(String phoneNumber){
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra("phone_number",phoneNumber);
        startActivity(intent);
    }
    private void goToEnterOtpActivity(String phoneNumber,String verificationId,User user){
        Intent intent =new Intent(this,OtpActivity.class);
        System.out.println("luc chuyen"+verificationId);
        intent.putExtra("phone_number",phoneNumber);
        intent.putExtra("verificationId",verificationId);
        intent.putExtra("user_info",  user);
        startActivity(intent);
    }
}