package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import tripleplay.ui.Group;
import tripleplay.ui.layout.AxisLayout;

public class GameInteractionArea extends Group {

    private final CompanyStatusGroupSystem companyStatusGroupSystem;

    public GameInteractionArea(GameWorld gameWorld) {
        super(AxisLayout.horizontal());
        this.companyStatusGroupSystem = new CompanyStatusGroupSystem(gameWorld);
        add(companyStatusGroupSystem.group);
    }
}
