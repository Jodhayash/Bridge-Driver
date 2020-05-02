package com.example.bridge_driver.Adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bridge_driver.Interfaces.ItemClickListener;
import com.example.bridge_driver.R;

public class StudentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    TextView txtAdno;
    TextView txtName;
    TextView txtClass;
    TextView txtSection;

    CheckBox cAttend;
    ItemClickListener itemClickListener;
        public StudentViewHolder(View itemView) {
            super(itemView);
            txtAdno= (TextView) itemView.findViewById(R.id.txtKidAdno);
            txtName= (TextView) itemView.findViewById(R.id.txtKidName);
            txtClass= (TextView) itemView.findViewById(R.id.txtKidClass);
            txtSection= (TextView) itemView.findViewById(R.id.txtKidSection);
            cAttend = itemView.findViewById(R.id.checkAttend);

            cAttend.setOnClickListener(this);
        }

        /*public void setAdno(String adno) {
            TextView textView1 = view.findViewById(R.id.txtKidAdno);
            textView1.setText(adno);
        }
        public void setName(String Name) {
            TextView textView2 = view.findViewById(R.id.txtKidName);
            textView2.setText(Name);
        }
        public void setStandard(String Standard) {
            TextView textView3 = view.findViewById(R.id.txtKidClass);
            textView3.setText(Standard);
        }
        public void setSec(String Sec) {
            TextView textView4 = view.findViewById(R.id.txtKidSection);
            textView4.setText(Sec);
        }*/
        public void setItemClickListener(ItemClickListener ic){
            this.itemClickListener = ic;
        }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }
}
