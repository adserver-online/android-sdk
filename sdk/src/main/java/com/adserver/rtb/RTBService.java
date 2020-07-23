package com.adserver.rtb;

import com.adserver.rtb.request.models.BidRequest;
import com.adserver.rtb.response.models.BidResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RTBService {
    @POST("/rtb/bidder")
    Call<BidResponse> getBid(@Query("zid") int zoneid, @Body BidRequest bidRequest);
}
