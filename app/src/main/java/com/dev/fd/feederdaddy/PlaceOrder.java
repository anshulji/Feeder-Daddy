package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.ViewHolder.BillAdapter;
import com.dev.fd.feederdaddy.ViewHolder.CartAdapter;
import com.dev.fd.feederdaddy.model.Order;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlaceOrder extends AppCompatActivity {

    ImageView imgrest,imggoback,imgeditaddress;
    TextView txtrestname,txtdeliverycharge,txtaddress,txttotalamount,txtrating,txtdeliverytime;
    RatingBar ratingBar;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String deliverycharge;

    List<Order> cart = new ArrayList<>();
    BillAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

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

        Typeface face = Typeface.createFromAsset(getAssets(),"NABILA.TTF");
        txtrestname.setTypeface(face);


        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String restaurantname = sharedPreferences.getString("restaurantname","N/A");
        String rating = sharedPreferences.getString("restaurantrating","N/A");
        String restimage = sharedPreferences.getString("restaurantimage","N/A");
        String address = sharedPreferences.getString("address","N/A");
        String deliverytime = sharedPreferences.getString("deliverytime","N/A");
         deliverycharge =sharedPreferences.getString("deliverycharge","N/A");

        if(!restaurantname.equals("N/A")){
            txtrestname.setText(restaurantname);
            ratingBar.setRating(Float.parseFloat(rating));
            txtrating.setText(rating);

            txtdeliverytime.setText("Delivery Time : "+deliverytime+" min");
            Picasso.with(getBaseContext()).load(restimage).fit().centerCrop().into(imgrest);
            txtaddress.setText(address);
            txtdeliverycharge.setText("₹"+deliverycharge);
        }

        //Init bill recycler view
        recyclerView= findViewById(R.id.recycler_bill);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadListFood();

    }



    private void loadListFood() {

        cart = new Database(this).getCarts();
        adapter = new BillAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        //calculate total price
        int total = 0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*Integer.parseInt(order.getQuantity());
        total+=Integer.parseInt(deliverycharge);
        //Locale locale = new Locale("","US");
        //NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txttotalamount.setText("₹"+total);


    }
}
