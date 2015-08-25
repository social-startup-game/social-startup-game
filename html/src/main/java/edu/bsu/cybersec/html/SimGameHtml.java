package edu.bsu.cybersec.html;

import com.google.gwt.core.client.EntryPoint;
import playn.html.HtmlPlatform;
import edu.bsu.cybersec.core.SimGame;

public class SimGameHtml implements EntryPoint {

    @Override
    public void onModuleLoad() {
        HtmlPlatform.Config config = new HtmlPlatform.Config();
        // use config to customize the HTML platform, if needed
        HtmlPlatform plat = new HtmlPlatform(config);
        plat.assets().setPathPrefix("sim/");
        new SimGame(plat);
        plat.start();
    }
}
