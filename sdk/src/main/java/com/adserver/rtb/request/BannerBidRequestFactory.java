package com.adserver.rtb.request;

import com.adserver.AdRequest;
import com.adserver.AdResponse;
import com.adserver.rtb.request.models.App;
import com.adserver.rtb.request.models.Banner;
import com.adserver.rtb.request.models.BidRequest;
import com.adserver.rtb.request.models.Device;
import com.adserver.rtb.request.models.Imp;
import com.adserver.rtb.request.models.User;
import com.adserver.rtb.response.models.BidResponse;

import java.util.ArrayList;
import java.util.UUID;

public class BannerBidRequestFactory extends BidRequestFactory {

    public BidRequest createBidRequest(AdRequest adRequest, App app, Device device, User user) {
        BidRequest bidRequest = new BidRequest();

        bidRequest.id = UUID.randomUUID().toString();

        Imp imp = new Imp();
        imp.id = UUID.randomUUID().toString();
        Banner banner = new Banner();

        banner.w = adRequest.getWidth();
        banner.h = adRequest.getHeight();
        imp.banner = banner;

        bidRequest.imp = new ArrayList<>(1);
        bidRequest.imp.add(imp);

        bidRequest.app = app;
        bidRequest.device = device;
        bidRequest.user = user;

        bidRequest.bcat = adRequest.getBlockedCategories();

        return bidRequest;
    }

    public AdResponse parseBidResponse(BidResponse bidResponse) {
        AdResponse adResponse = new AdResponse();

        try {
            adResponse.html = bidResponse.seatbid.get(0).bid.get(0).adm;
        } catch (IndexOutOfBoundsException ignored) {

        }

        return adResponse;
    }
}
