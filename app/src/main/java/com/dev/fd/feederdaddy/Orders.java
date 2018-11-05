package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.dev.fd.feederdaddy.Common.Common;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.ViewHolder.OrderViewHolder;
import com.dev.fd.feederdaddy.model.Food;
import com.dev.fd.feederdaddy.model.Order;
import com.dev.fd.feederdaddy.model.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;


public class Orders extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    private ImageView imggoback;

    private String phone, city;
    private static final int REQUEST_PHONE_CALL = 1;
    int t=0;

    Intent intent;

    FirebaseDatabase database;
    DatabaseReference requests;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        city = sharedPreferences.getString("city", "N/A");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        //firebase init
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("CurrentRequests");

        recyclerView = findViewById(R.id.listOrders);
        imggoback = findViewById(R.id.imggoback);

        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        phone = sharedPreferences.getString("phone", "N/A");

        //Toast.makeText(this, ""+Common.currentuser.getPhone(), Toast.LENGTH_SHORT).show();
        if (getIntent().getStringExtra("userPhone") == null) {//Toast.makeText(this, ""+Common.currentuser.getPhone(), Toast.LENGTH_SHORT).show();
            loadOrders(phone);
        } else {
            // Toast.makeText(this, ""+getIntent().getStringExtra("userPhone"), Toast.LENGTH_SHORT).show();
            loadOrders(getIntent().getStringExtra("userPhone"));
        }

    }

    private void loadOrders(final String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_item_layout,
                OrderViewHolder.class,
                requests.orderByChild("customerphone").equalTo(phone)
        ) {
            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                /*if(t==0) {
                    imggoback.setVisibility(View.INVISIBLE);
                    t=1;
                }
                else
                {
                    imggoback.setVisibility(View.VISIBLE);
                    t=0;
                }*/

                recyclerView.setAdapter(adapter);

            }

            @Override
            protected void populateViewHolder(final OrderViewHolder viewHolder, final Request model, final int position) {
                Picasso.with(getBaseContext()).load(model.getRestaurantimage()).fit().centerCrop().into(viewHolder.imgrestaurant);
                viewHolder.txtrestaurantname.setText(model.getRestaurantname());

                String orderidstr = adapter.getRef(position).getKey();
                orderidstr = orderidstr.substring(1,orderidstr.length());
                viewHolder.txtorderstatus.setText(Common.convertCodeToStatus(model.getOrderstatus()));
                viewHolder.txttotalamount.setText("Total: ₹" + model.getTotalamount());
                viewHolder.txtordertime.setText(Common.getDate(Long.parseLong(model.getTimeinms())));
                viewHolder.txtrestaurantareaname.setText(model.getRestaurantareaname());

                if(model.getOrderstatus().equals("11") || model.getOrderstatus().equals("1") || model.getOrderstatus().equals("-2"))
                viewHolder.imgorderstatus.setImageResource(R.drawable.ic_time);
                else if(model.getOrderstatus().equals("3"))
                    viewHolder.imgorderstatus.setImageResource(R.drawable.ic_restaurant_black_24dp);
                else if(model.getOrderstatus().equals("4"))
                    viewHolder.imgorderstatus.setImageResource(R.drawable.dispatched);
                else if(model.getOrderstatus().equals("6") || model.getOrderstatus().equals("5"))
                {viewHolder.imgorderstatus.setImageResource(R.drawable.order_delivered);
                }
                else if(model.getOrderstatus().equals("-1"))
                    viewHolder.imgorderstatus.setImageResource(R.drawable.order_cancelled);

                viewHolder.txtorderstatusmessage.setText("Order #"+orderidstr+"\n"+model.getOrderstatusmessage());
                if (!model.getOrderstatus().equals("4")) {
                    viewHolder.rldeliveryboyinfo.setVisibility(View.GONE);
                } else {
                    viewHolder.rldeliveryboyinfo.setVisibility(View.VISIBLE);
                    viewHolder.txtdeliveryboyname.setText(model.getDeliveryboyname());
                    int timemslen = model.getTimeinms().length();
                    String otp = model.getTimeinms().substring(timemslen - 4, timemslen);
                    viewHolder.txtdeliveryboyotp.setText("OTP : " + otp);

                    viewHolder.imgcalldeliveryboy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + model.getDeliveryboyphone()));
                            if (ContextCompat.checkSelfPermission(Orders.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(Orders.this, new String[]{android.Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                            }
                            else
                            {
                                startActivity(intent);
                            }
                        }

                    });
                }

                if(model.getOrderstatus().equals("5"))
                {
                    viewHolder.btnrate.setVisibility(View.VISIBLE);
                    viewHolder.btnrate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //something to do
                            Intent rate = new Intent(Orders.this, RateActivity.class);
                            rate.putExtra("rate",adapter.getRef(position).getKey());
                            startActivity(rate);

                        }
                    });

                }


                //viewHolder.txtordertime.setPaintFlags(viewHolder.txtordertime.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                viewHolder.setItemClickListener(new ItemClickListener() {
//                    @Override
//


//                    public void onClick(View view, int position, boolean isLongClick) {
//                        // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
//
//                        //start food details activity
//                        if(view==viewHolder.txtviewbill) {
//                            Intent Viewbill = new Intent(Orders.this, PlaceOrder.class);
//                            startActivity(Viewbill);
//                        }
//                    }
//                });

                viewHolder.txtviewbill.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent Viewbill = new Intent(Orders.this, ViewBillActivity.class);
                        Viewbill.putExtra("viewbill",adapter.getRef(position).getKey());
                        startActivity(Viewbill);
                    }
                });

            }
        };


    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_CALL: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                }
                else
                {

                }
                return;
            }
        }
    }

}
