package com.dev.fd.feederdaddy.ViewHolder;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.MenuActivity;
import com.dev.fd.feederdaddy.OrderMeal;
import com.dev.fd.feederdaddy.R;
import com.dev.fd.feederdaddy.SignUpActivity;
import com.dev.fd.feederdaddy.model.Order;
import com.dev.fd.feederdaddy.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.dev.fd.feederdaddy.OrderMeal.deliverycharges;
import static com.dev.fd.feederdaddy.OrderMeal.mindcdistance;
import static com.dev.fd.feederdaddy.OrderMeal.mindeliverycharge;
import static com.dev.fd.feederdaddy.OrderMeal.userlongitude;

class RestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtrestaurantname,txtrating,txtdistance,txttotalrates,txtdeliverytime,txtdelivercharge,txtclosed,txtopentime,txtrestdiscount;
    public ImageView imgveg,imgnonveg,imgresaturant;
    public RatingBar ratingBar;

    private ItemClickListener itemClickListener;

    public RestViewHolder(@NonNull View itemView) {
        super(itemView);

        txtrestaurantname = itemView.findViewById(R.id.txtxrestaurantname);
        txtdeliverytime = itemView.findViewById(R.id.txtdeliverytime);
        txttotalrates = itemView.findViewById(R.id.txttotalrates);
        txtdelivercharge = itemView.findViewById(R.id.txtdeliverycharge);
        ratingBar =itemView.findViewById(R.id.ratingbar);
        txtclosed = itemView.findViewById(R.id.txtclosed);
        txtopentime = itemView.findViewById(R.id.txtopentime);

        txtdistance = itemView.findViewById(R.id.txtdistance);
        txtrating = itemView.findViewById(R.id.txtrating);
        imgveg = itemView.findViewById(R.id.imgveg);
        imgnonveg = itemView.findViewById(R.id.imgnonveg);
        imgresaturant = itemView.findViewById(R.id.imgrestaurant);

        txtrestdiscount = itemView.findViewById(R.id.txtrestdiscount);

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

public class RestaurantAdapter extends RecyclerView.Adapter<RestViewHolder>{

    private List<Restaurant> listData = new ArrayList<>();
    private Context context;

    public RestaurantAdapter(List<Restaurant> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public RestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.restaurant_item,null);

        return new RestViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final RestViewHolder restViewHolder, final int i) {

        //txtrestaurantname
        Typeface face = Typeface.createFromAsset(context.getAssets(),"NABILA.TTF");
        restViewHolder.txtrestaurantname.setTypeface(face);
        restViewHolder.txtrestaurantname.setText(listData.get(i).getName());

        if(listData.get(i).getRestaurantdiscount().equals("null"))
        {
            restViewHolder.txtrestdiscount.setVisibility(View.GONE);
        }
        else
        {
            restViewHolder.txtrestdiscount.setVisibility(View.VISIBLE);
            restViewHolder.txtrestdiscount.setText(listData.get(i).getRestaurantdiscount()+"% OFF");
        }

        //img veg and nonveg
        if(!listData.get(i).getVeg().equals("1"))
            restViewHolder.imgveg.setVisibility(View.GONE);
        else if(!listData.get(i).getNonveg().equals("1"))
            restViewHolder.imgnonveg.setVisibility(View.GONE);

        //txt rating
        Double drating = Double.parseDouble(listData.get(i).getRating());
        String rat = String.format("%.1f",drating);
        restViewHolder.txtrating.setText(rat);

        //rating bar
        restViewHolder.ratingBar.setRating(Float.parseFloat(listData.get(i).getRating()));

        //txt distance
        final Double distance = getDistanceFromLatLonInKm(Double.parseDouble(OrderMeal.userlatitude),Double.parseDouble(userlongitude),Double.parseDouble(listData.get(i).getLatitude()),Double.parseDouble(listData.get(i).getLongitude()));
        String dist = String.format("%.2f",distance);
        restViewHolder.txtdistance.setText(dist+" km");

        //txt delivery rate

        Double rate = Double.parseDouble(deliverycharges);
        final Double mindcdistance = Double.parseDouble(OrderMeal.mindcdistance);
        final Double mindeliverycharge= Double.parseDouble(OrderMeal.mindeliverycharge);
        Double charge;
        if(distance<mindcdistance)
        {
            charge = mindeliverycharge;
        }
        else {
            charge = distance * rate;
        }
        String dc = String.format("%.0f",charge);
        restViewHolder.txtdelivercharge.setText("Delivery ₹"+dc);

        //txt total rates
        restViewHolder.txttotalrates.setText("("+listData.get(i).getTotalrates()+")");

        //txt delivery time
        Double time = 20.0 + (distance/0.2);
        String timestr = String.format("%.0f",time);
        time+=15.0;
        String timestr1 = String.format("%.0f",time);
        restViewHolder.txtdeliverytime.setText(timestr+"-"+timestr1);

        //txtopentime
        restViewHolder.txtopentime.setText(listData.get(i).getOpentime());

        //set open or close
        if(listData.get(i).getIsopen().equals("0"))
        {
            int color = R.color.black_filter;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                restViewHolder.imgresaturant.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
            }
            restViewHolder.txtclosed.setVisibility(View.VISIBLE);
        }
        else
        {
            int color = R.color.gray_filter;
            restViewHolder.txtclosed.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                restViewHolder.imgresaturant.setForeground(new ColorDrawable(ContextCompat.getColor(context, color)));
            }
        }



        Picasso.with(context).load(listData.get(i).getImage()).into(restViewHolder.imgresaturant);

        final Restaurant clickitem = listData.get(i);
        restViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                // Toast.makeText(Home.this, ""+clickitem.getName(), Toast.LENGTH_SHORT).show();
                //get category id and send to foodlist activity
                int ct = new Database(context).getCountCart();
                final SharedPreferences sharedPreferences = context.getSharedPreferences("MyData", Context.MODE_PRIVATE);
                String resid = sharedPreferences.getString("restaurantid","N/A");
                if(listData.get(i).getIsopen().equals("0"))
                {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context,R.style.MyDialogTheme);
                    alertDialogBuilder.setTitle("Restaurant is closed currently!");
                    alertDialogBuilder.setMessage("You can explore this restaurant's menu but you won't be able to place order from this restaurant.");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int in) {
                            new Database(context).cleanCart();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("restaurantdiscount",listData.get(i).getRestaurantdiscount());
                            editor.putString("restaurantid",listData.get(i).getId());
                            editor.putString("isopen","0");
                            editor.putString("deliveryrate",deliverycharges);
                            editor.putString("mindcdistance", String.valueOf(mindcdistance));
                            editor.putString("mindeliverycharge", String.valueOf(mindeliverycharge));
                            editor.commit();

                            Intent foodlist = new Intent(context, MenuActivity.class);
                            foodlist.putExtra("RestaurantId", listData.get(i).getId());
                            context.startActivity(foodlist);

                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialogBuilder.show();                }
                else if (ct!=0 && !resid.equals("N/A") && !listData.get(i).getId().equals(resid))
                {
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context,R.style.MyDialogTheme);
                    alertDialogBuilder.setTitle("Empty Your Cart");
                    alertDialogBuilder.setMessage("You already have some items from other restaurant in your cart. Do you wish to empty your cart?");
                    alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int in) {
                            new Database(context).cleanCart();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("restaurantdiscount",listData.get(i).getRestaurantdiscount());
                            editor.putString("restaurantid",listData.get(i).getId());
                            editor.putString("isopen","1");
                            editor.putString("deliveryrate",deliverycharges);
                            editor.putString("mindcdistance", String.valueOf(mindcdistance));
                            editor.putString("mindeliverycharge", String.valueOf(mindeliverycharge));
                            editor.commit();

                            Intent foodlist = new Intent(context, MenuActivity.class);
                            foodlist.putExtra("RestaurantId", listData.get(i).getId());
                            context.startActivity(foodlist);

                        }
                    });
                    alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
                    alertDialogBuilder.show();
                }
                else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("restaurantdiscount",listData.get(i).getRestaurantdiscount());
                    editor.putString("restaurantid",listData.get(i).getId());
                    editor.putString("deliveryrate",deliverycharges);
                    editor.putString("isopen","1");
                    //editor.putString("restaurantname", listData.get(i).getName());
                    //editor.putString("restaurantrating", listData.get(i).getRating());
                    //editor.putString("restaurantimage", listData.get(i).getImage());
                    //txt delivery rate

                    //Double rate = Double.parseDouble(deliverycharges);
                    //Double charge = distance * rate;
                    //String dc = String.format("%.0f", charge);
                    //restViewHolder.txtdelivercharge.setText("Delivery ₹"+dc);
                    //editor.putString("deliverycharge", dc);

                    //txt delivery time
                    //Double time = 20.0 + (distance / 0.2);
                    //String timestr = String.format("%.0f", time);
                    //time += 15.0;
                    //String timestr1 = String.format("%.0f", time);
                    //restViewHolder.txtdeliverytime.setText(timestr+"-"+timestr1);
                    //editor.putString("deliverytime", timestr + "-" + timestr1);

                    editor.commit();


                    Intent foodlist = new Intent(context, MenuActivity.class);
                    foodlist.putExtra("RestaurantId", listData.get(i).getId());
                    context.startActivity(foodlist);
                }
            }
        });


    }
    private double getDistanceFromLatLonInKm(double lat1,double lon1,double lat2,double lon2) {
        double R = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = R * c; // Distance in km
        return d;
    }

    private double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
