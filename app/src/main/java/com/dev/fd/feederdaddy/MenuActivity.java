package com.dev.fd.feederdaddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.dev.fd.feederdaddy.Interface.ItemClickListener;
import com.dev.fd.feederdaddy.ViewHolder.MenuViewHolder;
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

public class MenuActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference menulist;

    String RestaurantId="";

    FirebaseRecyclerAdapter<Menu,MenuViewHolder> adapter;

    //search menu adapter
    FirebaseRecyclerAdapter<Menu,MenuViewHolder> searchAdapter;
    List<String> SuggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //init firebase
        database = FirebaseDatabase.getInstance();

        recyclerView = findViewById(R.id.recycler_menulist);
        recyclerView.setHasFixedSize(true);

        layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent i = new Intent(MenuActivity.this,Cart.class);
                startActivity(i) ;

            }
        });


        //getting restaurantid passed by intent here
        if(getIntent()!=null) {
            RestaurantId = getIntent().getStringExtra("RestaurantId");
            menulist = database.getReference("Menus").child(RestaurantId);

        }
        if(!RestaurantId.isEmpty() && RestaurantId!=null){

            loadmenulist(RestaurantId);
        }

        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter Menu Item Name");
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


    }

    private void startSearch(CharSequence text) {

        searchAdapter = new FirebaseRecyclerAdapter<Menu, MenuViewHolder>(
                Menu.class,
                R.layout.menu_item,
                MenuViewHolder.class,
                menulist.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Menu model, int position) {
                viewHolder.txtmenuname.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgmenuitem);

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
                        for(DataSnapshot postSnaphot : dataSnapshot.getChildren())
                        {
                            Menu item = postSnaphot.getValue(Menu.class);
                            SuggestList.add(item.getName());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadmenulist(String categoryId) {

        adapter =new FirebaseRecyclerAdapter<Menu,MenuViewHolder>(Menu.class,R.layout.menu_item,MenuViewHolder.class,
                menulist// find that foods with menuid==categoryid
        ) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Menu model, int position) {
                viewHolder.txtmenuname.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imgmenuitem);

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

        recyclerView.setAdapter(adapter);
    }





}
