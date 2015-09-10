package edu.bsu.cybersec.core;

import playn.core.Game;
import playn.scene.Layer;
import playn.scene.Mouse;
import playn.scene.Pointer;
import pythagoras.f.Point;
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

    private Value<Element<?>> focus = Value.create(null);

    public CollapsingScreen() {
        configurePointerInput();
        focus.connect(new ValueView.Listener<Element<?>>() {
            @Override
            public void onChange(Element<?> value, Element<?> oldValue) {
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

    private void configurePointerInput() {
        new Pointer(game().plat, layer, true);
        game().plat.input().mouseEvents.connect(new Mouse.Dispatcher(layer, false));
    }

    private class WeightValue implements Animation.Value {

        private Element<?> widget;

        public WeightValue(Element<?> widget) {
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
        final Element<?> g1 = new ClickableGroup("Foo")
                .setStyles(Style.BACKGROUND.is(Background.solid(Colors.WHITE)));
        final Element<?> g2 = new ClickableGroup("Bar")
                .setStyles(Style.BACKGROUND.is(Background.solid(Colors.CYAN)));
        final Element<?> g3 = new ClickableGroup("Baz")
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

    private class ClickableGroup extends Group {

        public ClickableGroup(final String text) {
            super(AxisLayout.horizontal().offStretch());
            add(new Label(text));
            layer.setInteractive(true);
            layer.setHitTester(new Layer.HitTester() {
                @Override
                public Layer hitTest(Layer layer, Point p) {
                    return (p.x >= 0 && p.y >= 0 &&
                            p.x() <= _size.width && p.y() <= _size.height)
                            ? layer : null;
                }
            });
            layer.events().connect(new Pointer.Listener() {
                @Override
                public void onStart(Pointer.Interaction iact) {
                    if (focus.get() == ClickableGroup.this) {
                        focus.update(null);
                    } else {
                        focus.update(ClickableGroup.this);
                    }
                }
            });
        }
    }

    @Override
    public Game game() {
        return SimGame.game;
    }
}
