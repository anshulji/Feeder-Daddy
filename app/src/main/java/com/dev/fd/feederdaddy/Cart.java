package com.dev.fd.feederdaddy;

import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.ViewHolder.CartAdapter;
import com.dev.fd.feederdaddy.model.Order;
import com.dev.fd.feederdaddy.model.Request;
import com.google.android.gms.common.internal.service.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txtTotalPrice;
    FButton btnPlace;

    String phone,username;

    List<Order> cart = new ArrayList<>();
    CartAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        database= FirebaseDatabase.getInstance();
        requests= database.getReference("Requests");

        //Init
        recyclerView= findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice= findViewById(R.id.total);
        btnPlace= findViewById(R.id.btnPlaceOrder);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone","N/A");
        username = sharedPreferences.getString("name","N/A");

        loadListFood();

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Request request =new Request(
                        Common.currentuser.getPhone(),
                        Common.currentuser.getName(),
                        address,
                        txtTotalPrice.getText().toString(),
                        cart

                );*/

               // showAlertDialog();
                Intent intent = new Intent(Cart.this,PlaceOrder.class);
                startActivity(intent);



            }
        });


    }

    private void showAlertDialog() {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(Cart.this);
        alertdialog.setTitle("One more step !");
        alertdialog.setMessage("Enter your address: ");

        final EditText edtAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT );
        edtAddress.setLayoutParams(lp);
        alertdialog.setView(edtAddress);
        alertdialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertdialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Request request = new Request(
                        phone,
                        username,
                        edtAddress.getText().toString(),
                        txtTotalPrice.getText().toString(),
                        cart
                );
                //submit to firebase
                requests.child(String.valueOf(System.currentTimeMillis())).setValue(request);

                new Database(getBaseContext()).cleanCart();
                Toast.makeText(Cart.this, "Thankyou Order Placed !", Toast.LENGTH_SHORT).show();
                finish();

            }

        });
        alertdialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertdialog.show();


    }

    private void loadListFood() {

        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart,this);
        recyclerView.setAdapter(adapter);

        //calculate total price
        int total = 0;
        for(Order order:cart)
            total+=(Integer.parseInt(order.getPrice()))*Integer.parseInt(order.getQuantity());
        //Locale locale = new Locale("","US");
        //NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText("₹"+total);


    }
}
