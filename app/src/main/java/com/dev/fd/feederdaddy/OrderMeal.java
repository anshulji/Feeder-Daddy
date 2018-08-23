package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.R;
import com.dev.fd.feederdaddy.ViewHolder.MenuViewHolder;
import com.dev.fd.feederdaddy.ViewHolder.RestaurantAdapter;
import com.dev.fd.feederdaddy.ViewHolder.RestaurantViewHolder;
import com.dev.fd.feederdaddy.model.Restaurant;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class OrderMeal extends Fragment implements NavigationView.OnNavigationItemSelectedListener {


    FirebaseDatabase database;
    DatabaseReference restaurant,refdelivercharges;

    TextView txtfullname;

    DrawerLayout drawer;

    RecyclerView recylermenu;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Restaurant,RestaurantViewHolder> adapter;

    List<Restaurant> restaurantList = new ArrayList<>();
    List<Restaurant> searchedList = new ArrayList<>();
    RestaurantAdapter restaurantAdapter,searchAdapter ;
    List<String> SuggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    ProgressBar progressBar;

    SharedPreferences sharedPreferences;
    public static String phone,userlatitude,userlongitude,deliverycharges;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_order_meal, null);
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.sortby_menu);

        progressBar=view.findViewById(R.id.progressbar);


        BottomNavigationView bottomNavigationView =view.findViewById(R.id.bottomnavigationview);




        //init firebase
        database = FirebaseDatabase.getInstance();





        restaurant = database.getReference("Restaurant");

        sharedPreferences = this.getActivity().getSharedPreferences("MyData", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone","N/A");
        userlatitude = sharedPreferences.getString("latitude","0.0");
        userlongitude = sharedPreferences.getString("longitude","0.0");



        //setSupportActionBar(toolbar);


        drawer = (DrawerLayout) view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this.getActivity(), drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set name for user
        View headerview = navigationView.getHeaderView(0);

        // load menu
        recylermenu =  view.findViewById(R.id.recycler_menu);
        recylermenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this.getActivity());
        recylermenu.setLayoutManager(layoutManager);

        //loadRestaurants();


        //regiuster service
        //Intent i = new Intent(Home.this,ListenOrder.class);
        //startService(i);

        refdelivercharges = database.getReference("DeliveryCharges");
        refdelivercharges.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliverycharges = dataSnapshot.getValue().toString();
                loadRestaurantlist();
                //search bar
                materialSearchBar = view.findViewById(R.id.SearchBar);
                materialSearchBar.setHint("Enter Restaurant Name");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();

                //noinspection SimplifiableIfStatement
                if (id == R.id.rating) {
                    Collections.sort(restaurantList, new Comparator<Restaurant>() {

                        @Override
                        public int compare(Restaurant r1, Restaurant r2) {

                            return (r2.getRating()).compareTo(r1.getRating());
                        }
                    });

                    restaurantAdapter = new RestaurantAdapter(restaurantList,OrderMeal.this.getActivity());
                    recylermenu.setAdapter(restaurantAdapter);
                    return true;
                }
                if (id == R.id.alpha) {
                    Collections.sort(restaurantList, new Comparator<Restaurant>() {

                        @Override
                        public int compare(Restaurant r1, Restaurant r2) {

                            return (r1.getName()).compareTo(r2.getName());
                        }
                    });

                    restaurantAdapter = new RestaurantAdapter(restaurantList,OrderMeal.this.getActivity());
                    recylermenu.setAdapter(restaurantAdapter);
                    return true;
                }
                if (id == R.id.nearest) {
                    Collections.sort(restaurantList, new Comparator<Restaurant>() {

                        @Override
                        public int compare(Restaurant r1, Restaurant r2) {

                            Double distance1 = getDistanceFromLatLonInKm(Double.parseDouble(OrderMeal.userlatitude),Double.parseDouble(userlongitude),Double.parseDouble(r1.getLatitude()),Double.parseDouble(r1.getLongitude()));
                            Double distance2 = getDistanceFromLatLonInKm(Double.parseDouble(OrderMeal.userlatitude),Double.parseDouble(userlongitude),Double.parseDouble(r2.getLatitude()),Double.parseDouble(r2.getLongitude()));
                            String dist1 = String.format("%.2f",distance1);
                            String dist2 = String.format("%.2f",distance2);

                            return (dist1).compareTo(dist2);
                        }
                    });

                    restaurantAdapter = new RestaurantAdapter(restaurantList,OrderMeal.this.getActivity());
                    recylermenu.setAdapter(restaurantAdapter);
                    return true;
                }
                if (id == R.id.deliverycost) {
                    Collections.sort(restaurantList, new Comparator<Restaurant>() {

                        @Override
                        public int compare(Restaurant r1, Restaurant r2) {

                            Double distance1 = getDistanceFromLatLonInKm(Double.parseDouble(OrderMeal.userlatitude),Double.parseDouble(userlongitude),Double.parseDouble(r1.getLatitude()),Double.parseDouble(r1.getLongitude()));
                            Double distance2 = getDistanceFromLatLonInKm(Double.parseDouble(OrderMeal.userlatitude),Double.parseDouble(userlongitude),Double.parseDouble(r2.getLatitude()),Double.parseDouble(r2.getLongitude()));
                            String dist1 = String.format("%.2f",distance1);
                            String dist2 = String.format("%.2f",distance2);

                            return (dist1).compareTo(dist2);
                        }
                    });

                    restaurantAdapter = new RestaurantAdapter(restaurantList,OrderMeal.this.getActivity());
                    recylermenu.setAdapter(restaurantAdapter);
                    return true;
                }

                return true;
            }
        });




    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_meal);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.WHITE);

        progressBar=findViewById(R.id.progressbar);


        BottomNavigationView bottomNavigationView =findViewById(R.id.bottomnavigationview);




        //init firebase
        database = FirebaseDatabase.getInstance();





        restaurant = database.getReference("Restaurant");

        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        phone = sharedPreferences.getString("phone","N/A");
        userlatitude = sharedPreferences.getString("latitude","0.0");
        userlongitude = sharedPreferences.getString("longitude","0.0");



        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //set name for user
        View headerview = navigationView.getHeaderView(0);

        // load menu
        recylermenu =  findViewById(R.id.recycler_menu);
        recylermenu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recylermenu.setLayoutManager(layoutManager);

        //loadRestaurants();


        //regiuster service
        //Intent i = new Intent(Home.this,ListenOrder.class);
        //startService(i);

        refdelivercharges = database.getReference("DeliveryCharges");
        refdelivercharges.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                deliverycharges = dataSnapshot.getValue().toString();
                loadRestaurantlist();
                //search bar
                materialSearchBar = findViewById(R.id.SearchBar);
                materialSearchBar.setHint("Enter Restaurant Name");


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }*/

    private void startSearch(CharSequence text) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        searchedList.clear();
        restaurant.orderByChild("name").equalTo(text.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Restaurant res = postSnapshot.getValue(Restaurant.class);

                    searchedList.add(res);
                }
                searchAdapter = new RestaurantAdapter(searchedList,OrderMeal.this.getActivity());
                recylermenu.setAdapter(searchAdapter);

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadSuggest() {
        for(int i=0;i<restaurantList.size();i++)
        {
            SuggestList.add(restaurantList.get(i).getName());
        }
    }

    private void loadRestaurantlist() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        restaurant.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    Restaurant res = postSnapshot.getValue(Restaurant.class);
                    restaurantList.add(res);
                    //restaurantList.add(Integer.parseInt(postSnapshot.getKey()),res);
                }
                restaurantAdapter = new RestaurantAdapter(restaurantList,OrderMeal.this.getActivity());
                recylermenu.setAdapter(restaurantAdapter);

                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                loadSuggest();

                materialSearchBar.setLastSuggestions(SuggestList);
                materialSearchBar.setCardViewElevation(10);
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
                            recylermenu.setAdapter(restaurantAdapter);
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


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

   /* private void loadRestaurants() {
        Query query = restaurant.orderByChild("name"); // or ...orderByChild("username");
        adapter = new FirebaseRecyclerAdapter<Restaurant, RestaurantViewHolder>(Restaurant.class,R.layout.restaurant_item,RestaurantViewHolder.class,restaurant) {
            @Override
            protected void populateViewHolder(RestaurantViewHolder viewHolder, Restaurant model, int position) {
                //txtrestaurantname
                Typeface face = Typeface.createFromAsset(getAssets(),"NABILA.TTF");
                viewHolder.txtrestaurantname.setTypeface(face);
                viewHolder.txtrestaurantname.setText(model.getName());

                //img veg and nonveg
                if(!model.getVeg().equals("1"))
                   viewHolder.imgveg.setVisibility(View.GONE);
                else if(!model.getNonveg().equals("1"))
                    viewHolder.imgnonveg.setVisibility(View.GONE);

                //txt rating
                viewHolder.txtrating.setText(model.getRating());

                //rating bar
                viewHolder.ratingBar.setRating(Float.parseFloat(model.getRating()));

                //txt distance
                Double distance = getDistanceFromLatLonInKm(Double.parseDouble(userlatitude),Double.parseDouble(userlongitude),Double.parseDouble(model.getLatitude()),Double.parseDouble(model.getLongitude()));
                String dist = String.format("%.2f",distance);
                viewHolder.txtdistance.setText(dist+" km");

                //txt delivery rate
                Double rate = Double.parseDouble(deliverycharges);
                Double charge = distance*rate;
                String dc = String.format("%.0f",charge);
                viewHolder.txtdelivercharge.setText("Delivery ₹"+dc);

                //txt total rates
                viewHolder.txttotalrates.setText("("+model.getTotalrates()+")");

                //txt delivery time
                Double time = 20.0+ (distance/0.2);
                String timestr = String.format("%.0f",time);
                time+=15.0;
                String timestr1 = String.format("%.0f",time);
                viewHolder.txtdeliverytime.setText(timestr+"-"+timestr1);

                //set open or close
                if(model.getIsopen().equals("0"))
                {
                    int color = R.color.black_filter;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        viewHolder.imgresaturant.setForeground(new ColorDrawable(ContextCompat.getColor(OrderMeal.this, color)));
                    }
                    viewHolder.txtclosed.setVisibility(View.VISIBLE);
                }
                else
                {
                    int color = R.color.gray_filter;
                    viewHolder.txtclosed.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        viewHolder.imgresaturant.setForeground(new ColorDrawable(ContextCompat.getColor(OrderMeal.this, color)));
                    }
                }



                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgresaturant);

                final Restaurant clickitem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Toast.makeText(Home.this, ""+clickitem.getName(), Toast.LENGTH_SHORT).show();
                        //get category id and send to foodlist activity
                        Intent foodlist = new Intent(OrderMeal.this,MenuActivity.class);
                        foodlist.putExtra("RestaurantId",adapter.getRef(position).getKey());
                        startActivity(foodlist);
                    }
                });

            }
            @Override
            public void onDataChanged() {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        };
        recylermenu.setAdapter(adapter);
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
    */


   /* @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

*/
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.sortby_menu, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.rating) {
            Collections.sort(restaurantList, new Comparator<Restaurant>() {

                @Override
                public int compare(Restaurant r1, Restaurant r2) {

                    return (r2.getRating()).compareTo(r1.getRating());
                }
            });

            restaurantAdapter = new RestaurantAdapter(restaurantList,OrderMeal.this.getActivity());
            recylermenu.setAdapter(restaurantAdapter);
            return true;
        }
        if (id == R.id.alpha) {
            Collections.sort(restaurantList, new Comparator<Restaurant>() {

                @Override
                public int compare(Restaurant r1, Restaurant r2) {

                    return (r1.getName()).compareTo(r2.getName());
                }
            });

            restaurantAdapter = new RestaurantAdapter(restaurantList,OrderMeal.this.getActivity());
            recylermenu.setAdapter(restaurantAdapter);
            return true;
        }
        if (id == R.id.nearest) {
            Collections.sort(restaurantList, new Comparator<Restaurant>() {

                @Override
                public int compare(Restaurant r1, Restaurant r2) {

                    Double distance1 = getDistanceFromLatLonInKm(Double.parseDouble(OrderMeal.userlatitude),Double.parseDouble(userlongitude),Double.parseDouble(r1.getLatitude()),Double.parseDouble(r1.getLongitude()));
                    Double distance2 = getDistanceFromLatLonInKm(Double.parseDouble(OrderMeal.userlatitude),Double.parseDouble(userlongitude),Double.parseDouble(r2.getLatitude()),Double.parseDouble(r2.getLongitude()));
                    String dist1 = String.format("%.2f",distance1);
                    String dist2 = String.format("%.2f",distance2);

                    return (dist1).compareTo(dist2);
                }
            });

            restaurantAdapter = new RestaurantAdapter(restaurantList,OrderMeal.this.getActivity());
            recylermenu.setAdapter(restaurantAdapter);
            return true;
        }
        if (id == R.id.deliverycost) {
            Collections.sort(restaurantList, new Comparator<Restaurant>() {

                @Override
                public int compare(Restaurant r1, Restaurant r2) {

                    Double distance1 = getDistanceFromLatLonInKm(Double.parseDouble(OrderMeal.userlatitude),Double.parseDouble(userlongitude),Double.parseDouble(r1.getLatitude()),Double.parseDouble(r1.getLongitude()));
                    Double distance2 = getDistanceFromLatLonInKm(Double.parseDouble(OrderMeal.userlatitude),Double.parseDouble(userlongitude),Double.parseDouble(r2.getLatitude()),Double.parseDouble(r2.getLongitude()));
                    String dist1 = String.format("%.2f",distance1);
                    String dist2 = String.format("%.2f",distance2);

                    return (dist1).compareTo(dist2);
                }
            });

            restaurantAdapter = new RestaurantAdapter(restaurantList,OrderMeal.this.getActivity());
            recylermenu.setAdapter(restaurantAdapter);
            return true;
        }




        return super.onOptionsItemSelected(item);
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

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
             Intent i = new Intent(OrderMeal.this.getActivity(),Cart.class);
             startActivity(i);
        } else if (id == R.id.nav_orders) {
              Intent i = new Intent(OrderMeal.this.getActivity(),Orders.class);
             startActivity(i);

        } else if (id == R.id.nav_log_out) {
            //Intent i = new Intent(MainActivity.this,SignIn.class);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //startActivity(i);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
