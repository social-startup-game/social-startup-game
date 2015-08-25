package edu.bsu.cybersec.core;

import playn.core.Image;
import playn.core.Platform;
import playn.scene.ImageLayer;
import playn.scene.SceneGame;

public class SimGame extends SceneGame {

    public SimGame(Platform plat) {
        super(plat, 33); // update our "simulation" 33ms (30 times per second)

        // create and add background image layer
        Image bgImage = plat.assets().getImage("images/bg.png");
        ImageLayer bgLayer = new ImageLayer(bgImage);
        // scale the background to fill the screen
        bgLayer.setSize(plat.graphics().viewSize);
        rootLayer.add(bgLayer);
    }
}
