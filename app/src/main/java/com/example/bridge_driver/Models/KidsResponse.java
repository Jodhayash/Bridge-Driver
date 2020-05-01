package com.example.bridge_driver.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class KidsResponse extends Response{
    @SerializedName("kids")
    @Expose
    private List<KidResponse> kids = null;

    public List<KidResponse> getKids() {
        return kids;
    }

}
