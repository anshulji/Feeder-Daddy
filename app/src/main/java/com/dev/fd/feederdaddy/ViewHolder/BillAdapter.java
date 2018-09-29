package com.dev.fd.feederdaddy.ViewHolder;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.MenuActivity;
import com.dev.fd.feederdaddy.OrderMeal;
import com.dev.fd.feederdaddy.R;
import com.dev.fd.feederdaddy.model.Order;
import com.dev.fd.feederdaddy.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.dev.fd.feederdaddy.OrderMeal.deliverycharges;
import static com.dev.fd.feederdaddy.OrderMeal.userlongitude;

class BillViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtfooditemname,txtfooditemqty,txtfooditemtotalcost;

    private ItemClickListener itemClickListener;

    public BillViewHolder(@NonNull View itemView) {
        super(itemView);

        txtfooditemname = itemView.findViewById(R.id.txtfooditemname);
        txtfooditemtotalcost = itemView.findViewById(R.id.txtfooditemtotalcost);
        txtfooditemqty = itemView.findViewById(R.id.txtfooditemqty);



        itemView.setOnClickListener(this);

    }

    public void setItemClickListener(ItemClickListener itemClickListener)
    {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {


        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}

public class BillAdapter extends RecyclerView.Adapter<BillViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Context context;

    public BillAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.bill_item_layout,null);


        return new BillViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final BillViewHolder billViewHolder, final int i) {

       //txtfooditemname
        billViewHolder.txtfooditemname.setText(listData.get(i).getFoodname());
        billViewHolder.txtfooditemqty.setText(listData.get(i).getQuantity());

        billViewHolder.txtfooditemtotalcost.setText("₹"+Integer.parseInt(listData.get(i).getPrice())*Integer.parseInt(listData.get(i).getQuantity()));

    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

}
