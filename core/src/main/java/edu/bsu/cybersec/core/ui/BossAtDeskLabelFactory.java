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

package edu.bsu.cybersec.core.ui;

import edu.bsu.cybersec.core.GameBounds;
import edu.bsu.cybersec.core.SimGame;
import playn.core.Canvas;
import playn.core.Image;
import tripleplay.ui.Element;
import tripleplay.ui.Icons;
import tripleplay.ui.Label;

import static com.google.common.base.Preconditions.checkNotNull;

public final class BossAtDeskLabelFactory {

    /**
     * Empirically-determined scale for getting the boss to look reasonable on an intro-style slide.
     */
    private static final float RELATIVE_BOSS_SCALE = 0.8f;

    public static Element create(Image boss) {
        return new BossAtDeskLabelFactory(boss).imageGroup();
    }

    private final Image desk = SimGame.game.assets.getImage(GameAssets.ImageKey.DESK);
    private final Image boss;

    private BossAtDeskLabelFactory(Image boss) {
        this.boss = checkNotNull(boss);
    }

    private Element imageGroup() {
        GameBounds bounds = SimGame.game.bounds;
        Canvas canvas = SimGame.game.plat.graphics().createCanvas(bounds.width(), bounds.height() / 2);
        drawBossAndDeskTo(canvas);
        return new Label(Icons.image(canvas.image));
    }

    private void drawBossAndDeskTo(Canvas canvas) {
        final float bossScale = canvas.height / boss.height();
        final float bossScaledWidth = boss.width() * bossScale * RELATIVE_BOSS_SCALE;
        canvas.draw(boss, (canvas.width - bossScaledWidth) / 2f, 0,
                bossScaledWidth, canvas.height * RELATIVE_BOSS_SCALE);
        canvas.draw(desk, 0, canvas.height * 0.4f, canvas.width, canvas.height * 0.5f);
    }
}
