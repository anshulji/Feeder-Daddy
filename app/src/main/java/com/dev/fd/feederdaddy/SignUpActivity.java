package com.dev.fd.feederdaddy;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dev.fd.feederdaddy.Common.Common;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import android.Manifest;

public class SignUpActivity extends AppCompatActivity {




    private EditText edtverificationcode, edtroomnumber, edthostelname, edtlandmark, currentAddtv;

    Place shippingAddress;
    PlaceAutocompleteFragment edtAddress;

    private Spinner cityspinner,zonespinner;
    private TextView txtresendotp, txtresendotptimer;
    private Button btnverifyotp, btnsignin;
    private CheckBox checkBox;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference,zoneref;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId,addressassigned="",locationstr;
    private int btntype = 0, tm = 1, flag = 0, fg = 0;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    String phoneNumber, phoneno, username;
    ScrollView rlverifyotp, rlenteraddress;

    private static final String TAG = "SignUpActitvity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private boolean mLocationPermissionsGranted=false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    private FusedLocationProviderClient mFusedLocationproviderClient;
    private double latitude=0,longitude=0;
    private Geocoder geocoder;
    protected LocationManager locationManager;
    private String address;
    private String city,zone;
    private boolean isCheckBoxTicked=true;

    GpsLocationTracker mGpsLocationTracker;

    LocationManager mLocationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Cities");

        cityspinner = findViewById(R.id.spinner);
        zonespinner = findViewById(R.id.spinnerzone);

        edtverificationcode = findViewById(R.id.edtverificationcode);
        txtresendotp = findViewById(R.id.txtresendotp);
        btnverifyotp = findViewById(R.id.btnverifyotp);
        txtresendotptimer = findViewById(R.id.txtresendotptimer);
        rlverifyotp = findViewById(R.id.verifyotplayout);
        rlenteraddress = findViewById(R.id.rlddress);
        edthostelname = findViewById(R.id.edthostelname);
        edtlandmark = findViewById(R.id.edtlandmark);


// Spinner Drop down elements
        final List<String> categories = new ArrayList<String>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null) {

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        categories.add(postSnapshot.getValue().toString());
                    }
                    // Creating adapter for spinner
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, categories);

                    // Drop down layout style - list view with radio button
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // attaching data adapter to spinner
                    cityspinner.setAdapter(dataAdapter);

                    cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            city = parent.getItemAtPosition(position).toString();

                            zoneref = firebaseDatabase.getReference(city).child("Zones");
                            final List<String> zoneslist = new ArrayList<String>();
                            zoneref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.getValue()!=null) {

                                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                            zoneslist.add(postSnapshot.getValue().toString());
                                        }

                                        // Creating adapter for spinner
                                        ArrayAdapter<String> zoneadapter = new ArrayAdapter<String>(SignUpActivity.this, android.R.layout.simple_spinner_item, zoneslist);

                                        // Drop down layout style - list view with radio button
                                        zoneadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                        // attaching data adapter to spinner
                                        zonespinner.setAdapter(zoneadapter);
                                        zonespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                zone = parent.getItemAtPosition(position).toString();
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {

                                            }
                                        });
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            city = "Varanasi";
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        // currentAddtv = findViewById(R.id.edtlocality);
         edtAddress = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        //hide search icon before fragment
        edtAddress.getView().findViewById(R.id.place_autocomplete_search_button).setVisibility(View.GONE);
        //set hint for autocomplete edit text
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setHint("Enter your Location");
        //set text size
        ((EditText)edtAddress.getView().findViewById(R.id.place_autocomplete_search_input)).setTextSize(14);

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

        edtroomnumber = findViewById(R.id.edtroomnumber);
        btnsignin = findViewById(R.id.btnsignin);
        checkBox = findViewById(R.id.checkbox);

        geocoder = new Geocoder(this, Locale.getDefault());
        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        rlverifyotp.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            phoneno = bundle.getString("phone");
            username = bundle.getString("username");
            //databaseReference = firebaseDatabase.getReference("category").child(category);
        }

        btnverifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtverificationcode.getText().length() == 0) {
                    Toast.makeText(SignUpActivity.this, "Please enter 6-digit otp sent on your Phone No.", Toast.LENGTH_SHORT).show();
                } else {
                    //b1.setEnabled(false);
                    Toast.makeText(SignUpActivity.this, "Verifying...", Toast.LENGTH_SHORT).show();
                    tm = 0;
                    String verificationCode = edtverificationcode.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });


        txtresendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtresendotp.setEnabled(false);
                txtresendotp.getPaint().setUnderlineText(false);
                phoneNumber = "+91" + phoneno;
                startOTPTimer();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber, 30, TimeUnit.SECONDS, SignUpActivity.this,
                        mCallbacks
                );
            }
        });


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (tm != 0) {
                    Toast.makeText(SignUpActivity.this, "No Internet Connection!", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onVerificationFailed: ",e );
                }

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                btntype = 1;
                Toast.makeText(SignUpActivity.this, "Verification Code Sent !", Toast.LENGTH_SHORT).show();
                btnverifyotp.setEnabled(true);
            }
        };

        if (flag == 0) {
            txtresendotp.setEnabled(false);
            phoneNumber = "+91" + phoneno;
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber, 30, TimeUnit.SECONDS, SignUpActivity.this,
                    mCallbacks
            );
            flag = 1;
        }

        if (fg == 0) {
            fg = 1;
            startOTPTimer();
        }

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Common.isConnectedToInternet(getBaseContext())) {
                    if (edtroomnumber.getText().toString().equals(""))
                        Toast.makeText(SignUpActivity.this, "Please enter Flat/House/Room No.", Toast.LENGTH_SHORT).show();
                    else if (edthostelname.getText().toString().equals(""))
                        Toast.makeText(SignUpActivity.this, "Please enter Street/Society/Hostel Name", Toast.LENGTH_SHORT).show();
                    else if (edtlandmark.getText().toString().equals(""))
                        Toast.makeText(SignUpActivity.this, "Please enter Landmark", Toast.LENGTH_SHORT).show();
                        //else if (currentAddtv.getText().toString().equals(""))
                        //  Toast.makeText(SignUpActivity.this, "Please enter Locality", Toast.LENGTH_SHORT).show();
                    else if (shippingAddress==null && addressassigned.equals(""))
                        Toast.makeText(SignUpActivity.this, "Please enter Locality", Toast.LENGTH_SHORT).show();
                    else {
                        // address = edtroomnumber.getText().toString() + ", " + edthostelname.getText().toString() + ", " +
                        //      edtlandmark.getText().toString() + ", " + currentAddtv.getText().toString();
                        if(addressassigned.equals("")) {
                            address = edtroomnumber.getText().toString() + ", " + edthostelname.getText().toString() + ", " +
                                    edtlandmark.getText().toString() + ", " + shippingAddress.getAddress().toString();
                            LatLng ll=shippingAddress.getLatLng();
                            latitude=ll.latitude;
                            longitude=ll.longitude;
                            locationstr = shippingAddress.getAddress().toString();
                        }
                        else {
                            address = edtroomnumber.getText().toString() + ", " + edthostelname.getText().toString() + ", " +
                                    edtlandmark.getText().toString() + ", " + addressassigned;
                            locationstr = addressassigned;
                        }
                        Toast.makeText(SignUpActivity.this, "Signing in...", Toast.LENGTH_SHORT).show();

                        databaseReference = firebaseDatabase.getReference(city).child("Users").child(phoneno);
                        databaseReference.child("userphone").setValue(phoneno);
                        databaseReference.child("username").setValue(username);
                        databaseReference.child("useraddress").setValue(address);
                        databaseReference.child("userlatitude").setValue(String.valueOf(latitude));
                        databaseReference.child("userlongitude").setValue(String.valueOf(longitude));
                        databaseReference.child("userzone").setValue(zone);
                        databaseReference.child("usercity").setValue(city);

                        SharedPreferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("phone", phoneno);
                        if(latitude!=0)
                            editor.putString("latitude",String.valueOf(latitude));
                        if(longitude!=0)
                            editor.putString("longitude",String.valueOf(longitude));
                        editor.putString("address",address);
                        editor.putString("city",city);
                        editor.putString("zone",zone);
                        editor.putString("roomnumber",edtroomnumber.getText().toString());
                        editor.putString("hostelname",edthostelname.getText().toString());
                        editor.putString("landmark",edtlandmark.getText().toString());
                        editor.putString("location",locationstr);
                        editor.putString("name", username);
                        //editor.putString("address",address);
                        editor.commit();

                        //getFragmentManager().beginTransaction().remove(getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment))
                        //      .commit();

                        Toast.makeText(SignUpActivity.this, "Signed In Successfully !", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                else
                    Toast.makeText(getBaseContext(), "No Internet Connection !", Toast.LENGTH_SHORT).show();


            }
        });

      /*checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (!isChecked) {
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
                            checkBox.setChecked(false);
                        }
                    }
                    else
                    { getLocationPermission();
                        checkBox.setChecked(false);
                    }


                }
            }
        });*/

        mGpsLocationTracker = new GpsLocationTracker(SignUpActivity.this);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    /**
                     * Set GPS Location fetched address
                     */
                    if (!isChecked) {
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
                                checkBox.setChecked(false);
                            }
                        }
                        else
                        { getLocationPermission();
                            checkBox.setChecked(false);
                        }
                    }

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


                                 //Toast.makeText(SignUpActivity.this ,"address:"+latitude+" "+longitude,Toast.LENGTH_SHORT).show();

                                List<Address> addresses = new ArrayList<>();
                                try {
                                    addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                String address = addresses.get(0).getAddressLine(0);
                                for (int i = 0; i < address.length(); i++) {
                                    if (address.substring(i, i + 5).equals("India")) {
                                        address = address.substring(0, i + 5);
                                        break;
                                    }
                                }
                                edtAddress.setText(address);
                                addressassigned=address;
                                //currentAddtv.setText(address);
                            }
                            else
                            {
                                Toast.makeText(SignUpActivity.this, "Unable to detect your location !", Toast.LENGTH_SHORT).show();
                                checkBox.setChecked(false);
                            }

                        }else{
                            Log.d(TAG, "onComplete: Current location is null");
                            Toast.makeText(SignUpActivity.this, "Unable to detect your location !", Toast.LENGTH_SHORT).show();
                            checkBox.setChecked(false);
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

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
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
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(SignUpActivity.this);
        if(available== ConnectionResult.SUCCESS)
        {  //everything is fine and user can make map request
            Log.d(TAG, "isServicesOK: google play services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available))
        {//an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(SignUpActivity.this,available,ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else
        {
            Toast.makeText(this, "You Can't make map requests !", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private void startOTPTimer() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                txtresendotptimer.setText("" + millisUntilFinished / 1000 + " s");
            }

            public void onFinish() {
                txtresendotptimer.setText("");
                txtresendotp.setEnabled(true);
                txtresendotp.getPaint().setUnderlineText(true);
            }
        }.start();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Verification Success !", Toast.LENGTH_SHORT).show();

                            /*
                            Preferences sharedPreferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("phone", phoneno);
                            editor.putString("name", username);
                            //editor.putString("address",address);
                            editor.commit();*/

                            rlverifyotp.setVisibility(View.GONE);
                            rlenteraddress.setVisibility(View.VISIBLE);

                            if(isServicesOK())
                            {   /*getLocationPermission();
                                enableGPS();
                                getDevicelocation();*/
                            }

                        } else {
                            Toast.makeText(SignUpActivity.this, "There was some error !", Toast.LENGTH_SHORT).show();
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(SignUpActivity.this, "Invalid Code !", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
    }


    private void enableGPS() {

        boolean isGPSEnabled = false;
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSEnabled) {
            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SignUpActivity.this,R.style.MyDialogTheme);
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


}
