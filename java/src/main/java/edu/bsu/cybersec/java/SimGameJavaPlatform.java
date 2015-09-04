package edu.bsu.cybersec.java;

import edu.bsu.cybersec.core.PlatformSpecificDateFormatter;
import edu.bsu.cybersec.core.SimGamePlatform;
import playn.java.LWJGLPlatform;

public class SimGameJavaPlatform extends LWJGLPlatform implements SimGamePlatform {

    private final Java7DateFormatter formatter = new Java7DateFormatter();

    public SimGameJavaPlatform(Config config) {
        super(config);
    }

    @Override
    public PlatformSpecificDateFormatter formatter() {
        return formatter;
    }
}
