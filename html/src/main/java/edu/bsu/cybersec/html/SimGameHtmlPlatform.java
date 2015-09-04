package edu.bsu.cybersec.html;

import edu.bsu.cybersec.core.PlatformSpecificDateFormatter;
import edu.bsu.cybersec.core.SimGamePlatform;
import playn.html.HtmlPlatform;

public class SimGameHtmlPlatform extends HtmlPlatform implements SimGamePlatform {

    private final GWTDateFormatter formatter = new GWTDateFormatter();

    public SimGameHtmlPlatform(Config config) {
        super(config);
    }

    @Override
    public PlatformSpecificDateFormatter dateFormatter() {
        return formatter;
    }
}
