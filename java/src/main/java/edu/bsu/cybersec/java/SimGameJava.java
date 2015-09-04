package edu.bsu.cybersec.java;

import edu.bsu.cybersec.core.ClockUtils;
import edu.bsu.cybersec.core.SimGame;
import playn.java.LWJGLPlatform;

public class SimGameJava {

    public static void main(String[] args) {
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
        // use config to customize the Java platform, if needed
        LWJGLPlatform plat = new LWJGLPlatform(config);
        new SimGame(plat);

        ClockUtils.formatter = new Java7DateFormatter();

        plat.start();
    }
}
