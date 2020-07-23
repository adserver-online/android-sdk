package com.adserver.rtb.request;

import com.adserver.AdRequest;
import com.adserver.AdResponse;
import com.adserver.rtb.request.models.App;
import com.adserver.rtb.request.models.BidRequest;
import com.adserver.rtb.request.models.Device;
import com.adserver.rtb.request.models.User;
import com.adserver.rtb.response.models.BidResponse;

abstract public class BidRequestFactory {
    abstract public BidRequest createBidRequest(AdRequest adRequest, App app, Device device, User user);

    abstract public AdResponse parseBidResponse(BidResponse bidResponse);
}
