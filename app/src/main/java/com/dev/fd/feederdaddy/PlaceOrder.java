package com.dev.fd.feederdaddy;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.dev.fd.feederdaddy.Common.Common;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Service.ListenOrder;
import com.dev.fd.feederdaddy.ViewHolder.BillAdapter;
import com.dev.fd.feederdaddy.ViewHolder.CartAdapter;
import com.dev.fd.feederdaddy.model.Order;
import com.dev.fd.feederdaddy.model.Request;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.hoang8f.widget.FButton;


public class PlaceOrder extends AppCompatActivity {

    ImageView imgrest,imggoback,imgeditaddress,imgeditime;
    TextView txtrestname,txtdeliverycharge,txtaddress,txttotalamount,txtrating,txtdeliverytime,txteditaddress,txtpaytm,txttime;
    RatingBar ratingBar;
    FButton btnconfirmorder;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    SharedPreferences sharedPreferences;

    String deliverycharge,restaurantid,deliveryrate,restimageurl;

    List<Order> cart = new ArrayList<>();
    BillAdapter adapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference currentrequestsref,totalordersref,restaurantinforef;

    String totalordersstr,phone,username,address,userlatitude,userlongitude,city,zone,maxamount,isbakery="0",Paymentmethod="COD",restareaname;
    int total=0,t=0;
    CardView cvsetordertime;

    RadioGroup rgchoosepaymentmethod;
    RadioButton rbcod, rbpaytm,rbtez,rbphonepe;

    public static Intent listenservice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city","N/A");
        zone = sharedPreferences.getString("zone","N/A");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        currentrequestsref = firebaseDatabase.getReference("CurrentRequests");
        totalordersref = firebaseDatabase.getReference("TotalOrders");

        totalordersref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                totalordersstr = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        imggoback = findViewById(R.id.imggoback);
        imgeditaddress = findViewById(R.id.imgeditaddress);
        imgrest = findViewById(R.id.imgrest);
        txtaddress = findViewById(R.id.txtaddress);
        txtdeliverycharge = findViewById(R.id.txtdeliverycharge);
        txtrestname = findViewById(R.id.txtrestname);
        txttotalamount = findViewById(R.id.txttotalamount);
        ratingBar = findViewById(R.id.ratbar);
        txtrating = findViewById(R.id.txtrating);
        txtdeliverytime = findViewById(R.id.txtdeliverytime);
        txteditaddress= findViewById(R.id.txteditaddress);
        btnconfirmorder = findViewById(R.id.btnconfirmorder);

        rgchoosepaymentmethod = findViewById(R.id.rgpaymentmethod);
        rbcod = findViewById(R.id.rbcashondelivery);
        rbpaytm = findViewById(R.id.rbpaytm);
        rbtez = findViewById(R.id.rbtez);
        rbphonepe = findViewById(R.id.rbphonepe);


        cvsetordertime = findViewById(R.id.cvchoosetime);


        /*txtpaytm = findViewById(R.id.txtpaytm);
        txtpaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(t==0) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("net.one97.paytm");
                   t=1;
                    startActivity( launchIntent );

                }
                else if(t==1) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.nbu.paisa.user");
                    t=2;
                    startActivity( launchIntent );

                }
                else if(t==2) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.phonepe.app");
                    t=0;
                    if(launchIntent!=null)
                    startActivity( launchIntent );

                }
//net.one97.paytm is package name of Paytm App
            }
        });*/

        Typeface face = Typeface.createFromAsset(getAssets(),"NABILA.TTF");
        txtrestname.setTypeface(face);


        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        restaurantid = sharedPreferences.getString("restaurantid","N/A");
        //rest info ref
        restaurantinforef = firebaseDatabase.getReference(city).child("Restaurant").child(restaurantid);


        loadpage();



        //Init bill recycler view
        recyclerView= findViewById(R.id.recycler_bill);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        imgeditaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceOrder.this,ProfileActivity.class);
                intent.putExtra("comeback","yes");
                startActivityForResult(intent,1);

            }
        });
        txteditaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaceOrder.this,ProfileActivity.class);
                intent.putExtra("comeback","yes");
                startActivity(intent);

            }
        });

        btnconfirmorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRequest();
            }
        });


        rgchoosepaymentmethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.rbcashondelivery)
                {
                    Paymentmethod="COD";
                }
                else if(checkedId==R.id.rbpaytm)
                {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("net.one97.paytm");
                    Paymentmethod = "PAYTM";
                    if(launchIntent!=null)
                    startActivity( launchIntent );
                    else
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=net.one97.paytm")));
                }
                else if(checkedId==R.id.rbtez)
                {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.google.android.apps.nbu.paisa.user");
                    Paymentmethod = "TEZ";
                    if(launchIntent!=null)
                        startActivity( launchIntent );
                    else
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.nbu.paisa.user")));
                }
                else if(checkedId==R.id.rbphonepe)
                {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.phonepe.app");
                    Paymentmethod = "PHONEPE";
                    if(launchIntent!=null)
                        startActivity( launchIntent );
                    else
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.phonepe.app")));

                }
            }
        });


    }

    private void loadpage() {
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        //deliveryrate = sharedPreferences.getString("deliveryrate","N/A");

        address = sharedPreferences.getString("address","N/A");
        phone  = sharedPreferences.getString("phone","N/A");
        username = sharedPreferences.getString("name","N/A");
        userlatitude = sharedPreferences.getString("latitude","N/A");
        userlongitude = sharedPreferences.getString("longitude","N/A");

        restaurantinforef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.child("areaname").getValue()!=null)
                restareaname = dataSnapshot.child("areaname").getValue().toString();
                else
                    restareaname="";

                txtrestname.setText(dataSnapshot.child("name").getValue().toString());
                ratingBar.setRating(Float.parseFloat(dataSnapshot.child("rating").getValue().toString()));

                Double drating = Double.parseDouble(dataSnapshot.child("rating").getValue().toString());
                String rat = String.format("%.1f",drating);
                txtrating.setText(rat);

                isbakery = dataSnapshot.child("isbakery").getValue().toString();

                restimageurl = dataSnapshot.child("image").getValue().toString();
                Picasso.with(getBaseContext()).load(dataSnapshot.child("image").getValue().toString()).fit().centerCrop().into(imgrest);
                txtaddress.setText(address);


                //set delivery time and charge

                Double distance = getDistanceFromLatLonInKm(Double.parseDouble(userlatitude)
                        ,Double.parseDouble(userlongitude)
                        ,Double.parseDouble(dataSnapshot.child("latitude").getValue().toString())
                        ,Double.parseDouble(dataSnapshot.child("longitude").getValue().toString()));
                String dist = String.format("%.2f",distance);

                //Double rate = Double.parseDouble(deliveryrate);
                Double charge;
                maxamount = dataSnapshot.child("maxamount").getValue().toString();

                cart = new Database(PlaceOrder.this).getCarts();
                //calculate total price
                total = 0;
                for(Order order:cart)
                    total+=(Integer.parseInt(order.getPrice()))*Integer.parseInt(order.getQuantity());
                if(restaurantid.equals("1") && total>500)
                {
                    if(distance<4.0) charge=20.0;
                    else charge= distance*5.0;

                    charge = (charge*total)/500;
                }
                else if(total>=Integer.parseInt(maxamount))
                {
                    charge=0.0;
                }
                else if(distance<4.0)
                {
                    charge = 20.0;
                }
                else {
                    charge = distance * 5.0;
                }
                deliverycharge = String.format("%.0f",charge);
                txtdeliverycharge.setText("₹"+deliverycharge);

                //txt delivery time
                Double time = 20.0+ (distance/0.2);
                String timestr = String.format("%.0f",time);
                time+=15.0;
                String timestr1 = String.format("%.0f",time);
                txtdeliverytime.setText("Delivery Time : "+timestr+"-"+timestr1+" min");

                loadListFood();

                if (isbakery.equals("1")) {
                    cvsetordertime.setVisibility(View.VISIBLE);

                    imgeditime = findViewById(R.id.imgedittime);
                    txttime = findViewById(R.id.txttime);


                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    txttime.setText(hour + ":" + minute);
                    imgeditime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            Calendar mcurrentTime = Calendar.getInstance();
                            final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                            final int minute = mcurrentTime.get(Calendar.MINUTE);
                            TimePickerDialog mTimePicker;
                            mTimePicker = new TimePickerDialog(PlaceOrder.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    String shour,sminute;
                                    shour = String.valueOf(selectedHour);
                                    sminute = String.valueOf(selectedMinute);
                                    if(selectedHour==0)
                                        shour="00";
                                    if(selectedMinute==0)
                                        sminute="00";

                                    if(!(shour.equals("00") && sminute.equals("00")) && selectedHour<hour)
                                    {
                                        Toast.makeText(PlaceOrder.this, "Please set time after current time", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(selectedHour==hour && selectedMinute<minute)
                                    {
                                        Toast.makeText(PlaceOrder.this, "Please set time after current time", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                    txttime.setText(shour + ":" + sminute);
                                }
                            }, hour, minute, true);//Yes 24 hour time
                            mTimePicker.setTitle("Select Delivery Time");
                            mTimePicker.show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void updateRequest() {
        if(totalordersstr!=null)
        {
            int totalordersint = Integer.parseInt(totalordersstr);
            totalordersint-=1;
            totalordersref.setValue(String.valueOf(totalordersint));
            String ttordersstr = String.valueOf(totalordersint);

            String Orderreceivetime = "NOW";
            if(isbakery.equals("1"))
            {
                Orderreceivetime = txttime.getText().toString();
            }

            String OrderStatus,OrderStatusMessage;
            if(Paymentmethod.equals("COD"))
            {OrderStatus = "0";
              OrderStatusMessage ="Waiting for order confirmation...";
            }
            else {
                OrderStatus = "1";
                OrderStatusMessage = "Waiting for payment confirmation of ₹"+String.valueOf(total)+" on 8853795495";
            }

            //currentrequestsref.child(ttordersstr).child("restaurantname").setValue();
            Request request = new Request(
                    cart,
                    "null",
                    "0",
                    "null",
                    txtrestname.getText().toString(),
                    restareaname,
                    restaurantid,
                    restimageurl,
                    username,
                    phone,
                    address,
                    zone,
                    String.valueOf(totalordersint),
                    String.valueOf(System.currentTimeMillis()),
                    String.valueOf(total),
                    deliverycharge,
                    OrderStatus,
                    OrderStatusMessage,
                    Orderreceivetime,
                    Paymentmethod
            );
            //submit to firebase
            currentrequestsref.child(ttordersstr).setValue(request);

            new Database(getBaseContext()).cleanCart();
            Toast.makeText(PlaceOrder.this, "Thankyou Order Placed !", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PlaceOrder.this,Orders.class);
            startActivity(intent);
            finish();

            //regiuster service
            listenservice = new Intent(PlaceOrder.this,ListenOrder.class);
            startService(listenservice);
        }
        else
        {
            Toast.makeText(this, "Please check your internet connection !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                if(result.equals("1"))
                {
                    loadpage();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }



    private void loadListFood() {

        cart = new Database(this).getCarts();
        adapter = new BillAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        //calculate total price
        total = 0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*Integer.parseInt(order.getQuantity());
        total+=Integer.parseInt(deliverycharge);
        //Locale locale = new Locale("","US");
        //NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txttotalamount.setText("₹"+total);

        rbpaytm.setText("Pay ₹"+total+ " through Paytm on 8853795495"  );
        rbtez.setText("Pay ₹"+total+ " through Tez on 8853795495"  );
        rbphonepe.setText("Pay ₹"+total+ " through PhonePe on 8853795495"  );

    }

    private double getDistanceFromLatLonInKm(double lat1,double lon1,double lat2,double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }


}
