package com.dev.fd.feederdaddy;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

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

    String MenuId="",RestaurantId="";

    FirebaseRecyclerAdapter<Food,FoodViewHolder> adapter;

    //search food adapter
    FirebaseRecyclerAdapter<Food,FoodViewHolder> searchAdapter;
    List<String> SuggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //init firebase
        database = FirebaseDatabase.getInstance();


        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);

        layoutManager =new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //getting categoryid passed by intent here
        if(getIntent()!=null) {
            MenuId = getIntent().getStringExtra("MenuId");
            RestaurantId = getIntent().getStringExtra("RestaurantId");
            foodlist = database.getReference("Foods").child(RestaurantId).child(MenuId);

        }
        if(!MenuId.isEmpty() && MenuId!=null){

            loadfoodlist(MenuId);
        }

        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter Food Name");
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

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodlist.orderByChild("name").equalTo(text.toString())
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override

                    public void onClick(View view, int position, boolean isLongClick) {
                        // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();

                        //start food details activity
                        Intent fooddetail = new Intent(FoodListActivity.this,FoodDetails.class);
                        fooddetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        fooddetail.putExtra("RestaurantId",RestaurantId);
                        fooddetail.putExtra("MenuId", MenuId);
                        startActivity(fooddetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);

    }



    private void loadSuggest() {
        foodlist.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnaphot : dataSnapshot.getChildren())
                        {
                            Food item = postSnaphot.getValue(Food.class);
                            SuggestList.add(item.getName());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void loadfoodlist(String categoryId) {

        adapter =new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,R.layout.food_item,FoodViewHolder.class,
                foodlist // find that foods with menuid==categoryid
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                final Food local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        // Toast.makeText(FoodList.this, ""+local.getName(), Toast.LENGTH_SHORT).show();

                        //start food details activity
                        Intent fooddetail = new Intent(FoodListActivity.this,FoodDetails.class);
                        fooddetail.putExtra("FoodId",adapter.getRef(position).getKey());
                        fooddetail.putExtra("RestaurantId",RestaurantId);
                        fooddetail.putExtra("MenuId", MenuId);
                        startActivity(fooddetail);
                    }
                });


            }
        };

        recyclerView.setAdapter(adapter);
    }
}
