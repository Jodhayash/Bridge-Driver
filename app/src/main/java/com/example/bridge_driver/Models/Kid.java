package com.example.bridge_driver.Models;

import com.example.bridge_driver.Interfaces.Checkable;


// kid with 'checked' property (to be used in recycle view for attendance)
public class Kid extends KidResponse implements Checkable{

    private Boolean checked=Checkable.checked; //default false


    public Kid(int id, String name, String section, String url) {
        this.id=id;
        this.name=name;
        this.section=section;
        this.image_url=url;
    }


    @Override
    public Boolean isChecked() {
        return this.checked;
    }

    @Override
    public void setChecked(Boolean value) {
        this.checked=value;
    }
}
