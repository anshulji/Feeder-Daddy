package com.dev.fd.feederdaddy;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.dev.fd.feederdaddy.Common.Common;
import com.dev.fd.feederdaddy.Database.Database;
import com.dev.fd.feederdaddy.Service.ListenOrder;
import com.dev.fd.feederdaddy.model.Token;
import com.google.android.gms.signin.SignIn;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    public BottomNavigationView bottomNavigationView;
    public int height;
    String AppVersion="1.0";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if(getIntent().getStringExtra("FCM")!=null)
        {
            Intent intent = new Intent(MainActivity.this,Orders.class);
            startActivity(intent);
        }

        //init firebase
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference  = firebaseDatabase.getReference("AppVersion");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!AppVersion.equals(dataSnapshot.getValue().toString()))
                {
                     AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
                    alertDialogBuilder.setTitle("Update Your App");
                    alertDialogBuilder.setMessage("A newer version of this app is available on Playstore. Please update this app for further use.");
                    alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.dev.anshul.brainbaazianswerscheatcodes")));
                        }
                    });

                    alertDialogBuilder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                    alertDialogBuilder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        bottomNavigationView = findViewById(R.id.bottomnavigationview);


        //bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.bakery:
                        Common.currentfragment = "bakery";
                        getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new OrderMeal()).commit();
                        return true;
                    case R.id.hotdeals:
                        getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity, new HotDeals()).commit();
                        return true;
                    case R.id.nightorders:
                        getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity, new NightOrdersActivity()).commit();
                        return true;
                    case R.id.ordermeal:
                        Common.currentfragment="ordermeal";
                        getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new OrderMeal()).commit();
                        return true;



                }
                return false;
            }
        });

        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if(hour==23 || hour<3)
        {
            bottomNavigationView.setSelectedItemId(R.id.nightorders);
            getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new NightOrdersActivity()).commit();
        }
        else
        getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new OrderMeal()).commit();



        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationViewBehavior());

        updateToken(FirebaseInstanceId.getInstance().getToken());


    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone","N/A");
        String city = sharedPreferences.getString("city","N/A");
        String zone = sharedPreferences.getString("zone","N/A");


        Token data = new Token(token,city+zone+"0");
        tokens.child(phone).setValue(data);
    }

    public void refreshordermeal()
    {
        if(Common.currentfragment.equals("ordermeal"))
        {
            bottomNavigationView.setSelectedItemId(R.id.ordermeal);
            getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new OrderMeal()).commit();
        }
        else
        {
            bottomNavigationView.setSelectedItemId(R.id.bakery);
            getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new OrderMeal()).commit();
        }
    }

    public void refreshhotdeals()
    {
        bottomNavigationView.setSelectedItemId(R.id.hotdeals);
        getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new HotDeals()).commit();
    }

    public void refreshnightorders()
    {
        bottomNavigationView.setSelectedItemId(R.id.nightorders);
        getSupportFragmentManager().beginTransaction().replace(R.id.contentofmainactivity,new NightOrdersActivity()).commit();
    }

    public void hidebottomnavigationbar()
    {  //bottomNavigationView.clearAnimation();
       // bottomNavigationView.animate().translationY(0).setDuration(200);
        //bottomNavigationView.setTranslationY(0);
       bottomNavigationView.setVisibility(View.GONE);
    }
    public void showbottomnavigationbar()
    {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public class BottomNavigationViewBehavior extends CoordinatorLayout.Behavior<BottomNavigationView> {


        @Override
        public boolean onLayoutChild(CoordinatorLayout parent, BottomNavigationView child, int layoutDirection) {
            height = child.getHeight();
            return super.onLayoutChild(parent, child, layoutDirection);
        }

        @Override
        public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
                                           BottomNavigationView child, @NonNull
                                                   View directTargetChild, @NonNull View target,
                                           int axes, int type)
        {
            return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
        }

        @Override
        public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull BottomNavigationView child,
                                   @NonNull View target, int dxConsumed, int dyConsumed,
                                   int dxUnconsumed, int dyUnconsumed,
                                   @ViewCompat.NestedScrollType int type)
        {
            if (dyConsumed > 0) {
                slideDown(child);
            } else if (dyConsumed < 0) {
                slideUp(child);
            }
        }

        private void slideUp(BottomNavigationView child) {
            child.clearAnimation();
            child.animate().translationY(0).setDuration(200);
        }

        private void slideDown(BottomNavigationView child) {
            child.clearAnimation();
            child.animate().translationY(height).setDuration(200);
        }
    }
}
