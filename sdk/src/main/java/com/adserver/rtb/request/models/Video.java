package com.adserver.rtb.request.models;

import java.util.List;

public class Video {
    public List<String> mimes;
    public Integer minduration;
    public Integer maxduration;
    public List<Integer> protocols;
    @Deprecated
    public Integer protocol;
    public Integer w;
    public Integer h;
    public Integer startdelay;
    public Integer placement;
    public Integer linearity;
    public Integer skip;
    public Integer skipmin = 0;
    public Integer skipafter = 0;
    public Integer sequence;
    public List<Integer> battr;
    public Integer maxextended;
    public Integer minbitrate;
    public Integer maxbitrate;
    public Integer boxingallowed = 1;
    public List<Integer> playbackmethod;
    public Integer playbackend;
    public List<Integer> delivery;
    public Integer pos;
    public List<Banner> companionad;
    public List<Integer> api;
    public List<Integer> companiontype;
    //public Object ext;
}
