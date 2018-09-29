package com.dev.fd.feederdaddy.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.R;

public class OrderViewHolder extends RecyclerView.ViewHolder
        //implements View.OnClickListener
        {

    public ImageView imgrestaurant,imgorderstatus,imgcalldeliveryboy;
    public TextView txtrestaurantname,txtrestaurantareaname,txtorderstatus,txtorderstatusmessage,txtdeliveryboyname,txtdeliveryboyotp ,txtordertime,txttotalamount,txtviewbill;
    public Button btnrate;
    public RelativeLayout rldeliveryboyinfo;
    private ItemClickListener itemClickListener;


    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        imgrestaurant = itemView.findViewById(R.id.imgrestaurant);
        imgorderstatus = itemView.findViewById(R.id.imgorderstatus);
        txtrestaurantname = itemView.findViewById(R.id.txtrestaurantname);
        txtrestaurantareaname = itemView.findViewById(R.id.txtrestaurantareaname);
        txtorderstatus = itemView.findViewById(R.id.txtorderstatus);
        txtordertime = itemView.findViewById(R.id.txtordertime);
        txttotalamount = itemView.findViewById(R.id.txttotalamount);
        txtviewbill = itemView.findViewById(R.id.txtviewbill);
        btnrate = itemView.findViewById(R.id.btnrate);

        rldeliveryboyinfo = itemView.findViewById(R.id.rldeliveryboyinfo);
        txtorderstatusmessage = itemView.findViewById(R.id.txtorderstatusmessage);
        imgcalldeliveryboy = itemView.findViewById(R.id.imgcalldeliveryboy);
        txtdeliveryboyname = itemView.findViewById(R.id.txtdeliveryboyname);
        txtdeliveryboyotp = itemView.findViewById(R.id.txtdeliveryboyotp);



        //itemView.setOnClickListener(this);


    }

//    public void setItemClickListener(ItemClickListener itemClickListener) {
//        this.itemClickListener = itemClickListener;
//    }
//
//    @Override
//    public void onClick(View view) {
//        itemClickListener.onClick(view,getAdapterPosition(),false);
//
//    }

}
