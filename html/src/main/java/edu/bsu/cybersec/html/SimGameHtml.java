package edu.bsu.cybersec.html;

import com.google.gwt.core.client.EntryPoint;
import edu.bsu.cybersec.core.ClockUtils;
import edu.bsu.cybersec.core.SimGame;
import playn.html.HtmlPlatform;

public class SimGameHtml implements EntryPoint {

    @Override
    public void onModuleLoad() {
        HtmlPlatform.Config config = new HtmlPlatform.Config();
        // use config to customize the HTML platform, if needed
        HtmlPlatform plat = new HtmlPlatform(config);
        plat.assets().setPathPrefix("sim/");
        ClockUtils.formatter = new GWTDateFormatter();
        new SimGame(plat);
        plat.start();
    }
}
