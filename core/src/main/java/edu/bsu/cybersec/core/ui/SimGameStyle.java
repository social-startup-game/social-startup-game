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

import playn.core.Font;
import playn.core.Graphics;
import tripleplay.ui.*;

/**
 * Style sheet generator for this game.
 * <p/>
 * Based on TriplePlay's SimpleStyles class.
 *
 * @see <a href="https://github.com/threerings/tripleplay/blob/master/core/src/main/java/tripleplay/ui/SimpleStyles.java">SimpleStyles</a>
 */
public class SimGameStyle {

    public static final int UL_COLOR = 0xFFEEEEEE;

    public static Stylesheet newSheet(Graphics gfx) {
        return newSheetBuilder(gfx).create();
    }

    /**
     * Creates and returns a stylesheet builder configured with some useful default styles. The
     * caller can augment the sheet with additional styles and call {@code create}.
     */
    public static Stylesheet.Builder newSheetBuilder(Graphics gfx) {
        final Font font = FontCache.instance().REGULAR;
        int bgColor = 0xFFCCCCCC, ulColor = UL_COLOR, brColor = 0xFFAAAAAA;
        Background butBg = Background.roundRect(gfx, bgColor, 5, ulColor, 2).inset(5, 6, 2, 6);
        Background butSelBg = Background.roundRect(gfx, bgColor, 5, brColor, 2).inset(6, 5, 1, 7);
        Background disabledChangeViewButtonBackground = Background.roundRect(gfx, Palette.DIALOG_BACKGROUND, 5, ulColor, 2)
                .inset(5, 6, 2, 6);
        return Stylesheet.builder()
                .add(Button.class,
                        Style.BACKGROUND.is(butBg),
                        Style.FONT.is(font))
                .add(Button.class, Style.Mode.SELECTED,
                        Style.BACKGROUND.is(butSelBg))
                .add(Button.class,
                        Style.ACTION_SOUND.is(SfxCache.instance().CLICK))
                .add(ToggleButton.class,
                        Style.BACKGROUND.is(butBg))
                .add(ToggleButton.class, Style.Mode.SELECTED,
                        Style.BACKGROUND.is(butSelBg))
                .add(CheckBox.class,
                        Style.BACKGROUND.is(Background.roundRect(gfx, bgColor, 5, ulColor, 2).
                                inset(3, 2, 0, 3)))
                .add(CheckBox.class, Style.Mode.SELECTED,
                        Style.BACKGROUND.is(Background.roundRect(gfx, bgColor, 5, brColor, 2).
                                inset(3, 2, 0, 3)))
                // flip ul and br to make Field appear recessed
                .add(Field.class,
                        Style.BACKGROUND.is(Background.beveled(0xFFFFFFFF, brColor, ulColor).inset(5)),
                        Style.HALIGN.left)
                .add(Field.class, Style.Mode.DISABLED,
                        Style.BACKGROUND.is(Background.beveled(0xFFCCCCCC, brColor, ulColor).inset(5)))
                .add(Menu.class,
                        Style.BACKGROUND.is(Background.bordered(0xFFFFFFFF, 0x00000000, 1).inset(6)))
                .add(MenuItem.class,
                        Style.BACKGROUND.is(Background.solid(0xFFFFFFFF)),
                        Style.HALIGN.left,
                        Style.FONT.is(font))
                .add(MenuItem.class, Style.Mode.SELECTED,
                        Style.BACKGROUND.is(Background.solid(0xFF000000)),
                        Style.COLOR.is(0xFFFFFFFF))
                .add(Tabs.class,
                        Tabs.HIGHLIGHTER.is(Tabs.textColorHighlighter(0xFF000000, 0xFFFFFFFF)))
                .add(Label.class,
                        Style.FONT.is(font))
                .add(GameInteractionArea.ChangeViewControl.ChangeViewButton.class,
                        Style.BACKGROUND.is(butBg),
                        Style.FONT.is(font))
                .add(GameInteractionArea.ChangeViewControl.ChangeViewButton.class, Style.Mode.SELECTED,
                        Style.BACKGROUND.is(butSelBg))
                .add(GameInteractionArea.ChangeViewControl.ChangeViewButton.class, Style.Mode.DISABLED,
                        Style.BACKGROUND.is(disabledChangeViewButtonBackground))
                .add(GameInteractionArea.ChangeViewControl.ChangeViewButton.class,
                        Style.TEXT_EFFECT.pixelOutline)
                .add(GameInteractionArea.ChangeViewControl.ChangeViewButton.class,
                        Style.HIGHLIGHT.is(Palette.UNUSED_SPACE))
                .add(GameInteractionArea.ChangeViewControl.ChangeViewButton.class,
                        Style.COLOR.is(Palette.FOREGROUND))
                .add(GameInteractionArea.ChangeViewControl.ChangeViewButton.class,
                        Style.ACTION_SOUND.is(SfxCache.instance().CLICK))
                .add(GameInteractionArea.ChangeViewControl.CountLabel.class,
                        Style.FONT.is(font.derive(font.size * 0.85f)),
                        Style.COLOR.is(GameColors.WHITE),
                        Style.TEXT_EFFECT.pixelOutline,
                        Style.HIGHLIGHT.is(GameColors.HUNTER_GREEN));

    }
}
