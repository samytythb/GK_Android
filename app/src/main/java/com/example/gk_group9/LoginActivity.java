package com.example.gk_group9;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {
    private EditText edtuser,edtpass;
    private Button btnregist, btnlogin;
    private boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edtuser=findViewById(R.id.edtuser);
        edtpass=findViewById(R.id.edtpass);
        btnregist = findViewById(R.id.btnregist);
        btnlogin=findViewById(R.id.btnlogin);
        edtpass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int Right=2;
                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
                    if(motionEvent.getRawX()>=edtpass.getRight()-edtpass.getCompoundDrawables()[Right].getBounds().width()){
                        int selection =edtpass.getSelectionEnd();
                        if(passwordVisible){
                            edtpass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_off_24,0);
                            edtpass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else{
                            edtpass.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.baseline_visibility_24,0);
                            edtpass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        edtpass.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        // Write a message to the database
        btnregist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent int1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(int1);
            }
        });
    }
    private void login(){
        String username,password;
        username=edtuser.getText().toString();
        password=edtpass.getText().toString();
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this,"Username can't be blank",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Password can't not be blank",Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userFound = false;
                long userCount = dataSnapshot.getChildrenCount();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String password1 = userSnapshot.child("password").getValue(String.class);
                    String username1 = userSnapshot.child("username").getValue(String.class);
                    User user= new User(username1,password1);
                    if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                        userFound = true;
                        break;
                    }
                }
                if (userFound) {
                    Toast.makeText(getApplicationContext(),"Logged in successfully",Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Thực hiện chuyển đến Activity mới tại đây
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }, 1000);
                } else {
                    Toast.makeText(getApplicationContext(),"Login failed",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),"System error",Toast.LENGTH_SHORT).show();
                System.out.println("Read data failed: " + databaseError.getMessage());
            }
        });
    }
}