package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.ViewHolder.MenuViewHolder;
import com.dev.fd.feederdaddy.model.Menu;
import com.dev.fd.feederdaddy.model.RestaurantBanner;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference menulist,restcustommessageref;

    String RestaurantId="",city;

    ImageView imggoback;
    ProgressBar progressBar;

    TextView text_count;

    FirebaseRecyclerAdapter<Menu,MenuViewHolder> adapter;

    //search menu adapter
    FirebaseRecyclerAdapter<Menu,MenuViewHolder> searchAdapter;
    List<String> SuggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    //slider
    HashMap<String, String> image_list;
    SliderLayout sliderLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city","N/A");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        progressBar = findViewById(R.id.progressbar);

        //init firebase
        database = FirebaseDatabase.getInstance();

        imggoback = findViewById(R.id.imggoback);
        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        recyclerView = findViewById(R.id.recycler_menulist);
        //recyclerView.setHasFixedSize(true);
        layoutManager =new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),
                R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);


        FloatingActionButton fab = findViewById(R.id.fab);
        text_count = findViewById(R.id.text_count);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent i = new Intent(MenuActivity.this,Cart.class);
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





        //getting restaurantid passed by intent here
        if(getIntent()!=null) {
            RestaurantId = getIntent().getStringExtra("RestaurantId");
            menulist = database.getReference(city).child("Menus").child(RestaurantId);
            /*restcustommessageref = database.getReference(city).child("Restaurant").child(RestaurantId).child("custommessage");
            restcustommessageref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.getValue()!=null)
                    {       String cmstr = dataSnapshot.getValue().toString();
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MenuActivity.this,R.style.MyDialogTheme);
                            alertDialogBuilder.setTitle("Are You Sure?");
                            alertDialogBuilder.setMessage(cmstr);
                            alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                            alertDialogBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            alertDialogBuilder.show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });*/

            //loading list
            adapter =new FirebaseRecyclerAdapter<Menu,MenuViewHolder>(Menu.class,R.layout.menu_item,MenuViewHolder.class,
                    menulist// find that foods with menuid==categoryid
            ) {
                @Override
                protected void onDataChanged() {
                    super.onDataChanged();

                    if (progressBar != null) {
                        progressBar.setVisibility(View.GONE);
                    }

                    loadmenulist(RestaurantId);


                }

                @Override
                protected void populateViewHolder(MenuViewHolder viewHolder, Menu model, int position) {
                    Typeface face = Typeface.createFromAsset(getAssets(),"NABILA.TTF");
                    viewHolder.txtmenuname.setTypeface(face);
                    viewHolder.txtmenuname.setText(model.getName());
                    Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgmenuitem);
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

                    final Menu local = model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position, boolean isLongClick) {
                            // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();

                            //start food details activity
                            Intent foodlist = new Intent(MenuActivity.this,FoodListActivity.class);
                            foodlist.putExtra("RestaurantId",RestaurantId);
                            foodlist.putExtra("MenuId",adapter.getRef(position).getKey());
                            startActivity(foodlist);
                        }
                    });


                }
            };

        }
        if(!RestaurantId.isEmpty() && RestaurantId!=null){

        }

        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter Menu Item Name");
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

        //setup slider
        //need call this function after you init database

        setupSlider();

        final SwipeRefreshLayout pulltorefreshhome = findViewById(R.id.pulltorefreshhome);

        pulltorefreshhome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
                finish();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        sliderLayout.stopAutoCycle();
    }

    private void setupSlider() {
        sliderLayout = findViewById(R.id.slidermenu);
        sliderLayout.setVisibility(View.INVISIBLE);
        sliderLayout.stopAutoCycle();
        image_list = new HashMap<>();

        DatabaseReference menubannerref = database.getReference(city).child("RestaurantBanner").child(RestaurantId);

        menubannerref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {


                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        RestaurantBanner banner = postSnapshot.getValue(RestaurantBanner.class);

                        // we will concat string name and id
                        image_list.put(banner.getName() + "@#@" + banner.getRestaurantid() + "@#@" + banner.getMenuid() + "@#@" + banner.getFoodid(), banner.getImage());

                    }
                    for (String key : image_list.keySet()) {
                        String[] keySplit = key.split("@#@");
                        String nameoffood = keySplit[0];
                        final String restaurantid = keySplit[1];
                        final String menuid = keySplit[2];
                        final String foodid = keySplit[3];

                        //create slider
                        TextSliderView textSliderView = new TextSliderView(getBaseContext());
                        textSliderView.description(nameoffood)
                                .image(image_list.get(key))
                                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        Intent intent = new Intent(MenuActivity.this, FoodDetails.class);

                                        //we will start food details activity by sending intent extras
                                        intent.putExtra("comeback", "yes");
                                        intent.putExtra("RestaurantId", restaurantid);
                                        intent.putExtra("MenuId", menuid);
                                        intent.putExtra("FoodId", foodid);
                                        startActivity(intent);

                                    }
                                });

                        sliderLayout.addSlider(textSliderView);
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sliderLayout.setVisibility(View.VISIBLE);
                            sliderLayout.startAutoCycle();
                        }
                    }, 0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //sliderLayout.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Right_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(4000);



    }

    private void startSearch(CharSequence text) {

        searchAdapter = new FirebaseRecyclerAdapter<Menu, MenuViewHolder>(
                Menu.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                menulist.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void onDataChanged() {
                super.onDataChanged();

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Menu model, int position) {
                Typeface face = Typeface.createFromAsset(getAssets(),"NABILA.TTF");
                viewHolder.txtmenuname.setTypeface(face);
                viewHolder.txtmenuname.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgmenuitem);
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


                final Menu local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override

                    public void onClick(View view, int position, boolean isLongClick) {
                        // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();

                        //start food details activity
                        Intent foodlist = new Intent(MenuActivity.this,FoodListActivity.class);
                        foodlist.putExtra("RestaurantId",RestaurantId);
                        foodlist.putExtra("MenuId",adapter.getRef(position).getKey());
                        startActivity(foodlist);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);


    }



    private void loadSuggest() {
        menulist.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getValue()!=null) {

                            for (DataSnapshot postSnaphot : dataSnapshot.getChildren()) {
                                Menu item = postSnaphot.getValue(Menu.class);
                                SuggestList.add(item.getName());

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadmenulist(String categoryId) {

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
}
