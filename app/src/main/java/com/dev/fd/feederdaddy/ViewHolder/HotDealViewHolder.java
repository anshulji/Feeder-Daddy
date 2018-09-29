package com.dev.fd.feederdaddy.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.R;

public class HotDealViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView food_name,food_price;
    public ImageView food_image,imgveg,imgnonveg;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public HotDealViewHolder(@NonNull View itemView) {
        super(itemView);
        food_name = itemView.findViewById(R.id.food_name);
        food_price = itemView.findViewById(R.id.food_price);
        food_image = itemView.findViewById(R.id.food_image);
        imgveg = itemView.findViewById(R.id.imgveg);
        imgnonveg = itemView.findViewById(R.id.imgnonveg);

        itemView.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
