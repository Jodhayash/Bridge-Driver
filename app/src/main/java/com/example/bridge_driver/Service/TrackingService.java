package com.example.bridge_driver.Service;


import com.example.bridge_driver.Home;
import com.example.bridge_driver.Models.GpsRequest;
import com.example.bridge_driver.Utils.SharedPrefHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.app.PendingIntent;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.content.Intent;
import android.Manifest;
import android.location.Location;
import android.app.Notification;
import android.content.pm.PackageManager;
import android.app.Service;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.bridge_driver.R;

import static android.content.ContentValues.TAG;
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
        //stopSelf();
        return START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
