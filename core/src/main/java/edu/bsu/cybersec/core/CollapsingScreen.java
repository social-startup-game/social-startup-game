package edu.bsu.cybersec.core;

import playn.core.Game;
import playn.scene.Mouse;
import playn.scene.Pointer;
import react.Value;
import react.ValueView;
import tripleplay.anim.Animation;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AbsoluteLayout;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkNotNull;

public class CollapsingScreen extends ScreenStack.UIScreen {

    private Value<Widget<?>> focus = Value.create(null);

    public CollapsingScreen() {
        new Pointer(game().plat, layer, true);
        game().plat.input().mouseEvents.connect(new Mouse.Dispatcher(layer, false));
        focus.connect(new ValueView.Listener<Widget<?>>() {
            @Override
            public void onChange(Widget<?> value, Widget<?> oldValue) {
                final float duration = 1000f;
                if (oldValue != null) {
                    iface.anim.tween(new WeightValue(oldValue)).from(5).to(1).in(duration).easeOut();
                }
                if (value != null) {
                    iface.anim.tween(new WeightValue(value)).from(1).to(5).in(duration).easeOut();
                }
            }
        });
    }

    private class WeightValue implements Animation.Value {

        private Widget<?> widget;

        public WeightValue(Widget<?> widget) {
            this.widget = checkNotNull(widget);
        }

        @Override
        public float initial() {
            return 1;
        }

        @Override
        public void set(float value) {
            AxisLayout.Constraint constraint = AxisLayout.stretched(value);
            widget.setConstraint(constraint);
        }
    }

    @Override
    protected Root createRoot() {
        Group topGroup = new Group(AxisLayout.vertical().stretchByDefault().offStretch().gap(0));
        final Widget<?> g1 = new ClickableLabel("Foo")
                .setStyles(Style.BACKGROUND.is(Background.solid(Colors.WHITE)));
        final Widget<?> g2 = new ClickableLabel("Bar")
                .setStyles(Style.BACKGROUND.is(Background.solid(Colors.CYAN)));
        final Widget<?> g3 = new ClickableLabel("Baz")
                .setStyles(Style.BACKGROUND.is(Background.solid(Colors.BLUE)));

        topGroup.add(g1, g2, g3);

        Root root = iface.createRoot(new AbsoluteLayout(), SimpleStyles.newSheet(game().plat.graphics()))
                .setSize(size());
        root.add(AbsoluteLayout.at(topGroup, 0, 0, size().width(), size().height() / 2),
                AbsoluteLayout.at(
                        new Group(new AbsoluteLayout())
                                .setStyles(Style.BACKGROUND.is(Background.solid(Colors.RED))),
                        0, size().height() / 2, size().width(), size().height() / 2));

        return root;
    }

    private class ClickableLabel extends Label {

        public ClickableLabel(String text) {
            super(text);
            layer.setInteractive(true);
        }

        @Override
        protected Behavior<Label> createBehavior() {
            return new AbstractBehavior<Label>(this) {
                @Override
                public void onClick(Pointer.Interaction iact) {
                    if (focus.get() == ClickableLabel.this) {
                        focus.update(null);
                    } else {
                        focus.update(ClickableLabel.this);
                    }
                }
            };
        }
    }

    private static abstract class AbstractBehavior<T extends Widget<T>> extends Behavior<T> {

        public AbstractBehavior(T owner) {
            super(owner);
        }

        @Override
        public void onPress(Pointer.Interaction iact) {
        }

        @Override
        public void onHover(Pointer.Interaction iact, boolean inBounds) {
        }

        @Override
        public boolean onRelease(Pointer.Interaction iact) {
            return true;
        }

        @Override
        public void onClick(Pointer.Interaction iact) {
        }
    }

    @Override
    public Game game() {
        return SimGame.game;
    }
}
