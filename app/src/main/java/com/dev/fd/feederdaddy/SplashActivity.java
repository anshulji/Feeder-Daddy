package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.dev.fd.feederdaddy.Common.Common;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SplashActivity extends AppCompatActivity {

    //waiting duration
    private  int SPLASH_DISPLAY_LENGTH = 500;
    RelativeLayout logo,signuplayout;
    Button btnsendotp;
    EditText edtusername,edtphone;
    private FirebaseAuth mAuth;

    //Handler handler = new Handler();
    /*Runnable runnable = new Runnable() {
        @Override
        public void run() {
            logo.setVisibility(View.VISIBLE);
        }
    };*/

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logo = findViewById(R.id.fdlogo);
        signuplayout=findViewById(R.id.signup_layout);
        edtphone = findViewById(R.id.edtphone);
        edtusername = findViewById(R.id.edtusername);
        btnsendotp = findViewById(R.id.btnsendotp);

        /* New Handler to start the Menus-Activity
         * and close this Splash-Screen after some seconds.*/
        mAuth=FirebaseAuth.getInstance();
        final SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        final String phone = sharedPreferences.getString("phone","N/A");

        if(!phone.equals("N/A"))
        {
            SPLASH_DISPLAY_LENGTH=0;
        }

        //handler.postDelayed(runnable,1000);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                if(phone.equals("N/A"))
                { signuplayout.setVisibility(View.VISIBLE);
                }
                else {

                /* Create an Intent that will start the Menus-Activity. */
                    if(!Common.isConnectedToInternet(getBaseContext()))
                    {
                        Toast.makeText(SplashActivity.this, "Please Check Your Internet Connection !", Toast.LENGTH_LONG).show();
                    }

                    String city  = sharedPreferences.getString("city","N/A");

                    /*firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference(city).child("Restaurant");
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                            startActivity(mainIntent);
                            finish();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    */

                    Intent mainIntent = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(mainIntent);
                    finish();


                }
            }
        }, SPLASH_DISPLAY_LENGTH);

        btnsendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtusername.getText().toString().equals(""))
                {
                    Toast.makeText(SplashActivity.this, "Please Enter Username !", Toast.LENGTH_SHORT).show();
                }
                else if(edtphone.getText().toString().length()!=10)
                {
                    Toast.makeText(SplashActivity.this, "Please Enter Valid 10 digit Phone No.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(SplashActivity.this, SignUpActivity.class);
                    intent.putExtra("username", edtusername.getText().toString());
                    intent.putExtra("phone", edtphone.getText().toString());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser==null)
        {
        }
    }
}
