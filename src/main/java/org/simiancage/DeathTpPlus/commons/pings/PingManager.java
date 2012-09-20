package org.simiancage.DeathTpPlus.commons.pings;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * PluginName: DeathTpPlus
 * Class: PingManager
 * User: DonRedhorse
 * Date: 04.01.12
 * Time: 08:50
 */

// taken from     https://github.com/Adamki11s/Regios

public class PingManager {
    private static void sendPing(String Url) {
        try {
            URL url = new URL(Url);
            HttpURLConnection con = (HttpURLConnection) (url.openConnection());
            System.setProperty("http.agent", "");
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.100 Safari/534.30");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            while ((in.readLine()) != null) {
                ;
            }
            in.close();
        } catch (Exception e) {
        }
    }

    public static void enabled() {
        sendPing(PINGS.ON_ENABLE.getURL());
    }

    public static void created() {
        sendPing(PINGS.ON_CREATE.getURL());
    }

    public static void update() {
        sendPing(PINGS.ON_UPDATE.getURL());
    }


}

