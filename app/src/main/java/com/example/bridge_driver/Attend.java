package com.example.bridge_driver;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.bridge_driver.Adapter.MyAdapter;
import com.example.bridge_driver.Models.StudentModel;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class Attend extends AppCompatActivity {
    private RecyclerView r1;
    private RecyclerView.LayoutManager layoutManager;
    FirebaseFirestore ndb = FirebaseFirestore.getInstance();
    MyAdapter adapter;
    String BUS_NO, Status;
    Button bok,bcan;
    TextView tt;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attend);
        BUS_NO = getIntent().getStringExtra("Bus no");
        Status = getIntent().getStringExtra("Status");
        bok= findViewById(R.id.btnOk);
        bcan= findViewById(R.id.btnCancel);
        r1 = findViewById(R.id.listKid);
        layoutManager = new LinearLayoutManager(this);
        r1.setLayoutManager(layoutManager);
        switch (Status) {
            case "Pick" :
                query = ndb.collection("Students").whereEqualTo("Bus_no", BUS_NO).whereEqualTo("InBus", false);
                break;
            case "Drop":
                query = ndb.collection("Students").whereEqualTo("Bus_no", BUS_NO).whereEqualTo("InBus", true);
                bok.setText("Drop");
                break;
        }
        getDataList();
        bcan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void getDataList(){

        FirestoreRecyclerOptions<StudentModel> options = new FirestoreRecyclerOptions.Builder<StudentModel>()
                .setQuery(query, StudentModel.class)
                .build();
        adapter = new MyAdapter(options);
        r1.setAdapter(adapter);
    }

    public void onAtPickClick(View v) {
        ArrayList<String> st = adapter.cStu ;
        if(Status.equals("Pick")){
            for( String id : st){
            ndb.collection("Students").document(id)
                    .update("InBus",true);
        }
        st = adapter.cStu = null;
        }
        else if (Status.equals("Drop")) {
            for (String id : st) {
                ndb.collection("Students").document(id)
                        .update("InBus", false);
            }
            st = adapter.cStu = null;
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
