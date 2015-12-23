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

package edu.bsu.cybersec.core.intro;

import edu.bsu.cybersec.core.GameBounds;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.ui.GameAssets;
import playn.core.Canvas;
import playn.core.Image;
import playn.core.TextFormat;
import playn.core.TextLayout;
import tripleplay.ui.*;
import tripleplay.ui.layout.AxisLayout;
import tripleplay.util.Colors;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class BossSlide implements Slide {

    private final Image desk = SimGame.game.assets.getImage(GameAssets.ImageKey.DESK);
    private final Image boss;
    private final String text;

    public BossSlide(String text, Image boss) {
        this.text = checkNotNull(text);
        this.boss = checkNotNull(boss);
    }

    public Group createUI() {
        return new SizableGroup(AxisLayout.vertical(), SimGame.game.bounds.width(), SimGame.game.bounds.height())
                .add(new Label(text)
                                .addStyles(Style.TEXT_WRAP.on,
                                        Style.FONT.is(FONT)),
                        imageGroup());
    }

    private Element imageGroup() {
        GameBounds bounds = SimGame.game.bounds;
        Canvas canvas = SimGame.game.plat.graphics().createCanvas(bounds.width(), bounds.height() / 2);
        drawBossAndDeskTo(canvas);
        drawCeoLabelTo(canvas);
        return new Label(Icons.image(canvas.image));
    }

    private void drawBossAndDeskTo(Canvas canvas) {
        final float bossScale = boss.height() / canvas.height;
        canvas.draw(boss, (canvas.width - boss.width() * bossScale) / 2f, 0, boss.width() * bossScale, canvas.height);
        canvas.draw(desk, 0, canvas.height / 2, canvas.width, canvas.height / 2);
    }

    private void drawCeoLabelTo(Canvas canvas) {
        TextFormat textFormat = new TextFormat(FONT);
        TextLayout textLayout = SimGame.game.plat.graphics().layoutText("CEO", textFormat);
        canvas.setFillColor(Colors.WHITE);
        canvas.fillText(textLayout, canvas.width / 2, canvas.height * 0.55f);
    }


}
