package com.dev.fd.feederdaddy.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.R;

public class ReviewsViewHolder extends RecyclerView.ViewHolder {


    public TextView txtusername,txtreview;
    public RatingBar ratingBar;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ReviewsViewHolder(@NonNull View itemView) {
        super(itemView);
        txtusername = itemView.findViewById(R.id.txtusernamereview);
        txtreview = itemView.findViewById(R.id.txtcommentreview);
        ratingBar =itemView.findViewById(R.id.ratingbarreview);



    }


}
