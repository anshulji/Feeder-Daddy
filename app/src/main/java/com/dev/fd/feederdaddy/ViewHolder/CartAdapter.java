package com.dev.fd.feederdaddy.ViewHolder;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dev.fd.feederdaddy.Cart;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.MainActivity;
import com.dev.fd.feederdaddy.R;
import com.dev.fd.feederdaddy.model.Order;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.dev.fd.feederdaddy.Cart.adapter;
import static com.dev.fd.feederdaddy.Cart.recyclerView;
import static com.dev.fd.feederdaddy.Cart.txtTotalPrice;
import static com.dev.fd.feederdaddy.Cart.constraintLayout;


public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout,null);

        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final CartViewHolder cartViewHolder, final int i) {

//            TextDrawable drawable = TextDrawable.builder().buildRound(""+listData.get(i).getQuantity(), Color.RED);
//            cartViewHolder.img_cart_count.setImageDrawable(drawable);

        //Locale locale = new Locale("en","US");
        //NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        Picasso.with(context).load(listData.get(i).getImage()).fit().centerCrop().into(cartViewHolder.food_image);

        cartViewHolder.btn_quantity.setNumber(listData.get(i).getQuantity());

        cartViewHolder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(i);
                order.setQuantity(String.valueOf(newValue));
                new Database(context).updateCart(order);

                //update totAL price
                int total =0;
                List<Order> orders = new Database(context).getCarts();
                for (Order item : orders)
                    total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));


                int price = (Integer.parseInt(listData.get(i).getPrice())*Integer.parseInt(listData.get(i).getQuantity()));
                cartViewHolder.txt_price.setText("₹"+price);

                txtTotalPrice.setText("₹"+total);
            }
        });

        int price = (Integer.parseInt(listData.get(i).getPrice())*Integer.parseInt(listData.get(i).getQuantity()));
        cartViewHolder.txt_price.setText("₹"+price);
        cartViewHolder.txt_cart_name.setText(listData.get(i).getFoodname());
        String col =  String.valueOf(listData.get(i).getID());
        cartViewHolder.txt_cart_name.setTextColor(Color.parseColor("#"+col.substring(4,5)+"F"+col.substring(0,4)));

        cartViewHolder.imgremovefood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name= listData.get(i).getFoodname();

                final Order deleteItem = listData.get(i);

                final int deleteIndex = i;


                adapter.removeItem(deleteIndex);

                new Database(context).removeFoodFromCart(deleteItem.getID());

                //update totAL price
                int total =0;
                List<Order> orders = new Database(context).getCarts();
                for (Order item : orders)
                    total+=(Integer.parseInt(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                txtTotalPrice.setText("₹"+total);

                //make snackbar
                Snackbar snackbar = Snackbar.make(constraintLayout,name+" removed from cart!",Snackbar.LENGTH_LONG);
                snackbar.show();

                ((Cart) context).loadListFood();

            }
        });
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int position) {return listData.get(position);}

    public void removeItem (int position)
    {
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem (Order item,int position)
    {
        listData.add(position,item);
        notifyItemInserted(position);
    }




}
