package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.ViewHolder.BillAdapter;
import com.dev.fd.feederdaddy.model.Order;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ViewBillActivity extends AppCompatActivity {

    ImageView imgrestimage,imggoback;
    TextView txtrestname,txtuseraddress,txttotal,txtdeliverycharge,txtsubltotal,txtpromocodename,txtpromocodeamount;

    RecyclerView rvbill;

    String requestnumber;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    List<Order> orderlist  = new ArrayList<>();
    BillAdapter adapter;

    LinearLayout llpromobill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bill);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if(getIntent()!=null)
        {
            requestnumber = getIntent().getStringExtra("viewbill");
        }

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String city = sharedPreferences.getString("city","N/A");

        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("CurrentRequests").child(requestnumber);

        rvbill = findViewById(R.id.recycler_bill);
        rvbill.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        rvbill.setLayoutManager(layoutManager);

        txtrestname = findViewById(R.id.txtrestname);
        imggoback = findViewById(R.id.imggoback);
        imgrestimage = findViewById(R.id.imgrest);
        txtuseraddress = findViewById(R.id.txtaddress);
        txttotal =findViewById(R.id.txttotalamount);
        txtdeliverycharge = findViewById(R.id.txtdeliverycharge);

        llpromobill = findViewById(R.id.llbillpromo);
        txtsubltotal = findViewById(R.id.txtsubtotal);
        txtpromocodename = findViewById(R.id.txtbillpromoname);
        txtpromocodeamount = findViewById(R.id.txtpromoamount);

        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtrestname.setText(dataSnapshot.child("restaurantname").getValue().toString());
                Picasso.with(getBaseContext()).load(dataSnapshot.child("restaurantimage").getValue().toString()).into(imgrestimage);
                txtdeliverycharge.setText("₹"+dataSnapshot.child("deliverycharge").getValue().toString());
                txtuseraddress.setText(dataSnapshot.child("customeraddress").getValue().toString());
                txttotal.setText("₹"+dataSnapshot.child("totalamount").getValue().toString());
                if(!dataSnapshot.child("promocode").getValue().toString().equals("null"))
                {
                    llpromobill.setVisibility(View.VISIBLE);
                    txtpromocodename.setText(dataSnapshot.child("promocode").getValue().toString());
                    txtpromocodeamount.setText("-₹"+dataSnapshot.child("promoamount").getValue().toString());
                    int totalamnt = Integer.parseInt(dataSnapshot.child("totalamount").getValue().toString());
                    int promoamnt = Integer.parseInt(dataSnapshot.child("promoamount").getValue().toString());
                    int deliverycharge = Integer.parseInt(dataSnapshot.child("deliverycharge").getValue().toString());
                    int subtotal = totalamnt-deliverycharge+promoamnt;
                    txtsubltotal.setText("₹"+subtotal);
                }

                for(DataSnapshot postSnapshot : dataSnapshot.child("foods").getChildren())
                {
                  orderlist.add(postSnapshot.getValue(Order.class));
                }

                adapter = new BillAdapter(orderlist,ViewBillActivity.this);
                rvbill.setAdapter(adapter);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
}
