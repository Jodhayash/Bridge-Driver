package com.example.bridge_driver;

import android.Manifest;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bridge_driver.Service.TrackingService;
import com.example.bridge_driver.Utils.SharedPrefHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import java.util.List;
import java.util.Locale;
import android.widget.Toast;


public class Home extends AppCompatActivity {

    public static String FRAG_IMAGE_DIALOG = "image_dialog";
    private static String DRIVER_NAME;
    private static String BUS_NO;
    private static String Trip;
    private static String TOKEN;
    private static int TRIP_TYPE;
    private static Home mInstance;
    // frag variables
    final FragmentManager fm = getFragmentManager();
    private ImageView imgDriver;
    private TextView txtDriver, txtTrip;
    private TextView txtBus;
    private TextView txtGpsLocation;
    private CoordinatorLayout coordinatorLayout;
    private SharedPrefHelper sharedPrefHelper;
    private ProgressDialog progressBar;
    FusedLocationProviderClient client;
    private LocationCallback locationCallback;
    double lat;
    double lon;

    public static Home getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPrefHelper = SharedPrefHelper.getInstance(this);
        mInstance = this;
        initViewsAndSet();
        client = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                lat = location.getLatitude();
                lon = location.getLongitude();
                updateGps(lat,lon);
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Location").child(BUS_NO);
                if (location != null) {
                    ref.setValue(location);
                }
                double sp = location.getSpeed();
                if(sp>80){
                    Toast.makeText(getApplicationContext(), "Speed Limit Crossed!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        startService();
        requestLocationUpdates();
        setProgressBar();
    }
    private void requestLocationUpdates() {
        LocationRequest request = LocationRequest.create();
        request.setInterval(10000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, locationCallback , Looper.getMainLooper());
        }
        Toast.makeText(getApplicationContext(), "Sharing Live Location", Toast.LENGTH_SHORT).show();
    }
    public void stopLocationUpdates() {
        client.removeLocationUpdates(locationCallback);
        Toast.makeText(getApplicationContext(), "Stopping Location Service", Toast.LENGTH_SHORT).show();
    }

    public void startService() {
        Intent serviceIntent = new Intent(this, TrackingService.class);
        serviceIntent.putExtra("Bus no", BUS_NO);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public void stopService() {
        Intent serviceIntent = new Intent(this,TrackingService.class);
        stopService(serviceIntent);
    }

    private void initViewsAndSet() {
        imgDriver = (ImageView) findViewById(R.id.imgDriver);
        txtBus = (TextView) findViewById(R.id.txtDriverBus);
        txtDriver = (TextView) findViewById(R.id.txtDriverName);
        txtTrip = (TextView) findViewById(R.id.txtTripd);
        txtGpsLocation = (TextView) findViewById(R.id.txtGpsLocation);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_home);

        DRIVER_NAME = getIntent().getStringExtra(SharedPrefHelper.LOGIN_NAME);
        BUS_NO = getIntent().getStringExtra(SharedPrefHelper.LOGIN_BUS_NO);
        TOKEN = getIntent().getStringExtra(SharedPrefHelper.LOGIN_TOKEN);
        Trip = getIntent().getStringExtra(SharedPrefHelper.LOGIN_TRIP_TYPE);
        if(Trip.equals("From School to Home"))
            TRIP_TYPE = 1;
        else TRIP_TYPE = 0;
        txtDriver.setText(DRIVER_NAME);
        txtBus.setText(BUS_NO);
        txtTrip.setText(Trip);
    }

    private void setProgressBar() {
        progressBar = new ProgressDialog(this);
        progressBar.setIndeterminate(true);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setCancelable(false);
        progressBar.setCanceledOnTouchOutside(false);
    }

    // handle pick up button click
    public void onPickClick(View v) {
        String st = "Pick";
        Intent i = new Intent(Home.this, Attend.class);
        i.putExtra("Bus no", BUS_NO);
        i.putExtra("Status", st);
        startActivity(i);
    }

    //handle drop button click
    public void onDropClick(View v) {
        String st = "Drop";
        Intent i = new Intent(Home.this, Attend.class);
        i.putExtra("Bus no", BUS_NO);
        i.putExtra("Status", st);
        startActivity(i);
    }

    //handle logout action
    public void doLogout(View v) {
        logoutAlert();
    }

    private void doLogout() {
        progressBar.show();
        progressBar.dismiss();
        Log.d("BridgeDriver_ok", "logged out");
        // and remove user token
        sharedPrefHelper.addString(SharedPrefHelper.LOGIN_TOKEN, null);
        finish();
    }

    private void logoutAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Confirm Logout...");
        alertDialog.setMessage("Make sure all kids are dropped and their dropping attendance is taken?");
        alertDialog.setPositiveButton("I am Sure", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                doLogout();
            }
        });
        alertDialog.setNegativeButton("Not Sure", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestLocationUpdates();
    }

    public void updateGps(final double lat, final double lon) {
        Home.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String loc_name = getLocationName(lat, lon);

                if (loc_name != null) {
                    txtGpsLocation.setText(loc_name);
                } else {
                    txtGpsLocation.setText("lat:" + lat + " , lon:" + lon);
                }
            }
        });
    }

    @Nullable
    private String getLocationName(double LATITUDE, double LONGITUDE) {
            String strAdd = "";
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");

                    for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }
                    strAdd = strReturnedAddress.toString();
                    Log.w("Current address", strReturnedAddress.toString());
                } else {
                    Log.w("Current address", "No Address returned!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.w("Current address", "Canont get Address!");
            }
            return strAdd;
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
        stopService();
    }

    @Override
    public void onBackPressed() {
        logoutAlert();
    }

}
