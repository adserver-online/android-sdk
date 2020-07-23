package com.adserver.rtb.request.models;

import java.util.List;

public class BidRequest {
    public String id;
    public List<Imp> imp;
    public Site site;
    public App app;
    public Device device;
    public User user;
    public Integer at = 2;
    public Integer tmax = 500;
    public List<String> wseat;
    public List<String> bseat;
    public Integer allimps;
    public List<String> cur;
    public List<String> wlang;
    public List<String> bcat;
    public List<String> badv;
    public List<String> bapp;
    //public Object ext;

    public BidRequest() {
    }
}
