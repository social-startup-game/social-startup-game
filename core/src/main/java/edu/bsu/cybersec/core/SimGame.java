package edu.bsu.cybersec.core;

import playn.core.Platform;
import playn.scene.SceneGame;
import tripleplay.game.ScreenStack;

public class SimGame extends SceneGame {

    // This is required for ScreenStack.UIScreen's game() method, which is called before the constructor.
    // See https://github.com/threerings/tripleplay/blob/master/demo/core/src/main/java/tripleplay/demo/TripleDemo.java#L19
    static SimGame game;

    public SimGame(Platform plat) {
        super(plat, 33);
        game = this;
        ScreenStack screenStack = new ScreenStack(this, rootLayer);
        screenStack.push(new GameScreen());
    }

}
