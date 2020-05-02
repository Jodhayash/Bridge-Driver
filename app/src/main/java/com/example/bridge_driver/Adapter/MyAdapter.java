package com.example.bridge_driver.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;

import com.example.bridge_driver.Interfaces.ItemClickListener;
import com.example.bridge_driver.Models.StudentModel;
import com.example.bridge_driver.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.util.ArrayList;

public class MyAdapter extends FirestoreRecyclerAdapter<StudentModel, StudentViewHolder>{
    public ArrayList<String> cStu = new ArrayList<String>();

    public MyAdapter(@NonNull FirestoreRecyclerOptions<StudentModel> options) {
        super(options);
    }


    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_model_attend, parent, false);
        StudentViewHolder holder = new StudentViewHolder(v);
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentViewHolder holder, int position, @NonNull StudentModel model) {
        final String ad =model.getAdmission_no();
        holder.txtAdno.setText(ad);
        holder.txtName.setText(model.getName());
        holder.txtClass.setText(model.getStandard());
        holder.txtSection.setText(model.getSec());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                CheckBox chk  = (CheckBox) v;
                if(chk.isChecked()){
                    cStu.add(ad);
                }
                else if(!chk.isChecked()){
                    cStu.remove(ad);
                }
            }
        });
    }
}
