package com.dev.fd.feederdaddy.ViewHolder;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView txtrestaurantname,txtrating,txtdistance,txttotalrates,txtdeliverytime,txtminorder,txtdelivercharge,txtclosed;
    public ImageView imgveg,imgnonveg,imgresaturant;
    public RatingBar ratingBar;

    private ItemClickListener itemClickListener;

    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);

        txtrestaurantname = itemView.findViewById(R.id.txtxrestaurantname);
        txtdeliverytime = itemView.findViewById(R.id.txtdeliverytime);
        txtminorder = itemView.findViewById(R.id.txttotalrates);
        txttotalrates = itemView.findViewById(R.id.txttotalrates);
        txtdelivercharge = itemView.findViewById(R.id.txtdeliverycharge);
        ratingBar =itemView.findViewById(R.id.ratingbar);
        txtclosed = itemView.findViewById(R.id.txtclosed);

        txtdistance = itemView.findViewById(R.id.txtdistance);
        txtrating = itemView.findViewById(R.id.txtrating);
        imgveg = itemView.findViewById(R.id.imgveg);
        imgnonveg = itemView.findViewById(R.id.imgnonveg);
        imgresaturant = itemView.findViewById(R.id.imgrestaurant);


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
