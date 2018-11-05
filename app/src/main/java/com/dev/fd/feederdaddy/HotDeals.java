package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.ViewHolder.FoodViewHolder;
import com.dev.fd.feederdaddy.ViewHolder.HotDealViewHolder;
import com.dev.fd.feederdaddy.model.Food;
import com.dev.fd.feederdaddy.model.HotDeal;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.dev.fd.feederdaddy.OrderMeal.deliverycharges;

public class HotDeals extends Fragment implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseDatabase database;
    DatabaseReference HotDealsRef,restref;

    DrawerLayout drawer;

    FloatingActionButton fab;
    TextView text_count;


    RecyclerView recylermenu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<HotDeal,HotDealViewHolder> adapter;

    ProgressBar progressBar;

    String city,isopen="1";

    SharedPreferences sharedPreferences;
    public static String phone,userlatitude,userlongitude,deliverycharges;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_hot_deals, null);

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = this.getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);

        city = sharedPreferences.getString("city", "N/A");

        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        TextView txtcallorwhatsapp = view.findViewById(R.id.txtcallorwhatsapp);
        progressBar = view.findViewById(R.id.progressbar);

        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.WHITE);
        toolbar.setTitle("Hot Deals");

        fab = view.findViewById(R.id.fab);
        text_count = view.findViewById(R.id.text_count);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(),Cart.class);
                startActivity(i) ;
            }
        });
        int ct = new Database(getActivity()).getCountCart();
        String sct = String.valueOf(ct);
        if(ct==0)
            text_count.setVisibility(View.GONE);
        else {
            text_count.setVisibility(View.VISIBLE);
            text_count.setText(sct);
        }


        drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this.getActivity(), drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                ((MainActivity) getActivity()).hidebottomnavigationbar();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                ((MainActivity) getActivity()).showbottomnavigationbar();

            }
        };


        drawer.addDrawerListener(toggle);

        toggle.syncState();
        {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                ((MainActivity) getActivity()).hidebottomnavigationbar();
            }
        }

        NavigationView navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set name for user
        View headerview = navigationView.getHeaderView(0);


        if (isConnected) {
//
//            if (Common.currentfragment.equals("bakery"))
//                isbakerystr = "1";
//            else
//                isbakerystr = "0";

            txtcallorwhatsapp.setVisibility(View.GONE);

            recylermenu = view.findViewById(R.id.recycler_hotdeals);
            recylermenu.setHasFixedSize(true);
            layoutManager =new LinearLayoutManager(getActivity());
            recylermenu.setLayoutManager(layoutManager);

            //init firebase
            database = FirebaseDatabase.getInstance();
            HotDealsRef = database.getReference(city).child("Foods").child("-1").child("1");
            restref  = database.getReference(city).child("Restaurant").child("-1").child("isopen");

            restref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.getValue().toString().equals("1"))
                        isopen="1";
                    else
                    {isopen="0";
                        toolbar.setTitle("Hot Deals (Currently Closed)");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            phone = sharedPreferences.getString("phone", "N/A");
            userlatitude = sharedPreferences.getString("latitude", "0.0");
            userlongitude = sharedPreferences.getString("longitude", "0.0");

            //load food list
            adapter =new FirebaseRecyclerAdapter<HotDeal, HotDealViewHolder>(HotDeal.class,R.layout.hotdeal_item,HotDealViewHolder.class,
                    HotDealsRef
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
                protected void populateViewHolder(HotDealViewHolder viewHolder, HotDeal model, int position) {

                    Typeface face = Typeface.createFromAsset(getActivity().getAssets(),"NABILA.TTF");
                    viewHolder.food_name.setTypeface(face);

                    viewHolder.food_price.setText("₹ "+model.getFullprice());
                    viewHolder.food_name.setText(model.getName());
                    Picasso.with(getActivity()).load(model.getImage()).into(viewHolder.food_image);


                    if(model.getVeg().equals("0"))
                    {
                        viewHolder.imgveg.setVisibility(View.GONE);
                    }
                    else if(model.getNonveg().equals("0"))
                    {
                        viewHolder.imgnonveg.setVisibility(View.GONE);
                    }

                    final HotDeal local = model;
                    viewHolder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, final int position, boolean isLongClick) {
                            // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();
                            //start food details activity
                            String resid = sharedPreferences.getString("restaurantid","N/A");
                            int ct = new Database(getActivity()).getCountCart();

                            if (ct!=0 && !resid.equals("N/A") && !resid.equals("-1"))
                            {
                                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme);
                                alertDialogBuilder.setTitle("Empty Your Cart");
                                alertDialogBuilder.setMessage("You already have some items in your cart from other restaurant. Do you wish to empty your cart?");
                                alertDialogBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        new Database(getActivity()).cleanCart();

                                        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("restaurantid", "-1");
                                        editor.putString("isopen",isopen);
                                        editor.putString("deliveryrate","0");
                                        editor.putString("mindcdistance", "0.0");
                                        editor.putString("mindeliverycharge","0");
                                        editor.commit();
                                        Intent fooddetail = new Intent(getActivity(),FoodDetails.class);
                                        fooddetail.putExtra("isHotDeal","1");
                                        fooddetail.putExtra("FoodId",adapter.getRef(position).getKey());
                                        fooddetail.putExtra("RestaurantId","-1");
                                        fooddetail.putExtra("MenuId","1");
                                        startActivity(fooddetail);
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
                            else
                            {

                                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyData", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("restaurantid", "-1");
                                editor.putString("isopen",isopen);
                                editor.putString("deliveryrate","0");
                                editor.putString("mindcdistance", "0.0");
                                editor.putString("mindeliverycharge","0");
                                editor.commit();
                                Intent fooddetail = new Intent(getActivity(),FoodDetails.class);
                                fooddetail.putExtra("isHotDeal","1");
                                fooddetail.putExtra("FoodId",adapter.getRef(position).getKey());
                                fooddetail.putExtra("RestaurantId","-1");
                                fooddetail.putExtra("MenuId","1");

                                startActivity(fooddetail);
                            }


                        }
                    });

                }
            };

        }

        final SwipeRefreshLayout pulltorefreshhome = view.findViewById(R.id.pulltorefreshhome);

        pulltorefreshhome.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //startActivity(getActivity().getIntent());
                ((MainActivity) getActivity()).refreshhotdeals();
                //getActivity().finish();

            }
        });



    }


    private void loadfoodlist() {
        recylermenu.setAdapter(adapter);

        }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            Intent i = new Intent(HotDeals.this.getActivity(),ProfileActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_cart) {
             Intent i = new Intent(HotDeals.this.getActivity(),Cart.class);
             startActivity(i);
        } else if (id == R.id.nav_orders) {
              Intent i = new Intent(HotDeals.this.getActivity(),Orders.class);
             startActivity(i);

        } else if (id == R.id.nav_ordermeal) {
            //Intent i = new Intent(MainActivity.this,SignIn.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //startActivity(i);
        }
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        int ct = new Database(getActivity()).getCountCart();
        String sct = String.valueOf(ct);
        if(ct==0)
            text_count.setVisibility(View.GONE);
        else {
            text_count.setVisibility(View.VISIBLE);
            text_count.setText(sct);
        }
    }




}
