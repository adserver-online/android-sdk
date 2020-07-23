package com.adserver;

import com.adserver.mraid.MraidBridge;

public class AdResponse {

    public String html;

    public boolean getIsMraid() {
        return html.contains(MraidBridge.MRAID_JS);
    }
}
