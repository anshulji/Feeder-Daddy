package com.dev.fd.feederdaddy;

import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Helper.RecyclerItemTouchHelper;
import com.dev.fd.feederdaddy.Interface.RecyclerItemTouchHelperListener;
import com.dev.fd.feederdaddy.ViewHolder.CartAdapter;
import com.dev.fd.feederdaddy.ViewHolder.CartViewHolder;
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

public class Cart extends AppCompatActivity implements RecyclerItemTouchHelperListener {

    public static  RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    ImageView imggoback;

    public static TextView txtTotalPrice;
    FButton btnPlace;

    String phone,username,city;

    public static ConstraintLayout constraintLayout;

    List<Order> cart = new ArrayList<>();
    public static CartAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        city = sharedPreferences.getString("city","N/A");

        constraintLayout = findViewById(R.id.root_layout);

        imggoback = findViewById(R.id.imggoback);
        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        database= FirebaseDatabase.getInstance();
        requests= database.getReference(city).child("Requests");

        //Init
        recyclerView= findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        txtTotalPrice= findViewById(R.id.total);
        btnPlace= findViewById(R.id.btnPlaceOrder);

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
                int ct = new Database(Cart.this).getCountCart();
                if(ct==0)
                {
                    Toast.makeText(Cart.this, "Your Cart Is Empty !", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(Cart.this, PlaceOrder.class);
                    startActivity(intent);
                }


            }
        });


    }

    /*private void showAlertDialog() {
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


    }*/

    public void loadListFood() {

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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder)
        {
            String name= ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getFoodname();

            final Order deleteItem = ((CartAdapter) recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());

            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);

            new Database(getBaseContext()).removeFoodFromCart(deleteItem.getID());

            //update total price
            int total =0;
            List<Order> orders = new Database(getBaseContext()).getCarts();
            for (Order item : orders)
                total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));

            txtTotalPrice.setText("₹"+total);

            //make snackbar
            Snackbar snackbar = Snackbar.make(constraintLayout,name+" removed from cart!",Snackbar.LENGTH_LONG);

            snackbar.show();

            loadListFood();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadListFood();
    }
}
