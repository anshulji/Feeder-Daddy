package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.dev.fd.feederdaddy.Common.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class EventCateringActivity extends AppCompatActivity {


    EditText etfeedback;
    Button btnfeedback;
    ImageView imggoback;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_catering2);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        imggoback = findViewById(R.id.imggoback);
        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("EventCatering");

        etfeedback = findViewById(R.id.etfeedback);
        btnfeedback = findViewById(R.id.btnsubmitfeedback);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        final String phone = sharedPreferences.getString("phone","N/A");

        btnfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etfeedback.getText().toString().equals(""))
                {
                    String key = databaseReference.push().getKey();

                    databaseReference.child(key).child("userphone").setValue(phone);
                    databaseReference.child(key).child("request").setValue(etfeedback.getText().toString());

                    Calendar mcurrentTime = Calendar.getInstance();
                    long currenttime = mcurrentTime.getTimeInMillis();
                    databaseReference.child(key).child("datetimeofrequest").setValue(Common.getDate(currenttime));

                    //databaseReference.child(phone).setValue(etfeedback.getText().toString());
                    Toast.makeText(EventCateringActivity.this, "Your request submitted successfully !", Toast.LENGTH_SHORT).show();
                }
                else
                    Toast.makeText(EventCateringActivity.this, "Please enter feedback!", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
