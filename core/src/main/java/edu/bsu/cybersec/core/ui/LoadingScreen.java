package edu.bsu.cybersec.core.ui;

import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Game;
import playn.core.Image;
import react.RFuture;
import react.Slot;
import react.Try;
import tripleplay.game.ScreenStack;
import tripleplay.ui.Label;
import tripleplay.ui.Root;
import tripleplay.ui.SimpleStyles;
import tripleplay.ui.Style;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class LoadingScreen extends ScreenStack.UIScreen {

    private final ScreenStack screenStack;

    public LoadingScreen(ScreenStack screenStack) {
        this.screenStack = checkNotNull(screenStack);
    }

    @Override
    public void wasShown() {
        super.wasShown();
        List<RFuture<Image>> futures = Lists.newArrayList();
        for (PreloadedImage preloadedImage : PreloadedImage.values()) {
            futures.add(preloadedImage.image.state);
        }
        RFuture.collect(futures).onComplete(new Slot<Try<Collection<Image>>>() {
            @Override
            public void onEmit(Try<Collection<Image>> event) {
                if (event instanceof Try.Failure) {
                    game().plat.log().warn("Failed to load some images: " + event);
                } else {
                    screenStack.push(new GameScreen(), screenStack.slide());
                }
            }
        });
    }

    @Override
    protected Root createRoot() {
        Root root = new Root(iface, AxisLayout.vertical(), SimpleStyles.newSheet(game().plat.graphics())).setSize(size());
        root.add(new Label("Loading...").addStyles(Style.COLOR.is(Colors.WHITE)));
        return root;
    }

    @Override
    public Game game() {
        return SimGame.game;
    }
}
