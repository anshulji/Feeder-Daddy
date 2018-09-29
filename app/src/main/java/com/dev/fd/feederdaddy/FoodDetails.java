package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.R;
import com.dev.fd.feederdaddy.ViewHolder.AddOnViewHolder;
import com.dev.fd.feederdaddy.ViewHolder.FoodViewHolder;
import com.dev.fd.feederdaddy.ViewHolder.ReviewsViewHolder;
import com.dev.fd.feederdaddy.model.AddOnsModel;
import com.dev.fd.feederdaddy.model.Food;
import com.dev.fd.feederdaddy.model.Menu;
import com.dev.fd.feederdaddy.model.Order;
import com.dev.fd.feederdaddy.model.Rating;
import com.dev.fd.feederdaddy.model.Restaurant;
import com.dev.fd.feederdaddy.model.Reviews;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.C;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;
import java.util.Calendar;

import info.hoang8f.widget.FButton;

public class FoodDetails extends AppCompatActivity implements RatingDialogListener{

    TextView food_name, food_quarterprice,food_halfprice,food_fullprice,food_quarterfinalprice,food_halffinalprice,food_fullfinalprice,
            food_quarterdiscount, food_halfdiscount, food_fulldiscount,food_description,txttotalrates,txtrating;

    TextView text_count;


    RelativeLayout llquarter, llhalf, llfull;

    ImageView food_image,imgveg,imgnonveg,imggoback;
    RatingBar ratingBar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart,btnRating;
    ElegantNumberButton qnb,hnb,fnb;

    FButton btnshowreviews;
    RecyclerView recyclerView,recycler_addon;
    RecyclerView.LayoutManager layoutManager,layoutManager1;


    FirebaseRecyclerAdapter<Reviews,ReviewsViewHolder> adapter;
    FirebaseRecyclerAdapter<AddOnsModel,AddOnViewHolder> addonadapter;



    String FoodId="",RestaurantId="",MenuId="",phone="",name="",city,timeidstr;


    Food currentFood;
    Rating ratin;

    int flag=0,value=5;
    double oldrating=0.0;
    int fqprice,fhprice,ffprice,timeid;

    FirebaseDatabase database;
    DatabaseReference foods,rating,restaurantrating,menurating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        Calendar calendar = Calendar.getInstance();
        timeidstr = String.valueOf(calendar.getTimeInMillis());
        timeidstr = timeidstr.substring(timeidstr.length()-6,timeidstr.length()-2);



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
            RestaurantId=getIntent().getStringExtra("RestaurantId");
            MenuId=getIntent().getStringExtra("MenuId");
            foods=database.getReference(city).child("Foods").child(RestaurantId).child(MenuId).child(FoodId);
            rating = database.getReference(city).child("Rating").child(RestaurantId).child(MenuId).child(FoodId);

            restaurantrating = database.getReference(city).child("Restaurant").child(RestaurantId);
            menurating = database.getReference(city).child("Menus").child(RestaurantId).child(MenuId);


        }
        if(!FoodId.isEmpty())
        {
            getDetailFood(FoodId);
        }



        //Init View
        qnb=(ElegantNumberButton)findViewById(R.id.number_button_quarter);
        hnb=(ElegantNumberButton)findViewById(R.id.number_button_half);
        fnb=(ElegantNumberButton)findViewById(R.id.number_button_full);
        //btnRating = findViewById(R.id.btn_rating);

        /*btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });*/

        btnCart = findViewById(R.id.btnCart);
        text_count = findViewById(R.id.text_count);

        ratingBar = findViewById(R.id.ratingbar);
        txttotalrates = findViewById(R.id.txttotalrates);


        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cartintent = new Intent(FoodDetails.this,Cart.class);
                finish();
                startActivity(cartintent);

                /*if(qnb.getNumber().equals("0") && hnb.getNumber().equals("0") && fnb.getNumber().equals("0"))
                {
                    Toast.makeText(FoodDetails.this, "Please set item count !", Toast.LENGTH_SHORT).show();
                }

                else {

                    if(!currentFood.getQuarterprice().equals("null") && !qnb.getNumber().equals("0")) {
                        new Database(getBaseContext()).addToCart(new Order(
                                FoodId,
                                "(Quarter)"+currentFood.getName(),
                                qnb.getNumber(),
                                String.valueOf(fqprice),
                                currentFood.getImage(),
                                RestaurantId,
                                MenuId
                        ));
                    }
                    if(!currentFood.getHalfprice().equals("null") && !hnb.getNumber().equals("0")) {
                        new Database(getBaseContext()).addToCart(new Order(
                                FoodId,
                                "(Half)"+currentFood.getName(),
                                hnb.getNumber(),
                                String.valueOf(fhprice),
                                currentFood.getImage(),
                                RestaurantId,
                                MenuId
                        ));
                    }
                    if(!currentFood.getFullprice().equals("null") && !fnb.getNumber().equals("0")) {
                        new Database(getBaseContext()).addToCart(new Order(
                                FoodId,
                                "(Full)"+currentFood.getName(),
                                fnb.getNumber(),
                                String.valueOf(ffprice),
                                currentFood.getImage(),
                                RestaurantId,
                                MenuId
                        ));
                    }

                    Toast.makeText(FoodDetails.this, "Added To Cart", Toast.LENGTH_SHORT).show();
                    finish();
                }*/
            }
        });

        llquarter = findViewById(R.id.llquarter);
        llhalf = findViewById(R.id.llhalf);
        llfull = findViewById(R.id.llfull);

        food_description=findViewById(R.id.food_description);
        food_name = findViewById(R.id.food_name);
        food_quarterprice=findViewById(R.id.food_quarterprice);
        food_halfprice=findViewById(R.id.food_halfprice);
        food_fullprice=findViewById(R.id.food_fullprice);
        food_quarterfinalprice=findViewById(R.id.food_quarterfinalprice);
        food_halffinalprice =findViewById(R.id.food_halffinalprice);
        food_fullfinalprice=findViewById(R.id.food_fullfinalprice);

        food_quarterdiscount = findViewById(R.id.food_quarterdiscount);
        food_halfdiscount = findViewById(R.id.food_halfdiscount);
        food_fulldiscount = findViewById(R.id.food_fulldiscount);


        food_image= findViewById(R.id.img_food);
        imgveg =findViewById(R.id.imgveg);
        imgnonveg =findViewById(R.id.imgnonveg);
        txtrating = findViewById(R.id.txtrating);





        collapsingToolbarLayout= findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        imggoback = findViewById(R.id.imggoback);
        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnshowreviews = findViewById(R.id.btnshowreview);
        recyclerView =findViewById(R.id.recycler_reviews);
        recycler_addon = findViewById(R.id.recycler_addon);

        layoutManager =new LinearLayoutManager(this);
        layoutManager1 = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recycler_addon.setLayoutManager(layoutManager1);

        addonadapter = new FirebaseRecyclerAdapter<AddOnsModel, AddOnViewHolder>(AddOnsModel.class, R.layout.addonitem_layout, AddOnViewHolder.class,
                foods.child("addons") // find that foods with menuid==categoryid
        ) {

            @Override
            protected void onDataChanged() {
                super.onDataChanged();
                recycler_addon.setAdapter(addonadapter);
            }

            @Override
            protected void populateViewHolder(AddOnViewHolder viewHolder, final AddOnsModel model, final int position) {
                viewHolder.txtaddonitemname.setText(model.getAddonitemname());

                viewHolder.txtaddonitemprice.setText("₹"+model.getAddonitemprice());

                viewHolder.cbaddonitem.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                        {
                           new Database(getBaseContext()).addToCart(new Order(
                                   Integer.parseInt("4"+String.valueOf(position)+timeidstr),
                                   FoodId,
                                   "(AddOn)"+model.getAddonitemname()+" with "+currentFood.getName(),
                                   "1",
                                   model.getAddonitemprice(),
                                   currentFood.getImage(),
                                   RestaurantId,
                                   MenuId
                           ));
                        }
                        else
                        {
                            new Database(getBaseContext()).removeFoodFromCart(Integer.parseInt("4"+String.valueOf(position)+timeidstr));
                        }
                        int ct = new Database(FoodDetails.this).getCountCart();
                        String sct = String.valueOf(ct);
                        if(ct==0)
                            text_count.setVisibility(View.GONE);
                        else {
                            text_count.setVisibility(View.VISIBLE);
                            text_count.setText(sct);
                        }
                    }
                });

            }
        };


        btnshowreviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (btnshowreviews.getText().equals("SHOW REVIEWS"))
                {
                    Toast.makeText(FoodDetails.this, "Please wait...", Toast.LENGTH_SHORT).show();
                    btnshowreviews.setText("HIDE REVIEWS");
                  recyclerView.setVisibility(View.VISIBLE);
                    adapter = new FirebaseRecyclerAdapter<Reviews, ReviewsViewHolder>(Reviews.class, R.layout.reviews_item_layout, ReviewsViewHolder.class,
                            rating // find that foods with menuid==categoryid
                    ) {


                        @Override
                        protected void populateViewHolder(ReviewsViewHolder viewHolder, Reviews model, int position) {
                            viewHolder.txtusername.setText(model.getUsername());

                            viewHolder.txtreview.setText(model.getComment());

                            viewHolder.ratingBar.setRating(Float.parseFloat(model.getRate()));

                        }
                    };

                    recyclerView.setAdapter(adapter);
                }
                else
                {  btnshowreviews.setText("SHOW REVIEWS");
                   recyclerView.setVisibility(View.GONE);

                }
            }
        });



        int ct = new Database(FoodDetails.this).getCountCart();
        String sct = String.valueOf(ct);
        if(ct==0)
            text_count.setVisibility(View.GONE);
        else {
            text_count.setVisibility(View.VISIBLE);
            text_count.setText(sct);
        }

        // setting elegant number btn change listener to update cart
        qnb.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                if(oldValue==0)
                {
                    new Database(getBaseContext()).addToCart(new Order(
                            Integer.parseInt("1"+timeidstr),
                            FoodId,
                            "(Quarter)"+currentFood.getName(),
                            qnb.getNumber(),
                            String.valueOf(fqprice),
                            currentFood.getImage(),
                            RestaurantId,
                            MenuId
                    ));
                }
                else if(qnb.getNumber().equals("0"))
                {
                    new Database(getBaseContext()).removeFoodFromCart(Integer.parseInt("1"+timeidstr));
                }
                else
                {
                    new Database(getBaseContext()).updateFoodCart(String.valueOf(newValue),Integer.parseInt("1"+timeidstr));
                }

                //Toast.makeText(FoodDetails.this, ""+timeidstr, Toast.LENGTH_SHORT).show();

                int ct = new Database(FoodDetails.this).getCountCart();
                String sct = String.valueOf(ct);
                if(ct==0)
                    text_count.setVisibility(View.GONE);
                else {
                    text_count.setVisibility(View.VISIBLE);
                    text_count.setText(sct);
                }
            }
        });
        hnb.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                if(oldValue==0)
                {
                    new Database(getBaseContext()).addToCart(new Order(
                            Integer.parseInt("2"+timeidstr),
                            FoodId,
                            "(Half)"+currentFood.getName(),
                            hnb.getNumber(),
                            String.valueOf(fhprice),
                            currentFood.getImage(),
                            RestaurantId,
                            MenuId
                    ));
                }
                else if(hnb.getNumber().equals("0"))
                {
                    new Database(getBaseContext()).removeFoodFromCart(Integer.parseInt("2"+timeidstr));
                }
                else
                {
                    new Database(getBaseContext()).updateFoodCart(String.valueOf(newValue),Integer.parseInt("2"+timeidstr));
                }
                int ct = new Database(FoodDetails.this).getCountCart();
                String sct = String.valueOf(ct);
                if(ct==0)
                    text_count.setVisibility(View.GONE);
                else {
                    text_count.setVisibility(View.VISIBLE);
                    text_count.setText(sct);
                }
            }
        });
        fnb.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                if(oldValue==0)
                {
                    new Database(getBaseContext()).addToCart(new Order(
                            Integer.parseInt("3"+timeidstr),
                            FoodId,
                            "(Full)"+currentFood.getName(),
                            fnb.getNumber(),
                            String.valueOf(ffprice),
                            currentFood.getImage(),
                            RestaurantId,
                            MenuId
                    ));
                }
                else if(fnb.getNumber().equals("0"))
                {
                    new Database(getBaseContext()).removeFoodFromCart(Integer.parseInt("3"+timeidstr));

                }
                else
                {
                    new Database(getBaseContext()).updateFoodCart(String.valueOf(newValue),Integer.parseInt("3"+timeidstr));

                }
                int ct = new Database(FoodDetails.this).getCountCart();
                String sct = String.valueOf(ct);
                if(ct==0)
                    text_count.setVisibility(View.GONE);
                else {
                    text_count.setVisibility(View.VISIBLE);
                    text_count.setText(sct);
                }
            }
        });

    }




    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite OK","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this food")
                .setDescription("Please rate this food and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.gray)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.colorPrimary)
                .setCommentBackgroundColor(R.color.gray)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(FoodDetails.this)
                .show();


    }

    private void getDetailFood(String foodId) {

        foods.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);
                if(currentFood.getVeg().equals("1")) {
                    imgveg.setVisibility(View.VISIBLE);
                    food_name.setTextColor(getResources().getColor(R.color.green));
                }
                else
                    imgnonveg.setVisibility(View.VISIBLE);

                Double drating = Double.parseDouble(currentFood.getRating());
                String rat = String.format("%.1f",drating);
                txtrating.setText(rat);

                collapsingToolbarLayout.setTitle(currentFood.getName());
                //collapsingToolbarLayout.setContentScrim();

                if(currentFood.getQuarterprice().equals("null"))
                    llquarter.setVisibility(View.GONE);
                if(currentFood.getHalfprice().equals("null"))
                    llhalf.setVisibility(View.GONE);
                if(currentFood.getFullprice().equals("null"))
                    llfull.setVisibility(View.GONE);

                if(!currentFood.getQuarterprice().equals("null"))
                {
                    food_quarterprice.setText("₹"+currentFood.getQuarterprice());
                    food_quarterdiscount.setText(currentFood.getQuarterdiscount()+"% OFF");
                    food_quarterprice.setPaintFlags(food_quarterprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    String qpricestr = currentFood.getQuarterprice();
                    String qdiscountstr = currentFood.getQuarterdiscount();
                    double qdis = Double.parseDouble(qdiscountstr);
                    double qprice = Double.parseDouble(qpricestr);
                     double fqpriced = qprice - ( (qdis*qprice)/100.0 );
                    fqprice = (int) fqpriced;
                    food_quarterfinalprice.setText("₹"+fqprice);

                }
                if(!currentFood.getHalfprice().equals("null"))
                {
                    food_halfprice.setText("₹"+currentFood.getHalfprice());
                    food_halfdiscount.setText(currentFood.getHalfdiscount()+"% OFF");
                    food_halfprice.setPaintFlags(food_halfprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    String hpricestr = currentFood.getHalfprice();
                    String hdiscountstr = currentFood.getHalfdiscount();
                    double hdis = Double.parseDouble(hdiscountstr);
                    double hprice = Double.parseDouble(hpricestr);
                     double fhpriced = hprice - ( (hdis*hprice)/100.0 );
                    fhprice = (int) fhpriced;
                    food_halffinalprice.setText("₹"+fhprice);

                }
                if(!currentFood.getFullprice().equals("null"))
                {
                    food_fullprice.setText("₹"+currentFood.getFullprice());
                    food_fulldiscount.setText(currentFood.getFulldiscount()+"% OFF");
                    food_fullprice.setPaintFlags(food_fullprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    String fpricestr = currentFood.getFullprice();
                    String fdiscountstr = currentFood.getFulldiscount();
                    double fdis = Double.parseDouble(fdiscountstr);
                    double fprice = Double.parseDouble(fpricestr);
                     double ffpriced = fprice - ( (fdis*fprice)/100.0 );
                    ffprice = (int) ffpriced;
                    food_fullfinalprice.setText("₹"+ffprice);

                }

                //food_price.setText("₹"+currentFood.getPrice());
                food_name.setText(currentFood.getName());
                txttotalrates.setText("("+currentFood.getTotalrates()+")");
                ratingBar.setRating(Float.valueOf(currentFood.getRating()));
                food_description.setText(currentFood.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onPositiveButtonClicked(final int values, String comments) {
        //get rating and upload to  firebase


        value=values;

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
         phone = sharedPreferences.getString("phone","N/A");
        name = sharedPreferences.getString("name","N/A");

         ratin = new Rating(name,comments,String.valueOf(value));

        rating.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(phone).exists())
                {
                    flag=1;
                    String oldrat = dataSnapshot.child(phone).child("rate").getValue().toString();
                    oldrating = Double.parseDouble(oldrat);
                    updaterating();
                }
                else
                {
                    flag=0;
                    updaterating();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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
                String mprevrating = dataSnapshot.child("rating").getValue().toString();
                String mprevtotalrates = dataSnapshot.child("totalrates").getValue().toString();
                Double mdprevrating = Double.parseDouble(mprevrating);
                Double mdprevtotalrates =Double.parseDouble(mprevtotalrates);
                Double mprod =mdprevrating * mdprevtotalrates;
                mprod += value-oldrating;

                if(flag==0)
                    mdprevtotalrates+=1.0;

                mprod =mprod/mdprevtotalrates;
                menurating.child("rating").setValue(String.valueOf(mprod));
                String ntotalrates = String.format("%.0f",mdprevtotalrates);
                menurating.child("totalrates").setValue(String.valueOf(ntotalrates));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //change HotDealsRef rating

        restaurantrating.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String rprevrating = dataSnapshot.child("rating").getValue().toString();
                String rprevtotalrates = dataSnapshot.child("totalrates").getValue().toString();
                Double rdprevrating = Double.parseDouble(rprevrating);
                Double rdprevtotalrates =Double.parseDouble(rprevtotalrates);
                Double rprod =rdprevrating * rdprevtotalrates;
                rprod += value-oldrating;

                if(flag==0)
                    rdprevtotalrates+=1.0;

                rprod =rprod/rdprevtotalrates;
                restaurantrating.child("rating").setValue(String.valueOf(rprod));

                String ntotalrates = String.format("%.0f",rdprevtotalrates);
                restaurantrating.child("totalrates").setValue(String.valueOf(ntotalrates));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        getDetailFood(FoodId);


    }


}
