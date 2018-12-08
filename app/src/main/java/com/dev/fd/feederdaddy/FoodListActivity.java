package com.dev.fd.feederdaddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andremion.counterfab.CounterFab;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.ViewHolder.FoodViewHolder;
import com.dev.fd.feederdaddy.model.Food;
import com.dev.fd.feederdaddy.model.Menu;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodlist;

    ImageView imggoback;
    ProgressBar progressBar;

    String MenuId="",RestaurantId="",city;

    TextView text_count;
        FloatingActionButton fab;

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    //search food adapter
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> SuggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);

        city = sharedPreferences.getString("city","N/A");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        imggoback = findViewById(R.id.imggoback);
        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        progressBar=findViewById(R.id.progressbar);



        //init firebase
        database = FirebaseDatabase.getInstance();


        recyclerView = findViewById(R.id.recycler_food);
        //recyclerView.setHasFixedSize(true);
        //layoutManager =new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
        layoutManager =new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);

        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);

        //getting categoryid passed by intent here
        if(getIntent()!=null) {
            MenuId = getIntent().getStringExtra("MenuId");
            RestaurantId = getIntent().getStringExtra("RestaurantId");
            foodlist = database.getReference(city).child("Foods").child(RestaurantId).child(MenuId);

            //load foof list
            adapter =new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,R.layout.food_item,FoodViewHolder.class,
                    foodlist // find that foods with menuid==categoryid
            ) {
                @Override
                protected void onDataChanged() {
                    super.onDataChanged();

                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }
                    loadfoodlist();

                }

                @Override
                protected void populateViewHolder(final FoodViewHolder viewHolder, Food model, int position) {
                    Typeface face = Typeface.createFromAsset(getAssets(),"NABILA.TTF");
                    viewHolder.food_price.setText("₹ "+model.getFullprice());


                    foodlist.child(adapter.getRef(position).getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("hidefood").getValue()!=null)
                            {
                                    int color = R.color.black_filter;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        viewHolder.food_image.setForeground(new ColorDrawable(ContextCompat.getColor(getBaseContext(), color)));
                                    }
                                    viewHolder.food_price.setText("Out of Stock");
                            }
                            else
                            {
                                int color = R.color.gray_filter;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    viewHolder.food_image.setForeground(new ColorDrawable(ContextCompat.getColor(getBaseContext(), color)));
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    viewHolder.food_name.setTypeface(face);
                    viewHolder.food_name.setText(model.getName());
                    Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);
                    Double drating = Double.parseDouble(model.getRating());
                    String rat = String.format("%.1f",drating);
                    viewHolder.txtrating.setText(rat);
                    viewHolder.txttotalrates.setText("("+model.getTotalrates()+")");
                    viewHolder.ratingBar.setRating(Float.parseFloat(model.getRating()));


                    if(model.getVeg().equals("0"))
                    {
                        viewHolder.imgveg.setVisibility(View.GONE);
                    }
                    else if(model.getNonveg().equals("0"))
                    {
                        viewHolder.imgnonveg.setVisibility(View.GONE);
                    }

                    final Food local = model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();

                            //start food details activity

                            //start food details activity
                            Intent fooddetail = new Intent(FoodListActivity.this,FoodDetails.class);
                            fooddetail.putExtra("comeback","yes");
                            fooddetail.putExtra("FoodId",adapter.getRef(position).getKey());
                            fooddetail.putExtra("RestaurantId",RestaurantId);
                            fooddetail.putExtra("MenuId", MenuId);
                            if(viewHolder.food_price.getText().equals("Out of Stock"))
                            {
                                fooddetail.putExtra("isoutofstock","1");
                            }
                            else
                            {
                                fooddetail.putExtra("isoutofstock","0");
                            }
                            startActivityForResult(fooddetail,1);
                        }
                    });


                }
            };

        }
        if(!MenuId.isEmpty() && MenuId!=null){

            //loadfoodlist(MenuId);
        }


        fab =  findViewById(R.id.fab);
        text_count = findViewById(R.id.text_count);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent i = new Intent(FoodListActivity.this,Cart.class);
                startActivity(i) ;

            }
        });
        int ct = new Database(this).getCountCart();
        String sct = String.valueOf(ct);
        if(ct==0)
            text_count.setVisibility(View.GONE);
        else {
            text_count.setVisibility(View.VISIBLE);
            text_count.setText(sct);
        }


        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter Food Name");
        loadSuggest();

        materialSearchBar.setLastSuggestions(SuggestList);
        materialSearchBar.setCardViewElevation(0);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for(String search: SuggestList)
                {
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())){
                        suggest.add(search);
                    }
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //when search bar is close
                //restore original adapter
                if(!enabled){
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //when search finish
                //show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

        final SwipeRefreshLayout pulltorefreshhome = findViewById(R.id.pulltorefreshhome);

        pulltorefreshhome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
                finish();
            }
        });


    }

    private void startSearch(CharSequence text) {


        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodlist.orderByChild("name").equalTo(text.toString())
        ) {

            @Override
            protected void onDataChanged() {
                super.onDataChanged();

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                Typeface face = Typeface.createFromAsset(getAssets(),"NABILA.TTF");
                viewHolder.food_name.setTypeface(face);

                viewHolder.food_name.setText(model.getName());
                viewHolder.food_price.setText("₹ "+model.getFullprice());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                Double drating = Double.parseDouble(model.getRating());
                String rat = String.format("%.1f",drating);
                viewHolder.txtrating.setText(rat);
                viewHolder.txttotalrates.setText("("+model.getTotalrates()+")");
                viewHolder.ratingBar.setRating(Float.parseFloat(model.getRating()));



                if(model.getVeg().equals("0"))
                {
                    viewHolder.imgveg.setVisibility(View.GONE);
                }
                else if(model.getNonveg().equals("0"))
                {
                    viewHolder.imgnonveg.setVisibility(View.GONE);
                }

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override

                    public void onClick(View view, int position, boolean isLongClick) {
                        // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();

                        //start food details activity
                        Intent fooddetail = new Intent(FoodListActivity.this,FoodDetails.class);
                        fooddetail.putExtra("comeback","yes");
                        fooddetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        fooddetail.putExtra("RestaurantId",RestaurantId);
                        fooddetail.putExtra("MenuId", MenuId);
                        startActivityForResult(fooddetail,1);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);

    }



    private void loadSuggest() {
        foodlist.orderByChild("hidefood").equalTo(null).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null) {

                            for (DataSnapshot postSnaphot : dataSnapshot.getChildren()) {
                                Food item = postSnaphot.getValue(Food.class);
                                SuggestList.add(item.getName());

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadfoodlist() {

        recyclerView.setAdapter(adapter);

        //animation
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        int ct = new Database(this).getCountCart();
        String sct = String.valueOf(ct);
        if(ct==0)
            text_count.setVisibility(View.GONE);
        else {
            text_count.setVisibility(View.VISIBLE);
            text_count.setText(sct);
        }
        //animation
        if(recyclerView!=null  && recyclerView.getAdapter()!=null) {
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.scheduleLayoutAnimation();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
                if(result.equals("1"))
                {
                    finish();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }





}
