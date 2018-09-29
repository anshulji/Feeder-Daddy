package com.dev.fd.feederdaddy;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class ProfileActivity extends AppCompatActivity {

    private MaterialEditText edtroomnumber, edthostelname, edtlandmark,edtusername,edtphonenumber;

    Place shippingAddress;
    PlaceAutocompleteFragment edtAddress;
    ImageView imggoback;

    private FButton  btnupdate;
    private CheckBox checkBox;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private String addressassigned="",location="";
    private int btntype = 0, tm = 1, flag = 0, fg = 0;
    String phoneNumber, phoneno, username;

    private static final String TAG = "ProfileActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private boolean mLocationPermissionsGranted=false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private FusedLocationProviderClient mFusedLocationproviderClient;
    private double latitude=0,longitude=0;
    private Geocoder geocoder;
    protected LocationManager locationManager;
    private String address,city;
    private boolean isCheckBoxTicked=true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        city = sharedPreferences.getString("city","N/A");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        imggoback = findViewById(R.id.imggoback);
        imggoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        edtroomnumber = findViewById(R.id.edtroomnumber);
        checkBox = findViewById(R.id.checkbox);
        btnupdate = findViewById(R.id.btnupdate);
        edtphonenumber = findViewById(R.id.edtphonenumber);
        edtusername = findViewById(R.id.edtusername);
        edthostelname = findViewById(R.id.edthostelname);
        edtlandmark = findViewById(R.id.edtlandmark);
        edtAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //hide search icon before fragment
        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        //set hint for autocomplete edit text
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter your Location");
        //set text size
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(18);

        // get address from autocomplete
        edtAddress.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                shippingAddress = place;
                addressassigned="";
            }
            @Override
            public void onError(Status status) {
                Log.e("Error",status.getStatusMessage() );
            }
        });

        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        //get old data from shared preferences and set on edit text views
        sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
        String strphone = sharedPreferences.getString("phone","N/A");
        String strusername = sharedPreferences.getString("name","N/A");
        String straddress = sharedPreferences.getString("address","N/A");
        String strroomnumber = sharedPreferences.getString("roomnumber","N/A");
        String strhostelname = sharedPreferences.getString("hostelname","N/A");
        String strlandmark = sharedPreferences.getString("landmark","N/A");
        String strlocation = sharedPreferences.getString("location","N/A");
        String strlatitude = sharedPreferences.getString("latitude","N/A");
        String strlongitude = sharedPreferences.getString("longitude","N/A");

        edtusername.setText(strusername);
        edtphonenumber.setText(strphone);
        edtroomnumber.setText(strroomnumber);
        edthostelname.setText(strhostelname);
        edtlandmark.setText(strlandmark);
        edtAddress.setText(strlocation);
        phoneno = strphone;
        username = strusername;
        addressassigned=strlocation;
        latitude = Double.valueOf(strlatitude);
        longitude = Double.valueOf(strlongitude);

        btnupdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtroomnumber.getText().toString().equals(""))
                    Toast.makeText(ProfileActivity.this, "Please enter Flat/House/Room No.", Toast.LENGTH_SHORT).show();
                else if (edthostelname.getText().toString().equals(""))
                    Toast.makeText(ProfileActivity.this, "Please enter Street/Society/Hostel Name", Toast.LENGTH_SHORT).show();
                else if (edtlandmark.getText().toString().equals(""))
                    Toast.makeText(ProfileActivity.this, "Please enter Landmark", Toast.LENGTH_SHORT).show();
                    //else if (currentAddtv.getText().toString().equals(""))
                    //  Toast.makeText(SignUpActivity.this, "Please enter Locality", Toast.LENGTH_SHORT).show();
                else if (shippingAddress==null && addressassigned.equals(""))
                    Toast.makeText(ProfileActivity.this, "Please enter Locality", Toast.LENGTH_SHORT).show();
                else {
                    // address = edtroomnumber.getText().toString() + ", " + edthostelname.getText().toString() + ", " +
                    //      edtlandmark.getText().toString() + ", " + currentAddtv.getText().toString();
                    if(addressassigned.equals("")) {
                        address = edtroomnumber.getText().toString() + ", " + edthostelname.getText().toString() + ", " +
                                edtlandmark.getText().toString() + ", " + shippingAddress.getAddress().toString();
                        LatLng ll=shippingAddress.getLatLng();
                        latitude=ll.latitude;
                        longitude=ll.longitude;
                        location = shippingAddress.getAddress().toString();

                    }
                    else {
                        address = edtroomnumber.getText().toString() + ", " + edthostelname.getText().toString() + ", " +
                                edtlandmark.getText().toString() + ", " + addressassigned;
                        location = addressassigned;
                    }
                    Toast.makeText(ProfileActivity.this, "Updating...", Toast.LENGTH_SHORT).show();

                    firebaseDatabase = FirebaseDatabase.getInstance();
                    databaseReference = firebaseDatabase.getReference(city).child("Users").child(phoneno);
                    databaseReference.child("userphone").setValue(phoneno);
                    databaseReference.child("username").setValue(edtusername.getText().toString());
                    databaseReference.child("useraddress").setValue(address);
                    databaseReference.child("userlatitude").setValue(String.valueOf(latitude));
                    databaseReference.child("userlongitude").setValue(String.valueOf(longitude));

                    SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("phone", phoneno);
                    if(latitude!=0)
                        editor.putString("latitude",String.valueOf(latitude));
                    if(longitude!=0)
                        editor.putString("longitude",String.valueOf(longitude));
                    editor.putString("address",address);
                    editor.putString("roomnumber",edtroomnumber.getText().toString());
                    editor.putString("hostelname",edthostelname.getText().toString());
                    editor.putString("landmark",edtlandmark.getText().toString());
                    editor.putString("location",location);
                    editor.putString("name", edtusername.getText().toString());
                    //editor.putString("address",address);
                    editor.commit();

                    //getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                    //      .commit();

                    Toast.makeText(ProfileActivity.this, "Updated Successfully !", Toast.LENGTH_SHORT).show();
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null) {
                        String strgoback = bundle.getString("comeback");
                        if(strgoback.equals("yes"))
                        {Intent returnIntent = new Intent();
                            returnIntent.putExtra("result","1");
                            setResult(Activity.RESULT_OK,returnIntent);
                        }
                        //databaseReference = firebaseDatabase.getReference("category").child(category);
                    }
                }
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkBox.isChecked()) {
                    edtAddress.setText("");
                    addressassigned="";
                    // currentAddtv.setText("");
                } else {

                    if(mLocationPermissionsGranted) {
                        boolean isGPSEnabled = false;
                        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        if(isGPSEnabled) {
                            getDevicelocation();
                        }
                        else
                        {enableGPS();
                            getDevicelocation();
                        }
                    }
                    else
                    { getLocationPermission();
                        enableGPS();
                        getDevicelocation();
                    }

                    if(latitude==0)
                        checkBox.setChecked(false);

                }
            }
        });


    }
    private void getDevicelocation(){
        Log.d(TAG, "getDevicelocation: getting the device's current location");

        mFusedLocationproviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){
                Task location = mFusedLocationproviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: Found Location");
                            Location currentLocation = (Location) task.getResult();

                            if(currentLocation!=null) {
                                latitude = currentLocation.getLatitude();
                                longitude = currentLocation.getLongitude();


                                // Toast.makeText(SignUpActivity.this ,"address:"+latitude+" "+longitude,Toast.LENGTH_SHORT).show();

                                List<Address> addresses = new ArrayList<>();
                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String state = addresses.get(0).getAdminArea();
                                String country = addresses.get(0).getCountryName();
                                String postalCode = addresses.get(0).getPostalCode();
                                String knownName = addresses.get(0).getFeatureName();
                                for (int i = 0; i < address.length(); i++) {
                                    if (address.substring(i, i + 5).equals("India")) {
                                        address = address.substring(0, i + 5);
                                        break;
                                    }
                                }
                                edtAddress.setText(address);
                                addressassigned=address;
                                //currentAddtv.setText(address);
                                checkBox.setChecked(true);
                            }


                        }else{
                            Log.d(TAG, "onComplete: Current location is null");
                            Toast.makeText(ProfileActivity.this, "Unable to detect your location !", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

        }catch (SecurityException e){
            Log.e(TAG, "getDevicelocation: security exception"+ e.getMessage() );

        }
    }

    private void getLocationPermission() {
        String permissions[] = {FINE_LOCATION,COURSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)== PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COURSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionsGranted=true;
            }else {
                ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else {
            ActivityCompat.requestPermissions(this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted=false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length>0)
                { for(int i=0;i<grantResults.length;i++)
                {
                    if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                        mLocationPermissionsGranted=false;
                        return;
                    }
                }
                    mLocationPermissionsGranted=true;
                }
            }
        }
    }

    //-----------------------------------------location------------------------------//

    private void init() {
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: chicking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ProfileActivity.this);
        if(available== ConnectionResult.SUCCESS)
        {  //everything is fine and user can make map request
            Log.d(TAG, "isServicesOK: google play services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {//an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ProfileActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(this, "You Can't make map requests !", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void enableGPS() {

        boolean isGPSEnabled = false;
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnabled) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfileActivity.this,R.style.MyDialogTheme);
            alertDialogBuilder.setTitle("Turn On GPS");
            alertDialogBuilder.setMessage("Turn on your GPS, to detect your location.");
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            alertDialogBuilder.show();
        }
    }

    @Override
    public void onBackPressed() {
       finish();
    }
}
