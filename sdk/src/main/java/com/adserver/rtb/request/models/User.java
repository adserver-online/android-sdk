package com.adserver.rtb.request.models;

import java.util.List;

public class User {
    public String id;
    public String buyeruid;
    public Integer yob;
    public String gender;
    public String keywords;
    public String customdata;
    public Geo geo;
    public List<Data> data;
    //public Object ext;
}
