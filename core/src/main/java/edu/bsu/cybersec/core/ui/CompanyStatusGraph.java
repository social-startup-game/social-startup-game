/*
 * Copyright 2015 Paul Gestwicki
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

package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.*;
import playn.core.Canvas;
import playn.core.Surface;
import playn.core.TextFormat;
import playn.core.TextLayout;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import pythagoras.f.Dimension;
import pythagoras.f.MathUtil;
import react.ValueView;
import tripleplay.ui.SizableWidget;
import tripleplay.util.Colors;

public final class CompanyStatusGraph extends SizableWidget<CompanyStatusGraph> {

    private static final int BACKGROUND_COLOR = Colors.WHITE;
    private static final int FOREGROUND_COLOR = GameColors.HUNTER_GREEN;
    private static final int POLL_INTERVAL = ClockUtils.SECONDS_PER_HOUR / 6;
    private static final int MINIMUM_X_UNITS = 100;
    private static final float BORDER_WIDTH_PERCENT = 0.01f;
    private static final float THICKNESS = 1f;

    private SizeConstrainedSmoothingList data = SizeConstrainedSmoothingList.withCapacity(200);
    private int max;

    public CompanyStatusGraph(final GameWorld world) {
        layer.add(new Layer() {
            @Override
            protected void paintImpl(Surface surface) {
                invertYAxisForConvenientDrawing(surface);
                drawBackgroundAndBorder(surface);
                if (data.size() > 0) {
                    drawData(surface);
                }
            }

            private void invertYAxisForConvenientDrawing(Surface surface) {
                // http://stackoverflow.com/questions/4335400/in-html5-canvas-can-i-make-the-y-axis-go-up-rather-than-down
                surface.transform(1, 0, 0, -1, 0, _size.height);
            }

            private void drawBackgroundAndBorder(Surface surface) {
                final float borderSize = _size.width * BORDER_WIDTH_PERCENT;
                final float twiceBorderSize = borderSize * 2f;
                surface.setFillColor(FOREGROUND_COLOR);
                surface.fillRect(0, 0, _size.width, _size.height);
                surface.setFillColor(BACKGROUND_COLOR);
                surface.fillRect(borderSize, borderSize, _size.width - twiceBorderSize, _size.height - twiceBorderSize);
            }

            private void drawData(Surface surface) {
                final float minXPerUnit = _size.width / MINIMUM_X_UNITS;
                surface.setFillColor(FOREGROUND_COLOR);
                final float yPerUnit = _size.height / max;
                final float xPerUnit = Math.min(minXPerUnit, _size.width / (data.size() - 1));

                float prevX = 0;
                float prevY = yPerUnit * data.get(0);
                for (int i = 1, limit = data.size(); i < limit; i++) {
                    float newX = i * xPerUnit;
                    float newY = data.get(i) * yPerUnit;
                    surface.drawLine(prevX, prevY, newX, newY, THICKNESS);
                    prevX = newX;
                    prevY = newY;
                }
                drawGoalLine(surface, yPerUnit);
            }

            private void drawGoalLine(Surface surface, float yPerUnit) {
                // Draw goal line
                final float goal = world.company.get().goal.minimum;
                final float goalY = goal * yPerUnit;
                if (goalY < _size.height) {
                    surface.setFillColor(GameColors.GRANNY_SMITH);
                    surface.drawLine(0, goal * yPerUnit, _size.width, goal * yPerUnit, THICKNESS);
                }
            }
        });
        configurePreferredSize();
        configurePollingToUpdateData(world);
        addGraphLabel();
    }

    private void addGraphLabel() {
        final String message = "USERS";
        final TextFormat textFormat = new TextFormat().withFont(FontCache.instance().REGULAR);
        final TextLayout textLayout = SimGame.game.plat.graphics().layoutText(message, textFormat);
        final Canvas canvas = SimGame.game.plat.graphics().createCanvas(textLayout.size);
        canvas.setFillColor(FOREGROUND_COLOR).fillText(textLayout, 0, 0);
        layer.addAt(new ImageLayer(canvas.toTexture()).setRotation(-MathUtil.HALF_PI),
                textLayout.size.height() * 0.2f,
                textLayout.size.width() * 1.5f);
    }

    private void configurePreferredSize() {
        // It does not seem to matter _what_ dimension is used here,
        // but calling this method ensures that the graph is properly stretched when inside an axislayout.
        preferredSize.update(new Dimension(100, 100));
    }

    private void configurePollingToUpdateData(final GameWorld world) {
        world.gameTime.connect(new ValueView.Listener<GameTime>() {
            private int lastUpdate;

            @Override
            public void onChange(GameTime value, GameTime oldValue) {
                if (value.now - lastUpdate > POLL_INTERVAL) {
                    addDatum(world.users.get().intValue());
                    lastUpdate = value.now;
                }
            }

            private void addDatum(int datum) {
                if (data.size() == 0) {
                    setInitialMaxToShowLineStartingNearBottomOfGraph(datum);
                }
                data.add(datum);
                max = Math.max(max, datum);
            }

            private void setInitialMaxToShowLineStartingNearBottomOfGraph(int datum) {
                max = datum * 3;
            }
        });
    }


    @Override
    protected Class<?> getStyleClass() {
        return CompanyStatusGraph.class;
    }
}
