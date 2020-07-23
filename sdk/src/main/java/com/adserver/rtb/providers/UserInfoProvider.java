package com.adserver.rtb.providers;

import android.content.Context;

import com.adserver.rtb.request.models.User;

public class UserInfoProvider {

    private String exchangeUserId;

    public UserInfoProvider(Context context) {
    }

    public User getUser() {
        User user = new User();
        user.id = getExchangeUserId();

        return user;
    }

    private String getExchangeUserId() {
        return exchangeUserId;
    }
}
