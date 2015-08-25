package edu.bsu.cybersec.java;

import playn.java.LWJGLPlatform;

import edu.bsu.cybersec.core.SimGame;

public class SimGameJava {

    public static void main(String[] args) {
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
        // use config to customize the Java platform, if needed
        LWJGLPlatform plat = new LWJGLPlatform(config);
        new SimGame(plat);
        plat.start();
    }
}
