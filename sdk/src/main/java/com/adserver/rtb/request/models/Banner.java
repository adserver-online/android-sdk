package com.adserver.rtb.request.models;

import java.util.List;

public class Banner {
    public List<Format> format;
    public Integer w;
    public Integer h;
    @Deprecated
    public Integer wmax;
    @Deprecated
    public Integer hmax;
    @Deprecated
    public Integer wmin;
    @Deprecated
    public Integer hmin;
    public List<Integer> btype;
    public List<Integer> battr;
    public Integer pos;
    public List<String> mimes;
    public Integer topframe;
    public List<Integer> expdir;
    public List<Integer> api;
    public String id;
    public Integer vcm;
    //public Object ext;
}
