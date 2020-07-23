package com.adserver.rtb.providers;

import android.content.Context;

import com.adserver.Adserver;
import com.adserver.rtb.attributes.ConnectionType;
import com.adserver.rtb.attributes.DeviceType;
import com.adserver.rtb.request.models.Device;
import com.adserver.utils.AdvertisingIdClient;
import com.adserver.utils.DeviceInfo;
import com.adserver.utils.NetworkInfo;

import java.util.HashMap;
import java.util.Map;

public class DeviceInfoProvider {

    private final GeoProvider mGeoProvider;

    private String userAgent;
    private String ip;
    private String carrier;
    private String language;
    private String model;
    private String os;
    private String osVersion;
    private int connectionType;
    private int deviceType;
    private String make;
    private String ifa;
    private Integer dnt;

    public DeviceInfoProvider(Context context, GeoProvider geoProvider) {
        this.mGeoProvider = geoProvider;

        DeviceInfo deviceInfo = new DeviceInfo(context);
        NetworkInfo networkInfo = new NetworkInfo(context);

        this.carrier = networkInfo.carrierName;
        this.userAgent = deviceInfo.userAgent;
        this.language = deviceInfo.language;
        this.model = deviceInfo.model;
        this.os = deviceInfo.osName;
        this.osVersion = deviceInfo.osVersion;
        this.make = deviceInfo.manufacturer;
        this.connectionType = getRtbConnectionType(networkInfo.networkClass);

        this.deviceType = deviceInfo.isTablet ? DeviceType.TABLET : DeviceType.PHONE;

        AdvertisingIdClient.AdInfo adInfo = Adserver.getInstance().adInfo;

        this.ifa = adInfo.getId();
        if (adInfo.isLimitAdTrackingEnabled()) {
            this.dnt = 1;
        }
    }

    public Device getDevice() {
        Device device = new Device();
        device.ua = getUserAgent();
        device.ip = getIp();
        device.geo = mGeoProvider.getGeo();
        device.carrier = getCarrier();
        device.language = getLanguage();
        device.model = getModel();
        device.make = getMake();
        device.os = getOS();
        device.osv = getOSVersion();
        device.connectiontype = getConnectionType();
        device.devicetype = getDeviceType();
        device.ifa = getIFA();
        device.dnt = getDnt();

        return device;
    }

    private int getRtbConnectionType(String connectionType) {
        Map<String, Integer> map;

        map = new HashMap<>();
        map.put("WIFI", ConnectionType.WIFI);
        map.put("2G", ConnectionType.CELLULAR_2G);
        map.put("3G", ConnectionType.CELLULAR_3G);
        map.put("4G", ConnectionType.CELLULAR_4G);

        if (map.containsKey(connectionType)) {
            return map.get(connectionType);
        }

        return ConnectionType.UNKNOWN;
    }

    private String getUserAgent() {
        return userAgent;
    }

    private String getIp() {
        return ip;
    }

    private String getCarrier() {
        return carrier;
    }

    private String getLanguage() {
        return language;
    }

    private String getModel() {
        return model;
    }

    private String getOS() {
        return os;
    }

    private String getOSVersion() {
        return osVersion;
    }

    private int getConnectionType() {
        return connectionType;
    }

    private int getDeviceType() {
        return deviceType;
    }

    private String getIFA() {
        return ifa;
    }

    private String getMake() {
        return make;
    }

    public Integer getDnt() {
        return dnt;
    }
}
