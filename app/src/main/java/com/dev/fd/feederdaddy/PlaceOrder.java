package com.dev.fd.feederdaddy;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.dev.fd.feederdaddy.Common.Common;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Remote.APIService;
import com.dev.fd.feederdaddy.Service.ListenOrder;
import com.dev.fd.feederdaddy.ViewHolder.BillAdapter;
import com.dev.fd.feederdaddy.model.MyResponse;
import com.dev.fd.feederdaddy.model.Notification;
import com.dev.fd.feederdaddy.model.Order;
import com.dev.fd.feederdaddy.model.Request;
import com.dev.fd.feederdaddy.model.Sender;
import com.dev.fd.feederdaddy.model.Token;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.apache.commons.net.time.TimeTCPClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import info.hoang8f.widget.FButton;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlaceOrder extends AppCompatActivity {

    ImageView imgrest,imggoback,imgeditaddress;
    TextView txtrestname,txtdeliverycharge,txtaddress,txttotalamount,txtrating,txtdeliverytime,txteditaddress,txtpaytm,txtasap,txtschedule,txtsubltotal,txtpromocodename,txtpromocodeamount;
    RatingBar ratingBar;
    FButton btnconfirmorder;
    LinearLayout llpromobill;
    RelativeLayout rl1,rl2,rl3;

    CheckBox cbasap,cbschedule,cbthird;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    SharedPreferences sharedPreferences;

    String deliverycharge,restaurantid,restimageurl;
    double mindcdistance,mindeliverycharge,deliveryrate;
    String Orderreceivetime = "ASAP";
    int restclosetimehr=22,restclosetimemin=0;


    List<Order> cart = new ArrayList<>();
    BillAdapter adapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference currentrequestsref,totalordersref,restaurantinforef;

    String totalordersstr,phone,username,address,userlatitude,userlongitude,city,zone,maxamount,Paymentmethod="COD",restareaname,restphone,lat,lng;
    int total=0,t=0;
    CardView cvsetordertime;

    RadioGroup rgchoosepaymentmethod;
    RadioButton rbcod, rbpaytm,rbtez,rbphonepe;

    //public static Intent listenservice;
    APIService mService;

    EditText edtpromocode;
    TextView txtapplypc,txtpcas;
    private int subtotal,tp=0;
    String rqpromocode="null",rqpromoamount="null";
    private double charge;
    private double distance;
    private String timestr,timestr1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);


        //init service
        mService = Common.getFCMService();

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city","N/A");
        zone = sharedPreferences.getString("zone","N/A");
        lat = sharedPreferences.getString("latitude","N/A");
        lng = sharedPreferences.getString("longitude","N/A");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();
        currentrequestsref = firebaseDatabase.getReference("CurrentRequests");
        totalordersref = firebaseDatabase.getReference("TotalOrders");


        totalordersref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
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

        //promo bill
        llpromobill = findViewById(R.id.llbillpromo);
        txtsubltotal = findViewById(R.id.txtsubtotal);
        txtpromocodename = findViewById(R.id.txtbillpromoname);
        txtpromocodeamount = findViewById(R.id.txtpromoamount);

        edtpromocode = findViewById(R.id.edtpromocode);
        txtapplypc = findViewById(R.id.txtapplypromocode);
        txtpcas  = findViewById(R.id.txtpcas);

        txtasap = findViewById(R.id.txtasap);
        txtschedule = findViewById(R.id.txtschedule);
        cbasap = findViewById(R.id.cbasap);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl3 = findViewById(R.id.rl3);
        cbthird = findViewById(R.id.cbthird);

        cbschedule = findViewById(R.id.cbschedule);


        rgchoosepaymentmethod = findViewById(R.id.rgpaymentmethod);
        rbcod = findViewById(R.id.rbcashondelivery);
        rbpaytm = findViewById(R.id.rbpaytm);
        rbtez = findViewById(R.id.rbtez);
        rbphonepe = findViewById(R.id.rbphonepe);


        cvsetordertime = findViewById(R.id.cvchoosetime);


        //promo code logics
        txtapplypc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtapplypc.getText().equals("APPLY"))
                {
                    if(edtpromocode.getText().toString().equals(""))
                        Toast.makeText(PlaceOrder.this, "Please enter promo code first!", Toast.LENGTH_SHORT).show();
                    else
                    {   t=0;
                        firebaseDatabase = FirebaseDatabase.getInstance();
                        DatabaseReference promoref = firebaseDatabase.getReference(city).child("CouponCodes");
                        promoref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getValue()!=null) {

                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                        if (edtpromocode.getText().toString().equalsIgnoreCase(postSnapshot.child("name").getValue().toString())) {
                                            t = 1;
                                            txtpcas.setVisibility(View.VISIBLE);
                                            txtapplypc.setText("REMOVE");
                                            txtapplypc.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                            edtpromocode.setEnabled(false);
                                            edtpromocode.setText(postSnapshot.child("name").getValue().toString() + " - " +
                                                    postSnapshot.child("discount").getValue().toString() + "% OFF");
                                            edtpromocode.setTextColor(Color.parseColor("#1e90ff"));

                                            llpromobill.setVisibility(View.VISIBLE);
                                            txtsubltotal.setText("₹" + subtotal);
                                             txtpromocodename.setText("Promo - (" + postSnapshot.child("name").getValue().toString() + ")");

                                            int promodiscount = Integer.parseInt(postSnapshot.child("discount").getValue().toString());
                                            float promoamount = (promodiscount * subtotal) / 100;
                                            int promoamnt = (int) promoamount;
                                            txtpromocodeamount.setText("-₹" + promoamnt);
                                            int totalamnt = subtotal + Integer.parseInt(deliverycharge) - promoamnt;
                                            txttotalamount.setText("₹" + totalamnt);

                                            rqpromoamount = String.valueOf(promoamnt);
                                            rqpromocode = "Promo - (" + postSnapshot.child("name").getValue().toString() + ")";
                                        }
                                    }
                                    if (t == 0)
                                        Toast.makeText(PlaceOrder.this, "No Such Promo Code Exist !", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
                else
                {
                    edtpromocode.setText("");
                    edtpromocode.setEnabled(true);
                    edtpromocode.setTextColor(getResources().getColor(android.R.color.black));
                    txtpcas.setVisibility(View.GONE);
                    txtapplypc.setText("APPLY");
                    txtapplypc.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    llpromobill.setVisibility(View.GONE);
                    txttotalamount.setText("₹"+total);
                    rqpromoamount="null";
                    rqpromocode="null";
                }
            }
        });


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
                if(Common.isConnectedToInternet(getBaseContext()))
                updateRequest();
                else
                    Toast.makeText(PlaceOrder.this, "Please Check Your Internet !", Toast.LENGTH_SHORT).show();
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


        final ProgressDialog dialog =new ProgressDialog(PlaceOrder.this);
        dialog.setMessage("Please wait...");
        dialog.show();

        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        deliveryrate = Double.parseDouble(sharedPreferences.getString("deliveryrate","N/A"));
        mindcdistance = Double.parseDouble(sharedPreferences.getString("mindcdistance","N/A"));
        mindeliverycharge = Double.parseDouble(sharedPreferences.getString("mindeliverycharge","N/A"));

        address = sharedPreferences.getString("address","N/A");
        phone  = sharedPreferences.getString("phone","N/A");
        username = sharedPreferences.getString("name","N/A");
        userlatitude = sharedPreferences.getString("latitude","N/A");
        userlongitude = sharedPreferences.getString("longitude","N/A");

        restaurantinforef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()!=null) {

                    if (dataSnapshot.child("areaname").getValue() != null)
                        restareaname = dataSnapshot.child("areaname").getValue().toString();
                    else
                        restareaname = "";

                    restphone = dataSnapshot.child("restaurantphone").getValue().toString();

                    if (dataSnapshot.child("opentime").getValue() != null) {
                        restclosetimehr = Integer.parseInt(dataSnapshot.child("opentime").getValue().toString().substring(6, 8));
                        restclosetimemin = Integer.parseInt(dataSnapshot.child("opentime").getValue().toString().substring(9, 11));
                    }


                    txtrestname.setText(dataSnapshot.child("name").getValue().toString());
                    ratingBar.setRating(Float.parseFloat(dataSnapshot.child("rating").getValue().toString()));

                    Double drating = Double.parseDouble(dataSnapshot.child("rating").getValue().toString());
                    String rat = String.format("%.1f", drating);
                    txtrating.setText(rat);

                    restimageurl = dataSnapshot.child("image").getValue().toString();
                    Picasso.with(getBaseContext()).load(dataSnapshot.child("image").getValue().toString()).fit().centerCrop().into(imgrest);
                    txtaddress.setText(address);


                    //set delivery time and charge
                    distance = getDistanceFromLatLonInKm(Double.parseDouble(userlatitude)
                            , Double.parseDouble(userlongitude)
                            , Double.parseDouble(dataSnapshot.child("latitude").getValue().toString())
                            , Double.parseDouble(dataSnapshot.child("longitude").getValue().toString()));
                    String dist = String.format("%.2f", distance);

                    //Double rate = Double.parseDouble(deliveryrate);

                    if (dataSnapshot.child("maxamount").getValue() != null)
                        maxamount = dataSnapshot.child("maxamount").getValue().toString();
                    else
                        maxamount = "99999";

                    cart = new Database(PlaceOrder.this).getCarts();
                    //calculate total price
                    total = 0;
                    for (Order order : cart)
                        total += (Integer.parseInt(order.getPrice())) * Integer.parseInt(order.getQuantity());

                    if (restaurantid.equals("1")) {

                        if(cbasap.isChecked())
                        {
                            Orderreceivetime = "05:30 PM - 06:15 PM";
                        }

                        DatabaseReference dbref = firebaseDatabase.getReference(city).child("ShaChickenDC");
                        dbref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {

                                    mindcdistance = Double.parseDouble(dataSnapshot.child("mindcdistance").getValue().toString());
                                    deliveryrate = Double.parseDouble(dataSnapshot.child("deliverychargeperkm").getValue().toString());
                                    mindeliverycharge = Double.parseDouble(dataSnapshot.child("mindeliverycharge").getValue().toString());


                                    if (restaurantid.equals("1") && total > 500) {
                                        if (distance < mindcdistance) charge = mindeliverycharge;
                                        else charge = distance * deliveryrate;

                                        charge = (charge * total) / 500;

                                    } else if (total >= Integer.parseInt(maxamount)) {
                                        charge = 0.0;
                                    } else if (distance < mindcdistance) {
                                        charge = mindeliverycharge;
                                    } else {
                                        charge = distance * deliveryrate;
                                    }

                                    deliverycharge = String.format("%.0f", charge);
                                    txtdeliverycharge.setText("₹" + deliverycharge);


                                    //txt delivery time
                                    Double time = 25.0 + (distance / 0.2);
                                    String timestr = String.format("%.0f", time);
                                    time += 15.0;
                                    String timestr1 = String.format("%.0f", time);
                                    txtdeliverytime.setText("Delivery Time : " + timestr + "-" + timestr1 + " min");

                                    loadListFood();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        if (restaurantid.equals("1") && total > 500) {
                            if (distance < mindcdistance) charge = mindeliverycharge;
                            else charge = distance * deliveryrate;

                            charge = (charge * total) / 500;

                        } else if (total >= Integer.parseInt(maxamount)) {
                            charge = 0.0;
                        } else if (distance < mindcdistance) {
                            charge = mindeliverycharge;
                        } else {
                            charge = distance * deliveryrate;
                        }

                        deliverycharge = String.format("%.0f", charge);
                        txtdeliverycharge.setText("₹" + deliverycharge);

                        //txt delivery time
                        Double time = 25.0 + (distance / 0.2);
                        timestr = String.format("%.0f", time);
                        time += 15.0;
                        timestr1 = String.format("%.0f", time);
                        txtdeliverytime.setText("Delivery Time : " + timestr + "-" + timestr1 + " min");

                        loadListFood();
                    }


                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);

                    txtasap.setText("ASAP : " + timestr + "-" + timestr1 + " min");
                    if (restaurantid.equals("1")) {
                        if(hour >= 20 && (minute>40 || hour>20))
                        {
                            Toast.makeText(PlaceOrder.this, "Sha Chicken orders are closed for today!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if(hour >= 19 && (minute>40))
                        {
                            rl1.setVisibility(View.GONE);
                            rl2.setVisibility(View.GONE);
                            rl3.setVisibility(View.VISIBLE);
                            cbthird.setChecked(true);
                            Orderreceivetime = "09:00 PM - 10:00 PM";
                        }
                        else if(hour>=17 && (minute>10 || hour>17))
                        {
                            rl1.setVisibility(View.GONE);
                            rl2.setVisibility(View.VISIBLE);
                            txtschedule.setText("08:00 PM - 09:00 PM");
                            cbschedule.setChecked(true);
                            rl3.setVisibility(View.VISIBLE);
                            Orderreceivetime = "08:00 PM - 09:00 PM";
                        }
                        else
                        {
                            rl1.setVisibility(View.VISIBLE);
                            rl2.setVisibility(View.VISIBLE);
                            rl3.setVisibility(View.VISIBLE);
                            cbasap.setChecked(true);
                            txtasap.setText("05:30 PM - 06:15 PM");
                            txtschedule.setText("08:00 PM - 09:00 PM");
                        }
                    }


                    cbasap.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (cbasap.isChecked() && !restaurantid.equals("1")) {
                                cbschedule.setChecked(false);
                                cbthird.setChecked(false);
                                Orderreceivetime = "ASAP";
                            } else if (cbasap.isChecked() && restaurantid.equals("1")) {
                                cbschedule.setChecked(false);
                                cbthird.setChecked(false);
                                Orderreceivetime = "05:30 PM - 06:15 PM";
                            }
                            else if(!cbasap.isChecked())
                            {
                                cbasap.setChecked(true);
                            }
                        }
                    });

                    cbschedule.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (cbschedule.isChecked() && !restaurantid.equals("1")) {
                                cbasap.setChecked(false);
                                Calendar mcurrentTime = Calendar.getInstance();
                                final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                                final int minute = mcurrentTime.get(Calendar.MINUTE);
                                tp=0;

                                final TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(PlaceOrder.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                        String shour, sminute;
                                        shour = String.valueOf(selectedHour);
                                        sminute = String.valueOf(selectedMinute);
                                        if (selectedHour == 0)
                                            shour = "00";
                                        if (selectedMinute == 0)
                                            sminute = "00";
                                        if (shour.length() == 1) {
                                            shour = "0" + shour;
                                        }
                                        if (sminute.length() == 1) {
                                            sminute = "0" + sminute;
                                        }

                                        if (!(shour.equals("00") && sminute.equals("00")) && selectedHour < hour) {
                                            Toast.makeText(PlaceOrder.this, "Please set time after current time", Toast.LENGTH_SHORT).show();
                                        } else if (selectedHour == hour && selectedMinute < minute) {
                                            Toast.makeText(PlaceOrder.this, "Please set time after current time", Toast.LENGTH_SHORT).show();
                                        }
                                        /*else if(!(shour.equals("00") && sminute.equals("00")) && selectedHour>restclosetimehr)
                                        {
                                            Toast.makeText(PlaceOrder.this, "Please set time before restaurant closing time", Toast.LENGTH_SHORT).show();
                                        }
                                        else if(selectedHour==restclosetimehr && selectedMinute>restclosetimemin) {
                                            Toast.makeText(PlaceOrder.this, "Please set time before restaurant closing time", Toast.LENGTH_SHORT).show();
                                        }*/
                                        else
                                        {
                                            txtschedule.setText("Scheduled at " + shour + ":" + sminute + " today");
                                            Orderreceivetime = shour + ":" + sminute;
                                            tp=1;
                                        }
                                    }
                                }, hour, minute, true);//Yes 24 hour time
                                mTimePicker.setTitle("Select Today's Delivery Time");
                                mTimePicker.show();

                                mTimePicker.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        if(tp==0) {cbasap.setChecked(true);
                                        cbschedule.setChecked(false);
                                        txtschedule.setText("Schedule (Pick time)");
                                        }
                                    }
                                });

                            } else if (cbschedule.isChecked() && restaurantid.equals("1")) {
                                cbasap.setChecked(false);
                                cbthird.setChecked(false);
                                Orderreceivetime = "08:00 PM - 09:00 PM";
                            }
                            else if(!cbschedule.isChecked())
                            {
                                cbschedule.setChecked(true);
                            }
                        }
                    });

                    cbthird.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(cbthird.isChecked()) {
                                Orderreceivetime = "09:00 PM - 10:00 PM";
                                cbasap.setChecked(false);
                                cbschedule.setChecked(false);
                            }
                            else if(!cbthird.isChecked())
                            {
                                cbthird.setChecked(true);
                            }
                        }
                    });
                    dialog.dismiss();
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
            //int totalordersint = Integer.parseInt(totalordersstr);
            //totalordersint-=1;

            String totalordersint = "-"+String.valueOf(System.currentTimeMillis()).substring(2,10);
            totalordersref.setValue(totalordersint);
            String ttordersstr = totalordersint;


            String OrderStatus,OrderStatusMessage,adminstatus,city_zone_status;
            if(Orderreceivetime.equals("ASAP"))
            {OrderStatus = "1";
              OrderStatusMessage ="Waiting for order confirmation...";
              adminstatus="1";
              city_zone_status=city+zone+"1";
            }
            else {
                OrderStatus = "11";
                OrderStatusMessage = "Waiting for order confirmation...";
                adminstatus="11";
                city_zone_status=city+zone+"11";

            }

            //currentrequestsref.child(ttordersstr).child("restaurantname").setValue();



            Request request = new Request(
                    cart,
                    city,
                    zone,
                    "null",
                    "0",
                    "null",
                    txtrestname.getText().toString(),
                    restareaname,
                    restaurantid,
                    restimageurl,
                    username,
                    phone,
                    address+"{"+lat+","+lng+"}",
                    String.valueOf(System.currentTimeMillis()),
                    txttotalamount.getText().toString().substring(1,txttotalamount.getText().toString().length()),
                    deliverycharge,
                    OrderStatus,
                    OrderStatusMessage,
                    Orderreceivetime,
                    Paymentmethod,
                    adminstatus,
                    city_zone_status,
                    restphone,
                    rqpromocode,
                    rqpromoamount
                    );
            //submit to firebase
            currentrequestsref.child(ttordersstr).setValue(request);


            new Database(getBaseContext()).cleanCart();
            //Toast.makeText(PlaceOrder.this, "Thankyou Order Placed !", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PlaceOrder.this,Orders.class);
            startActivity(intent);
            finish();

            //regiuster service
            //listenservice = new Intent(PlaceOrder.this,ListenOrder.class);
            //startService(listenservice);

            boolean isadvorder;
            if(Orderreceivetime.equals("ASAP"))
                isadvorder=false;
            else isadvorder=true;

            sendNotificationOrder(ttordersstr,isadvorder);
            sendNotificationOrderToAdmin(ttordersstr,isadvorder);

        }
        else
        {
            Toast.makeText(this, "Please check your internet connection !", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendNotificationOrder(final String order_number, final boolean isadvorder) {


        DatabaseReference tokens = firebaseDatabase.getReference("Tokens");
        Query data =tokens.orderByChild("city_zone_isserver").equalTo(city+zone+"1"); // get all node with server token is true
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Token serverToken = postSnapshot.getValue(Token.class);

                        //create raw play load to send
                        String click_action;
                        if(isadvorder) click_action= "ADVORDERS";
                        else click_action = "ORDERS";
                        Notification notification = new Notification("Waiting for order confirmation...", "You have new order #" + order_number.substring(1, order_number.length()), click_action, "customer", "3");
                        Sender content = new Sender(serverToken.getToken(), notification);

                        mService.sendNotification(content)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                        // if(response.code()==200){
                                        if (response.body().success == 1) {
                                            Toast.makeText(PlaceOrder.this, "Thankyou Order Placed !", Toast.LENGTH_SHORT).show();
                                            finish();
                                            //    }
                                        } else {
                                            //Toast.makeText(PlaceOrder.this, "No Guest Admin !", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Log.e("ERROR", t.getMessage());
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendNotificationOrderToAdmin(final String order_number, final boolean isadvorder) {


        DatabaseReference tokens = firebaseDatabase.getReference("Tokens");
        Query data =tokens.orderByChild("city_zone_isserver").equalTo("allN/A1"); // get all node with server token is true
        data.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Token serverToken = postSnapshot.getValue(Token.class);

                        //create raw play load to send
                        String click_action;
                        if(isadvorder) click_action= "ADVORDERS";
                        else click_action = "ORDERS";
                        Notification notification = new Notification("Waiting for order confirmation...", "You have new order #" + order_number.substring(1, order_number.length()), click_action, "customer", "3");
                        Sender content = new Sender(serverToken.getToken(), notification);

                        mService.sendNotification(content)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {

                                        // if(response.code()==200){
                                        if (response.body().success == 1) {
                                            Toast.makeText(PlaceOrder.this, "Thankyou Order Placed !", Toast.LENGTH_SHORT).show();
                                            finish();
                                            //    }
                                        } else {
                                            //Toast.makeText(PlaceOrder.this, "No Guest Admin !", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {
                                        Log.e("ERROR", t.getMessage());
                                    }
                                });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        total = 0; subtotal=0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*Integer.parseInt(order.getQuantity());
        subtotal = total;
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
