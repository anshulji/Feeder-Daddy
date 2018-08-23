package com.dev.fd.feederdaddy;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.signin.SignIn;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        bottomNavigationView = findViewById(R.id.bottomnavigationview);
        //bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.confectionaries:
                        getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new OrderMeal()).commit();
                        return true;
                    case R.id.eventcatering:
                        return true;
                    case R.id.ordermeal:
                        getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new OrderMeal()).commit();
                        return true;
                    case R.id.orders:
                        return true;

                }
                return false;
            }
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new OrderMeal()).commit();
    }
}
