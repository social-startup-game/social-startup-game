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

package edu.bsu.cybersec.core.study.pre;

import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.ui.layout.TableLayout;

import static com.google.common.base.Preconditions.checkNotNull;

public class SurveyQuestionView extends Group {

    private final Selector selector = new Selector();
    public final SurveyQuestion question;

    public SurveyQuestionView(SurveyQuestion question) {
        super(AxisLayout.vertical().offStretch());
        this.question = checkNotNull(question);
        add(new Label(question.text).addStyles(Style.HALIGN.left, Style.TEXT_WRAP.on));
        Group optionsGroup = new Group(new TableLayout(
                new ExposedColumn(Style.HAlign.CENTER, false, 1, 0),
                new ExposedColumn(Style.HAlign.LEFT, true, 5, 0)));
        for (SurveyQuestion.Option option : question.options) {
            CheckBox checkBox = new OptionCheckBox(option.text);
            selector.add(checkBox);
            optionsGroup.add(checkBox);
            optionsGroup.add(new Label(option.text).addStyles(Style.HALIGN.left));
        }

        add(optionsGroup);
    }

    private final static class ExposedColumn extends TableLayout.Column {
        public ExposedColumn(Style.HAlign halign, boolean stretch, float weight, float minWidth) {
            super(halign, stretch, weight, minWidth);
        }
    }

    public boolean hasSelection() {
        return selector.selected.get() != null;
    }

    public String getSelection() {
        if (!hasSelection()) {
            throw new UnsupportedOperationException("No selection!");
        } else {
            return ((OptionCheckBox) selector.selected.get()).loggableName;
        }
    }

    private static final class OptionCheckBox extends CheckBox {
        public final String loggableName;

        OptionCheckBox(String loggableName) {
            super('X');
            this.loggableName = loggableName;
        }
    }

}
