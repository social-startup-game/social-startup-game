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

import edu.bsu.cybersec.core.Company;
import edu.bsu.cybersec.core.DecimalTruncator;
import edu.bsu.cybersec.core.SimGame;
import edu.bsu.cybersec.core.ui.GameAssets;

public final class Introduction {

    private final Company company;

    public Introduction(Company company) {
        this.company = company;
    }

    public Slide createSlides() {
        GameAssets assets = SimGame.game.assets;
        return new TextAndImageSlide("Social Jam is an up and coming social media service, and they have hired you as their chief security advisor.",
                assets.getTile(GameAssets.TileKey.NARRATIVE_BACKGROUND_1)) {
            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Slide next() {
                return new TextAndImageSlide("You were hired to manage a team of developers and protect the company from hackers! ",
                        SimGame.game.assets.getTile(GameAssets.TileKey.NARRATIVE_BACKGROUND_2)) {
                    @Override
                    public boolean hasNext() {
                        return true;
                    }

                    @Override
                    public Slide next() {
                        return new TextAndImageSlide("You have three employees. Assign them to develop features or maintain your current system.",
                                SimGame.game.assets.getTile(GameAssets.TileKey.NARRATIVE_BACKGROUND_3)) {
                            @Override
                            public boolean hasNext() {
                                return true;
                            }

                            @Override
                            public Slide next() {
                                return new BossSlide("You have a job review in two weeks, and I expect us to have " + company.goal.minimumUsers
                                        + " users by then. Make sure that we stay safe and our exposure doesn't go above "
                                        + new DecimalTruncator(0).makeTruncatedString(company.goal.maximumExposure * 100)
                                        + "%. Do you have what it takes?",
                                        company.boss.image) {
                                    @Override
                                    public boolean hasNext() {
                                        return false;
                                    }

                                    @Override
                                    public Slide next() {
                                        throw new UnsupportedOperationException();
                                    }
                                };
                            }
                        };
                    }
                };
            }
        };
    }
}
