package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.SimGame;
import playn.core.Image;

public enum PreloadedImage {
    ESTEBAN("Esteban.png"),
    NANCY("Nancy.png"),
    JERRY("Jerry.png"),
    ADMIN("admin.png"),
    DOLLAR_SIGN("dollar-sign.png"),
    ENVELOPE("envelope.png"),
    STAR("star.png"),
    WRENCH("wrench.png");

    public final Image image;

    PreloadedImage(String name) {
        this.image = SimGame.game.plat.assets().getImage("images/" + name);
    }
}
