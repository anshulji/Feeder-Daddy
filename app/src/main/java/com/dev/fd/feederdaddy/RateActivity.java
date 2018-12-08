package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.fd.feederdaddy.Common.Common;
import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.ViewHolder.FoodViewHolder;
import com.dev.fd.feederdaddy.ViewHolder.RateFoodViewHolder;
import com.dev.fd.feederdaddy.model.Food;
import com.dev.fd.feederdaddy.model.Order;
import com.dev.fd.feederdaddy.model.Rating;
import com.dev.fd.feederdaddy.model.Restaurant;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import info.hoang8f.widget.FButton;

import static android.view.View.GONE;


public class RateActivity extends AppCompatActivity implements RatingDialogListener {

    ImageView imggoback,imgrestaurant;
    FButton btndone;
    RecyclerView recycler_food;
    RecyclerView.LayoutManager layoutManager;

    TextView txtrestaurantname,txtrestaurantareaname,txthowwasrest;

    DatabaseReference databaseReference,foods;

    FirebaseRecyclerAdapter<Order,RateFoodViewHolder> adapter;

    String currentrequestsstr;

    List<String> foodnames = new ArrayList<>();


    int flag=0,value=5;
    double oldrating=0.0;
    String FoodId="",RestaurantId="",MenuId="",phone="",name="",city;
    Rating ratin;

    FirebaseDatabase database;
    DatabaseReference rating,restaurantrating,menurating;
    Food currentFood;

    SharedPreferences sharedPreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        imgrestaurant = findViewById(R.id.imgrestaurant);
        recycler_food = findViewById(R.id.recycler_ratefood);
        recycler_food.setHasFixedSize(true);
        layoutManager =new LinearLayoutManager(this);
        recycler_food.setLayoutManager(layoutManager);

        txtrestaurantname = findViewById(R.id.txtrestaurantname);
        txtrestaurantareaname = findViewById(R.id.txtrestaurantareaname);
        txthowwasrest =findViewById(R.id.txthowwasrest);
        imggoback = findViewById(R.id.imggoback);
        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btndone = findViewById(R.id.btndone);



        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        city = sharedPreferences.getString("city","N/A");


        database = FirebaseDatabase.getInstance();

        if(getIntent()!=null)
        {
            currentrequestsstr = getIntent().getStringExtra("rate");
            databaseReference =database.getReference("CurrentRequests").child(currentrequestsstr);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue()!=null) {

                        txtrestaurantname.setText(dataSnapshot.child("restaurantname").getValue().toString());
                        txtrestaurantareaname.setText(dataSnapshot.child("restaurantareaname").getValue().toString());
                        txthowwasrest.setText("How was " + dataSnapshot.child("restaurantname").getValue().toString() + "?");

                        Picasso.with(getBaseContext()).load(dataSnapshot.child("restaurantimage").getValue().toString()).fit().centerCrop().into(imgrestaurant);

                        RestaurantId = dataSnapshot.child("restaurantid").getValue().toString();

                        //load foof list
                        adapter = new FirebaseRecyclerAdapter<Order, RateFoodViewHolder>(Order.class, R.layout.rate_food_item_layout, RateFoodViewHolder.class,
                                dataSnapshot.child("foods").getRef()
                        ) {
                            @Override
                            protected void onDataChanged() {
                                super.onDataChanged();
                                recycler_food.setAdapter(adapter);
                            }

                            @Override
                            protected void populateViewHolder(final RateFoodViewHolder viewHolder, final Order model, int position) {

                                if (model.getFoodname().substring(0, 7).equals("(AddOn)")) {
                                    viewHolder.rlview.setVisibility(GONE);
                                } else {
                                    viewHolder.rlview.setVisibility(View.VISIBLE);

                                    String Foodname = model.getFoodname();
                                    for (int i = 0; i < Foodname.length(); i++) {
                                        if (Foodname.charAt(i) == ')') {
                                            Foodname = Foodname.substring(i + 1, Foodname.length());
                                            break;
                                        }
                                    }

                                    if (foodnames.contains(Foodname))
                                        viewHolder.rlview.setVisibility(GONE);
                                    else {
                                        viewHolder.rlview.setVisibility(View.VISIBLE);
                                        foodnames.add(Foodname);
                                    }

                                    viewHolder.txtfoodname.setText(Foodname);


                                    viewHolder.imgratebtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            MenuId = model.getMenuid();
                                            FoodId = model.getFoodid();
                                            foods = database.getReference(city).child("Foods").child(RestaurantId).child(model.getMenuid()).child(model.getFoodid());
                                            rating = database.getReference(city).child("Rating").child(RestaurantId).child(model.getMenuid()).child(model.getFoodid());
                                            restaurantrating = database.getReference(city).child("Restaurant").child(RestaurantId);
                                            menurating = database.getReference(city).child("Menus").child(RestaurantId).child(model.getMenuid());

                                            viewHolder.imgratebtn.setImageResource(R.drawable.order_delivered);
                                            foods.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    currentFood = dataSnapshot.getValue(Food.class);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                            showRatingDialog();

                                        }
                                    });
                                }
                            }
                        };

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });




        }
        btndone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("orderstatus").setValue("6");
                databaseReference.child("orderstatusmessage").setValue("Thankyou for ordering!");
                finish();
            }
        });
    }



    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite OK","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please rate this food and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.gray)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.gray)
                .setCommentTextColor(R.color.colorPrimary)
                .setCommentBackgroundColor(R.color.gray_filter)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(RateActivity.this)
                .show();


    }

    @Override
    public void onPositiveButtonClicked(final int values, String comments) {
        //get rating and upload to  firebase

        if(Common.isConnectedToInternet(getBaseContext())) {
            if(!comments.equalsIgnoreCase("")) {
                value = values;
                phone = sharedPreferences.getString("phone", "N/A");
                name = sharedPreferences.getString("name", "N/A");

                ratin = new Rating(name, comments, String.valueOf(value));

                rating.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null) {

                            if (dataSnapshot.child(phone).exists()) {
                                flag = 1;
                                String oldrat = dataSnapshot.child(phone).child("rate").getValue().toString();
                                oldrating = Double.parseDouble(oldrat);
                                updaterating();
                            } else {
                                flag = 0;
                                updaterating();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            else
                Toast.makeText(this, "Comment couldn't be empty!", Toast.LENGTH_SHORT).show();

        }
        else
            Toast.makeText(getBaseContext(), "No Internet Connection !", Toast.LENGTH_SHORT).show();

    }

    private void updaterating() {
        rating.child(phone).setValue(ratin);

        //add rating and comment


        //change foods rating
        final String prevrating = currentFood.getRating();
        final String prevtotalrates =currentFood.getTotalrates();
        final Double dprevrating = Double.parseDouble(prevrating);
        Double dprevtotalrates =Double.parseDouble(prevtotalrates);
        Double prod =dprevrating * dprevtotalrates;
        prod += value-oldrating;

        if(flag==0)
            dprevtotalrates+=1.0;



        prod =prod/dprevtotalrates;
        foods.child("rating").setValue(String.valueOf(prod));
        currentFood.setRating(String.valueOf(prod));
        String ntotalrates = String.format("%.0f",dprevtotalrates);
        foods.child("totalrates").setValue(String.valueOf(ntotalrates));
        currentFood.setTotalrates(String.valueOf(dprevtotalrates));


        //change menu rating

        menurating.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {

                    String mprevrating = dataSnapshot.child("rating").getValue().toString();
                    String mprevtotalrates = dataSnapshot.child("totalrates").getValue().toString();
                    Double mdprevrating = Double.parseDouble(mprevrating);
                    Double mdprevtotalrates = Double.parseDouble(mprevtotalrates);
                    Double mprod = mdprevrating * mdprevtotalrates;
                    mprod += value - oldrating;

                    if (flag == 0)
                        mdprevtotalrates += 1.0;

                    mprod = mprod / mdprevtotalrates;
                    menurating.child("rating").setValue(String.valueOf(mprod));
                    String ntotalrates = String.format("%.0f", mdprevtotalrates);
                    menurating.child("totalrates").setValue(String.valueOf(ntotalrates));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //change HotDealsRef rating

        restaurantrating.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {

                    String rprevrating = dataSnapshot.child("rating").getValue().toString();
                    String rprevtotalrates = dataSnapshot.child("totalrates").getValue().toString();
                    Double rdprevrating = Double.parseDouble(rprevrating);
                    Double rdprevtotalrates = Double.parseDouble(rprevtotalrates);
                    Double rprod = rdprevrating * rdprevtotalrates;
                    rprod += value - oldrating;

                    if (flag == 0)
                        rdprevtotalrates += 1.0;

                    rprod = rprod / rdprevtotalrates;
                    restaurantrating.child("rating").setValue(String.valueOf(rprod));

                    String ntotalrates = String.format("%.0f", rdprevtotalrates);
                    restaurantrating.child("totalrates").setValue(String.valueOf(ntotalrates));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }
}
