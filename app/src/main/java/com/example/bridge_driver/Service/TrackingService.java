package com.example.bridge_driver.Service;


import com.example.bridge_driver.Home;
import com.example.bridge_driver.Models.GpsRequest;
import com.example.bridge_driver.Utils.SharedPrefHelper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.PendingIntent;
import android.os.IBinder;
import android.content.Intent;
import android.Manifest;
import android.location.Location;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.app.Service;
import android.util.Log;
import android.widget.Toast;

import com.example.bridge_driver.R;

import static com.example.bridge_driver.App.CHANNEL_ID;


public class TrackingService extends Service {
    private double Lati;
    private double Longi;
    private static String BUS_NO;



    private static final String TAG = TrackingService.class.getSimpleName();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //buildNotification();
        //requestLocationUpdates();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        BUS_NO = intent.getStringExtra("Bus no");

        Intent notificationIntent = new Intent(this, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))
                .setSmallIcon((R.drawable.tracking_enabled))
                .setContentIntent(pendingIntent)
                .build();

        startForeground(1, notification);
        requestLocationUpdates();
        //stopSelf();
        return START_NOT_STICKY;
    }


//Create the persistent notification //

   /* private void buildNotification() {
// Create the persistent notification//
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_enabled_notif))

//Make this notification ongoing so it can’t be dismissed by the user//

                .setOngoing(true)
                //.setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.tracking_enabled);
        startForeground(1, builder.build());
    }*/

   /* protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

//Unregister the BroadcastReceiver when the notification is tapped//

            unregisterReceiver(stopReceiver);

//Stop the Service//

            stopSelf();
        }
    };*/

    private void requestLocationUpdates() {
        LocationRequest request = new LocationRequest();
//Specify how often your app should request the device’s location//
        request.setInterval(10000);
//Get the most accurate location data available//
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permission == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {


                    Location location = locationResult.getLastLocation();
                    double lat = 5.0;//location.getLatitude();
                    double lon = 5.0;//location.getLongitude();
                    Lati=lat;
                    Longi = lon;
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Location").child(BUS_NO);
                    if(location != null) {
                        ref.setValue(location);
                    }
                }
            }, null);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        Toast.makeText(getApplicationContext(), "Location services are down", Toast.LENGTH_LONG).show();
    }

   /* public static double getLati(){
        GpsRequest gp =new GpsRequest();
        double l1 = gp.getLat();
        return l1;
    }
    public static double getLongi(){
        GpsRequest gp =new GpsRequest();
        double l2 = gp.getLon();
        return l2;
    }*/
}
