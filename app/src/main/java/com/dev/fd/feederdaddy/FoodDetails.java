package com.dev.fd.feederdaddy;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.R;
import com.dev.fd.feederdaddy.model.Food;
import com.dev.fd.feederdaddy.model.Menu;
import com.dev.fd.feederdaddy.model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetails extends AppCompatActivity {

    TextView food_name, food_price, food_description;
    ImageView food_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String FoodId="",RestaurantId="",MenuId="";

    Food currentFood;

    FirebaseDatabase database;
    DatabaseReference foods;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);

        database=database.getInstance();
        //get food id
        if(getIntent()!=null)
        {
            FoodId=getIntent().getStringExtra("FoodId");
            RestaurantId=getIntent().getStringExtra("RestaurantId");
            MenuId=getIntent().getStringExtra("MenuId");
            foods=database.getReference("Foods").child(RestaurantId).child(MenuId).child(FoodId);


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

                new Database(getBaseContext()).addToCart(new Order(
                        FoodId,
                        currentFood.getName(),
                        numberButton.getNumber(),
                        currentFood.getPrice(),
                        "0"
                ));

                Toast.makeText(FoodDetails.this, "Added To Cart", Toast.LENGTH_SHORT).show();

            }
        });

        food_description=findViewById(R.id.food_description);
        food_name = findViewById(R.id.food_name);
        food_price=findViewById(R.id.food_price);
        food_image= findViewById(R.id.img_food);

        collapsingToolbarLayout= findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);





    }

    private void getDetailFood(String foodId) {

        foods.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentFood = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(currentFood.getImage()).into(food_image);

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
