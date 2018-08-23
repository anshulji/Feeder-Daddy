package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.dev.fd.feederdaddy.Common.Common;
import com.dev.fd.feederdaddy.ViewHolder.OrderViewHolder;
import com.dev.fd.feederdaddy.model.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Orders extends AppCompatActivity {

    public RecyclerView recyclerView;
    public  RecyclerView.LayoutManager layoutManager;

    private String phone;

    FirebaseDatabase database;
    DatabaseReference requests;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        //firebase init
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone","N/A");

        //Toast.makeText(this, ""+Common.currentuser.getPhone(), Toast.LENGTH_SHORT).show();
        if(getIntent().getStringExtra("userPhone")==null)
        {//Toast.makeText(this, ""+Common.currentuser.getPhone(), Toast.LENGTH_SHORT).show();
            loadOrders(phone);}
        else {
            // Toast.makeText(this, ""+getIntent().getStringExtra("userPhone"), Toast.LENGTH_SHORT).show();
            loadOrders(getIntent().getStringExtra("userPhone"));
        }



    }

    private void loadOrders(final String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_item_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone").equalTo(phone)
        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderPhone.setText(phone);
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderAddress.setText(model.getAddress());
            }
        };

        recyclerView.setAdapter(adapter);

    }


}
