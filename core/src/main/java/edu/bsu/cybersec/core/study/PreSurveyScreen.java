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
import com.google.common.collect.Lists;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.ui.SimGameStyle;
import edu.bsu.cybersec.core.ui.StartingScreen;
import playn.core.Game;
import react.Slot;
import tripleplay.game.ScreenStack;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import java.util.List;

public class PreSurveyScreen extends ScreenStack.UIScreen {

    private final SimGame game;

    private final ImmutableList<SurveyQuestion> questions = ImmutableList.of(
            SurveyQuestion.named("How old are you?")
                    .logPrefix("age")
                    .option("18-25")
                    .option("26-40")
                    .option("41-55")
                    .option("56+")
                    .build(),
            SurveyQuestion.named("Do you work in Information Technology (IT)?")
                    .logPrefix("IT")
                    .option("Yes")
                    .option("No")
                    .build(),
            SurveyQuestion.named("Do you consider yourself to have good computer security skills?")
                    .logPrefix("skill")
                    .option("Yes")
                    .option("No")
                    .build(),
            SurveyQuestion.named("What is your highest level of education completed?")
                    .logPrefix("ed")
                    .option("Some high school")
                    .option("High school")
                    .option("Some college")
                    .option("Associate's degree")
                    .option("Bachelor's degree")
                    .option("Master's degree")
                    .option("Doctoral degree")
                    .build()
    );

    private List<SurveyQuestionView> views = Lists.newArrayListWithCapacity(questions.size());

    public PreSurveyScreen(SimGame game) {
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
                .addStyles(Style.BACKGROUND.is(Background.solid(Colors.WHITE)));
        root.setLocation((game.plat.graphics().viewSize.width() - game.bounds.width()) / 2,
                (game.plat.graphics().viewSize.height() - game.bounds.height()) / 2);
        for (SurveyQuestion question : questions) {
            SurveyQuestionView view = new SurveyQuestionView(question);
            views.add(view);
            root.add(view);
            root.add(new Shim(0, game.bounds.percentOfHeight(0.01f)));
        }
        root.add(new Group(AxisLayout.horizontal())
                .add(new Button("OK").onClick(new Slot<Button>() {
                    @Override
                    public void onEmit(Button button) {
                        logResponses();
                        game.screenStack.replace(new StartingScreen(game.screenStack), game.screenStack.slide());
                    }
                }))
                .setConstraint(AxisLayout.fixed())
        );
    }

    private void logResponses() {
        StringBuilder stringBuilder = new StringBuilder();
        for (SurveyQuestionView question : views) {
            if (question.hasSelection()) {
                String selection = question.getSelection();
                stringBuilder.append(question.question.logPrefix)
                        .append(":")
                        .append(selection)
                        .append(";");
            }
        }
        String output = stringBuilder.toString();
        if (!output.isEmpty()) {
            game.plat.log().info(output);
        }
    }

    @Override
    public Game game() {
        return game;
    }
}
