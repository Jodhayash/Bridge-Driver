package com.example.bridge_driver;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.example.bridge_driver.Interfaces.ApiInterface;
import com.example.bridge_driver.Service.TrackingService;
import com.example.bridge_driver.Models.GpsRequest;
import com.example.bridge_driver.Utils.SharedPrefHelper;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import android.widget.Toast;


public class Home extends AppCompatActivity {

    public static String FRAG_PICK_TAG = ApiInterface.QRY_PICKED;
    public static String FRAG_DROP_TAG = ApiInterface.QRY_DROPPED;
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
    private ApiInterface apiService;
    private ProgressDialog progressBar;
    double lati;
    double longi;

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
        startService();
        setProgressBar();

        //registerReceiver(Receiver, new IntentFilter("GET_SIGNAL_STRENGTH"));
        //updateGps(lat,log);
        txtGpsLocation.setText("lat:" + lati + " , lon:" + longi);
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
    /*private void startTrackerService() {
        Intent locationIntent = new Intent(this, TrackingService.class);
        locationIntent.putExtra("Bus no", BUS_NO);
        startService(locationIntent);
        Toast.makeText(this, "Sharing Live Location", Toast.LENGTH_SHORT).show();
    }*/

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
        /*final AtendDialogFrag dialogFrag = new AtendDialogFrag();
        //dialogFrag.show(fm,FRAG_PICK_TAG);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(FRAG_DROP_TAG);
        if (prev != null) {
            ft.remove(prev).commit();
        }
        dialogFrag.show(ft, FRAG_PICK_TAG);*/

    }

    //handle drop button click
    public void onDropClick(View v) {
        /*final AtendDialogFrag dialogFrag = new AtendDialogFrag();
        //dialogFrag.show(fm,FRAG_DROP_TAG);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag(FRAG_PICK_TAG);
        if (prev != null) {
            ft.remove(prev).commit();
        }
        dialogFrag.show(ft, FRAG_DROP_TAG);*/

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
        stopService();
        finish();
    }

    private void logoutAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);

        // Setting Dialog Title
        alertDialog.setTitle("Confirm Logout...");

        // Setting Dialog Message
        alertDialog.setMessage("Make sure all kids are dropped and their dropping attendance is taken?");
        ;

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("I am Sure", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                doLogout();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("Not Sure", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // DON'T do this,as it stops sending gps data in background
        // STOP-SERVICE only when driver has logged-out
        // stopService(locationIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        startService();
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
    private String getLocationName(double lat, double lon) {
        Geocoder g = new Geocoder(this);
        try {
            Address address= g.getFromLocation(lat, lon, 1).get(0);
            String address_line="";
            int max_address=address.getMaxAddressLineIndex();
            for(int i=0;i<max_address;i++){

                address_line+=address.getAddressLine(i)+" ";
            }
            Log.d("bts_ok","full address:"+address_line);
            return address_line;
        } catch (IOException | IndexOutOfBoundsException e) {
            Log.d("bts_error", "cant get location name " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService();
    }

}
