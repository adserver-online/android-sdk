package com.adserver.rtb.response.models;

import java.util.List;

public class BidResponse {
    public String id;
    public List<SeatBid> seatbid;
    public String bidid;
    public String cur = "USD";
    public String customdata;
    public int nbr;
    //public Object ext;
}
