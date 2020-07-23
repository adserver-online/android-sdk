package com.adserver;

import android.location.Location;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class AdRequest {

    private String gender;
    private Location location;
    private Bundle customExtras;
    private List<String> keywords = new ArrayList<>();
    private List<String> blockedCategories = new ArrayList<>();

    private int mZoneID;
    private int mWidth = 320;
    private int mHeight = 50;
    private int mYearOfBirth = 0;

    public AdRequest(int zoneID) {
        this();
        this.mZoneID = zoneID;
    }

    public AdRequest(int zoneID, int width, int height) {
        this();
        this.mZoneID = zoneID;
        this.mWidth = width;
        this.mHeight = height;
    }

    public AdRequest() {
        blockedCategories.add("IAB24");
        blockedCategories.add("IAB25");
        blockedCategories.add("IAB26");
    }

    public void setAdSize(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getZoneID() {
        return this.mZoneID;
    }

    public void setZoneID(int id) {
        this.mZoneID = id;
    }

    public List<String> getBlockedCategories() {
        return this.blockedCategories;
    }

    public void setBlockedCategories(List<String> cats) {
        this.blockedCategories = cats;
    }

    public List<String> getKeywords() {
        return this.keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getYearOfBirth() {
        return mYearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.mYearOfBirth = yearOfBirth;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public void setWidth(int width) {
        this.mWidth = width;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public void setHeight(int height) {
        this.mHeight = height;
    }

    public Bundle getCustomExtras() {
        return customExtras;
    }

    public void setCustomExtras(Bundle customExtras) {
        this.customExtras = customExtras;
    }
}
