package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.ViewHolder.ReviewsViewHolder;
import com.dev.fd.feederdaddy.model.Food;
import com.dev.fd.feederdaddy.model.HotDeal;
import com.dev.fd.feederdaddy.model.Order;
import com.dev.fd.feederdaddy.model.Rating;
import com.dev.fd.feederdaddy.model.Reviews;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;

import java.util.Arrays;
import java.util.Calendar;

import info.hoang8f.widget.FButton;

public class HotDealDetailsActivity extends AppCompatActivity {
    TextView food_name, food_price, food_description;
    ImageView food_image,imgveg,imgnonveg;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    FButton btnshowreviews;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    String FoodId="",RestaurantId="",MenuId="",phone="",name="",city,isHotDeal="";

    HotDeal currentFood;

    int flag=0,value=5;

    FirebaseDatabase database;
    DatabaseReference foods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hot_deal_details);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        city = sharedPreferences.getString("city","N/A");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        database=database.getInstance();

        //get food id
        if(getIntent()!=null)
        {
            FoodId=getIntent().getStringExtra("FoodId");
            isHotDeal = getIntent().getStringExtra("isHotDeal");
            if(isHotDeal.equals("1"))
            foods=database.getReference(city).child("HotDeals").child(FoodId);
            else
                foods=database.getReference(city).child("NightOrders").child(FoodId);


        }
        if(!FoodId.isEmpty())
        {
            getDetailFood(FoodId);
        }



        //Init View
        numberButton=(ElegantNumberButton)findViewById(R.id.number_button);

        btnCart = findViewById(R.id.btnCart);



        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(numberButton.getNumber().equals("0"))
                {
                    Toast.makeText(HotDealDetailsActivity.this, "Please set item count !", Toast.LENGTH_SHORT).show();
                }

                else {

                    SharedPreferences sharedPreferences = getSharedPreferences("MyData",Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(isHotDeal.equals("1"))
                    editor.putString("restaurantid", "-1");
                    else
                        editor.putString("restaurantid", "-2");
                    editor.commit();


                    String timeidstr = String.valueOf(Calendar.getInstance().getTimeInMillis());
                    timeidstr = timeidstr.substring(timeidstr.length()-6,timeidstr.length()-2);


                    new Database(getBaseContext()).addToCart(new Order(
                            Integer.parseInt(timeidstr),
                            FoodId,
                            currentFood.getName(),
                            numberButton.getNumber(),
                            currentFood.getPrice(),
                            currentFood.getImage(),
                            "-1",
                            "0"
                    ));

                    Toast.makeText(HotDealDetailsActivity.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        food_description=findViewById(R.id.food_description);
        food_name = findViewById(R.id.food_name);
        food_price=findViewById(R.id.food_price);
        food_image= findViewById(R.id.img_food);
        imgveg =findViewById(R.id.imgveg);
        imgnonveg =findViewById(R.id.imgnonveg);

        collapsingToolbarLayout= findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);



    }





    private void getDetailFood(String foodId) {

        foods.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(HotDeal.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);
                if(currentFood.getVeg().equals("1")) {
                    imgveg.setVisibility(View.VISIBLE);
                    food_name.setTextColor(getResources().getColor(R.color.green));
                }
                else
                    imgnonveg.setVisibility(View.VISIBLE);

                collapsingToolbarLayout.setTitle(currentFood.getName());
                food_price.setText("₹"+currentFood.getPrice());
                food_name.setText(currentFood.getName());
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}
