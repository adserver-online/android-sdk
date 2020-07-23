package com.adserver.rtb.request.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Imp {
    public String id;
    public Banner banner;
    public Video video;
    @SerializedName("native")
    public Native aNative;
    public String displaymanager;
    public String displaymanagerver;
    public Integer instl;
    public String tagid;
    public Double bidfloor;
    public String bidfloorcur;
    public Integer clickbrowser;
    public Integer secure;
    public List<String> iframebuster;
    public Integer exp;
    //public Object ext;

    public Imp() {
    }
}
