package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameWorld;
import react.Slot;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.FlowLayout;
import tripleplay.util.Colors;

public final class GameInteractionArea extends Group {

    private Group shown = new Group(new FlowLayout());
    private final CompanyStatusGroupSystem companyStatusGroupSystem;

    public GameInteractionArea(GameWorld gameWorld) {
        super(AxisLayout.vertical());
        this.companyStatusGroupSystem = new CompanyStatusGroupSystem(gameWorld);

        shown.add(companyStatusGroupSystem.group);
        add(shown.setConstraint(AxisLayout.stretched()));
        add(makeButtonArea().setConstraint(AxisLayout.fixed()));
        addStyles(Style.BACKGROUND.is(Background.solid(Colors.MAGENTA)));
    }

    private Element makeButtonArea() {
        return new Group(AxisLayout.horizontal())
                .add(new ChangeViewButton("Status", companyStatusGroupSystem.group),
                        new ChangeViewButton("Features", new Label("Features")),
                        new ChangeViewButton("Defects", new Label("Defects")),
                        new ChangeViewButton("Events", new Label("News and events")));
    }

    private final class ChangeViewButton extends Button {
        ChangeViewButton(String text, final Element<?> view) {
            super(text);
            onClick(new Slot<Button>() {
                @Override
                public void onEmit(Button event) {
                    shown.removeAll();
                    shown.add(view);
                }
            });
        }
    }
}
