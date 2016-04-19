/*
 * Copyright 2016 Paul Gestwicki
 *
 * This file is part of The Social Startup Game
 *
 * The Social Startup Game is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Social Startup Game is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The Social Startup Game.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.bsu.cybersec.core.study;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.TrackedEvent;
import edu.bsu.cybersec.core.ui.FontCache;
import edu.bsu.cybersec.core.ui.SimGameStyle;
import playn.core.Game;
import react.Slot;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import java.util.Map;

public final class PostSurveyScreen extends ScreenStack.UIScreen {

    private static final float GAP_BETWEEN_QUESTIONS = SimGame.game.bounds.percentOfHeight(0.02f);
    private static final float HORIZONTAL_INSET = SimGame.game.bounds.percentOfHeight(0.005f);

    private final SimGame game;
    private final LikertTable table = new LikertTable();
    private final Field field = new Field();

    public PostSurveyScreen(SimGame game) {
        super(game.plat);
        this.game = game;
    }

    @Override
    public void wasAdded() {
        super.wasAdded();
        createUI();
    }

    private void createUI() {
        Root root = iface.createRoot(AxisLayout.vertical().offStretch(), SimGameStyle.newSheet(game.plat.graphics()), layer)
                .setSize(game.bounds.size())
                .addStyles(Style.BACKGROUND.is(Background.solid(Colors.WHITE).inset(HORIZONTAL_INSET, 0)));
        root.setLocation((game.plat.graphics().viewSize.width() - game.bounds.width()) / 2,
                (game.plat.graphics().viewSize.height() - game.bounds.height()) / 2);

        root.add(new Label("Please rate how much you agree with the following statements.")
                .addStyles(Style.TEXT_WRAP.on));
        root.add(table);
        root.add(new Shim(0, GAP_BETWEEN_QUESTIONS));
        root.add(new Label("Is there anything else you want to share with us about your game play experience?")
                .addStyles(Style.TEXT_WRAP.on));
        root.add(field);
        root.add(new Shim(0, GAP_BETWEEN_QUESTIONS));
        root.add(new Button("OK").onClick(new Slot<Button>() {
            @Override
            public void onEmit(Button button) {
                logResponses();
                game.screenStack.remove(PostSurveyScreen.this, game.screenStack.slide().right());
            }

            private void logResponses() {
                StringBuilder stringBuilder = new StringBuilder();
                for (Selector selector : table.selectorMap.keySet()) {
                    LikertTable.LevelButton selected = (LikertTable.LevelButton) selector.selected.get();
                    if (selected != null) {
                        String action = table.selectorMap.get(selector).code;
                        String label = selected.level.code;
                        SimGame.game.event.emit(TrackedEvent.survey().action(action).label(label));
                    }
                }
                if (!field.text.get().isEmpty()) {
                    SimGame.game.event.emit(TrackedEvent.survey().action("free").label(field.text.get()));
                }
            }
        }));
    }

    @Override
    public Game game() {
        return SimGame.game;
    }

    private static final class LikertTable extends Group {

        private static final float INTERNAL_SHIM_SIZE = SimGame.game.bounds.percentOfHeight(0.01f);

        private static final class Level {
            public final String text;
            public final String code;

            public Level(String text, String code) {
                this.text = text;
                this.code = code;
            }
        }

        private static final ImmutableList<Level> LEVELS = ImmutableList.of(
                new Level("Strongly Agree", "SA"),
                new Level("Agree", "A"),
                new Level("Neutral", "N"),
                new Level("Disagree", "D"),
                new Level("Strongly Disagree", "SD")
        );

        private static final class Prompt {
            public final String text;
            public final String code;

            public Prompt(String text, String code) {
                this.text = text;
                this.code = code;
            }
        }

        private static final ImmutableList<Prompt> PROMPTS = ImmutableList.of(
                new Prompt("The game was enjoyable", "enjoy"),
                new Prompt("The characters were believable", "chars"),
                new Prompt("The decisions were difficult", "difficult"),
                new Prompt("I could become an app developer", "me")
        );

        private final Map<Selector, Prompt> selectorMap = Maps.newHashMap();

        private LikertTable() {
            super(AxisLayout.vertical().offStretch());

            for (Prompt prompt : PROMPTS) {
                add(new Label(prompt.text).addStyles(Style.HALIGN.left));
                Group responseGroup = new Group(AxisLayout.horizontal());
                Selector selector = new Selector();
                selectorMap.put(selector, prompt);
                for (Level level : LEVELS) {
                    LevelButton toggle = new LevelButton(level);
                    selector.add(toggle);
                    responseGroup.add(toggle);
                }
                add(responseGroup);
                add(new Shim(0, INTERNAL_SHIM_SIZE));
            }
        }

        private static final class LevelButton extends ToggleButton {
            final Level level;

            LevelButton(Level level) {
                super(level.text);
                this.level = level;
                addStyles(Style.FONT.is(FontCache.instance().REGULAR.derive(12f)));
            }
        }
    }
}
