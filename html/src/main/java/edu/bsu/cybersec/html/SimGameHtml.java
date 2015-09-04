package edu.bsu.cybersec.html;

import com.google.gwt.core.client.EntryPoint;
import edu.bsu.cybersec.core.SimGame;
import playn.html.HtmlPlatform;

public class SimGameHtml implements EntryPoint {

    @Override
    public void onModuleLoad() {
        HtmlPlatform.Config config = new HtmlPlatform.Config();
        HtmlPlatform plat = new SimGameHtmlPlatform(config);
        plat.assets().setPathPrefix("sim/");
        new SimGame(plat);
        plat.start();
    }
}
