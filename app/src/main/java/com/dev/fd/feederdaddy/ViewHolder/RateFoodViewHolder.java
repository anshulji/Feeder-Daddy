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

public class RateFoodViewHolder extends RecyclerView.ViewHolder
        //implements View.OnClickListener
        {

    public ImageView imgratebtn;
    public TextView txtfoodname;

    private ItemClickListener itemClickListener;


    public RateFoodViewHolder(@NonNull View itemView) {
        super(itemView);

        imgratebtn = itemView.findViewById(R.id.imgrate);
        txtfoodname = itemView.findViewById(R.id.food_name);

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
