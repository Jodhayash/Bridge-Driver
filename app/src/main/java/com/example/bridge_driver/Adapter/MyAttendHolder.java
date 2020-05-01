package com.example.bridge_driver.Adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bridge_driver.R;



public class MyAttendHolder extends RecyclerView.ViewHolder{

     TextView txtKidName;
     TextView txtKidSection;
     CheckBox checkedAttend;
     ImageView imgKid;
     MyAttendHolder(View itemView) {
        super(itemView);
        txtKidName= (TextView) itemView.findViewById(R.id.txtKidName);
        txtKidSection= (TextView) itemView.findViewById(R.id.txtKidSection);
        checkedAttend= (CheckBox) itemView.findViewById(R.id.checkAttend);
        imgKid= (ImageView) itemView.findViewById(R.id.imgKid);
    }
}
