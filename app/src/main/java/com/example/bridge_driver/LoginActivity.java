package com.example.bridge_driver;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.example.bridge_driver.Models.DriverLoginModel;
import com.example.bridge_driver.Utils.SharedPrefHelper;
import com.example.bridge_driver.Utils.NetworkHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class LoginActivity extends AppCompatActivity {

    Button lgin;
    EditText bno, password;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CheckBox mTripTypeCheckBox;
    private static final int PERMISSION_FINE_LOCATION = 101;
    private volatile Boolean gpsError = Boolean.TRUE;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Location").child("5677");
    //private SharedPrefHelper sharedPrefHelper;
   // private ApiInterface apiService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //lLayout = findViewById(R.id.ll);
        bno = findViewById(R.id.Busno);
        password = findViewById(R.id.dpass);
        lgin = findViewById(R.id.btn_login);
        mTripTypeCheckBox = (CheckBox) findViewById(R.id.checkSchoolToHome);

        if (!checkPermission()) {
            gpsError = Boolean.TRUE;
            requestPermission();

        } else {
            gpsError = Boolean.FALSE;
        }

    }
    private Boolean checkPermission() {
        int perm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return (perm == PackageManager.PERMISSION_GRANTED);
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);
    }
    private Boolean isGpsOn() {
        LocationManager l = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!l.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable GPS", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            // result of this action can be rechecked in onResume
        }
        return true;
    }

    private int tripType() {
        // 1: trip from school to home
        //0: from home to school
        return mTripTypeCheckBox.isChecked() ? DriverLoginModel.JOURNEY_FROM_SCHOOL_TO_HOME : DriverLoginModel.JOURNEY_FROM_HOME_TO_SCHOOL;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission granted
                    //force on gps
                    if (isGpsOn()) {
                        gpsError = false;
                        //1st entry point of application
                    }

                } else {
                    //permission rejected
                    gpsError = false;
                    finish();
                }
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isGpsOn()) {
            gpsError = false;
            //2nd entry point
        } else {
            gpsError = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void doLogin(View v) {

        final String Trip;
        final String no = bno.getText().toString().trim();
        final String pass = password.getText().toString().trim();
        int flag = 0;
        flag = tripType();
        if(flag==1) {
        Trip = "From School to Home";}
        else { Trip = "From Home to School";
        }
        if (gpsError) {
            Log.d("bts_error", "gps not enabled");
            Toast.makeText(LoginActivity.this, "Enable GPS!", Toast.LENGTH_LONG).show();
        } else if (!NetworkHelper.isInternetAvailable(this)) {
            Log.d("bts_error", "internet not avail");
            Toast.makeText(LoginActivity.this, "Enable Internet!", Toast.LENGTH_LONG).show();
        } else {
            if (no.isEmpty()) {
                bno.setError("Enter Bus Number!");
            } else {
                db.collection("School_bus")
                        .whereEqualTo("Bus_no", no)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.getResult().size() == 0) {
                                    bno.setError("Enter correct number");
                                } else if (task.isSuccessful()) {
                                    if (pass.isEmpty()) {
                                        password.setError("Enter Bus Number!");
                                    } else {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.d("+1 Fetch", document.getId() + " => " + document.getData());
                                            String cpass = (document.getString("Driver_Password"));
                                            String name = (document.getString("Driver_Name"));
                                            if (cpass.equals(pass)) {
                                                Intent i = new Intent(LoginActivity.this, Home.class);
                                                i.putExtra(SharedPrefHelper.LOGIN_NAME, name);
                                                i.putExtra(SharedPrefHelper.LOGIN_BUS_NO, no);
                                                i.putExtra(SharedPrefHelper.LOGIN_TRIP_TYPE, Trip);
                                                startActivity(i);
                                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                            } else {
                                                password.setError("Enter Correct Password");
                                            }
                                        }
                                    }
                                } else {
                                    Log.d("Fetch error", "Error getting documents: ", task.getException());
                                    Toast.makeText(LoginActivity.this, "Error!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        }
}
@Override
public void onDestroy(){
        super.onDestroy();

}
}