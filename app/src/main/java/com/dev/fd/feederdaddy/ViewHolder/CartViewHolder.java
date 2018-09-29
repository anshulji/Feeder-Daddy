package com.dev.fd.feederdaddy.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView txt_cart_name, txt_price;
    ImageView food_image,imgremovefood;
    ElegantNumberButton btn_quantity;

    public RelativeLayout view_background,view_foreground;


    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name)
    {
        this.txt_cart_name = txt_cart_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        txt_cart_name = itemView.findViewById(R.id.cart_item_name);
        txt_price = itemView.findViewById(R.id.cart_item_price);
        food_image = itemView.findViewById(R.id.food_image);
        btn_quantity = itemView.findViewById(R.id.btn_quantity);
        view_background = itemView.findViewById(R.id.view_background);
        view_foreground = itemView.findViewById(R.id.view_foreground);
        imgremovefood = itemView.findViewById(R.id.imgremovefoodfromcart);


    }

    @Override
    public void onClick(View view) {

    }
}
