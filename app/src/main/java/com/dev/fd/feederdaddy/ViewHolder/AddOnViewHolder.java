package com.dev.fd.feederdaddy.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.R;

public class AddOnViewHolder extends RecyclerView.ViewHolder
        //implements View.OnClickListener
        {

    public CheckBox cbaddonitem;
    public TextView txtaddonitemname,txtaddonitemprice;

    private ItemClickListener itemClickListener;


    public AddOnViewHolder(@NonNull View itemView) {
        super(itemView);

        cbaddonitem = itemView.findViewById(R.id.cbaddonitem);
        txtaddonitemname = itemView.findViewById(R.id.txtaddonitemname);
        txtaddonitemprice = itemView.findViewById(R.id.txtaddonitemprice);

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
