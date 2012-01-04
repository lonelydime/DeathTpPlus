package org.simiancage.DeathTpPlus.helpers;

/**
 * PluginName: DeathTpPlus
 * Class: PINGS
 * User: DonRedhorse
 * Date: 04.01.12
 * Time: 08:51
 */

// taken from  https://github.com/Adamki11s/Regios

public enum PINGS {

    // ToDo add new bit.ly links when new version comes out.

    ON_ENABLE("http://bit.ly/zlutwL"),
    ON_CREATE("http://bit.ly/yV0Swt"),
    ON_UPDATE("http://bit.ly/yctIg4"),
    BUNDLE("http://bit.ly/yIasPj");

    private String url;

    PINGS(String url) {
        this.url = url;
    }

    public String getURL() {
        return this.url;
    }
}

