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

package edu.bsu.cybersec.java;

import edu.bsu.cybersec.core.SimGame;
import org.apache.commons.cli.*;
import playn.java.JavaPlatform;
import playn.java.LWJGLPlatform;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;

import java.awt.*;

public class SimGameJava {

    private static final IDimension DEFAULT_SIZE = new Dimension(640, 960);
    private static Dimension size = new Dimension(DEFAULT_SIZE.width(), DEFAULT_SIZE.height());
    private static JavaGameConfig gameConfig;

    public static void main(String[] args) {
        gameConfig = new JavaGameConfig();
        new Parser().process(args);
        LWJGLPlatform plat = new LWJGLPlatform(makeLWJGLConfig());
        gameConfig.setPlatform(plat);
        registerFont(plat);
        new SimGame(plat, gameConfig);
        plat.start();
    }

    private static JavaPlatform.Config makeLWJGLConfig() {
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
        config.width = size.width();
        config.height = size.height();
        return config;
    }

    private static void registerFont(JavaPlatform plat) {
        try {
            Font font = plat.assets().getFont("fonts/Lato-Regular.ttf");
            plat.graphics().registerFont("Lato-Regular", font);
        } catch (Exception e) {
            plat.log().error("Failed to load font", e);
        }
    }

    private static final class Parser extends BasicParser {
        private static final String SIZE_OPTION_NAME = "size";
        private static final String SKIP_INTRO_OPTION = "skipIntro";
        private static final String SKIP_WELCOME_OPTION = "skipWelcome";
        private static final String SKIP_NARRATIVE = "skipNarrative";
        private static final String MUTE_MUSIC = "mute";
        private static final String SHOW_CONSENT = "consent";
        private Options options = createOptions();

        @SuppressWarnings("static-access") // Static access required through CLI API
        private static Options createOptions() {
            Options options = new Options();
            options.addOption(OptionBuilder
                    .withLongOpt(SIZE_OPTION_NAME)
                    .withDescription("window size")
                    .hasArg()
                    .withArgName("<w>x<h>")
                    .create());
            options.addOption(SKIP_INTRO_OPTION, false, "skip introduction");
            options.addOption(SKIP_WELCOME_OPTION, false, "skip Frieda's welcome narrative");
            options.addOption(SKIP_NARRATIVE, false, "skip all narrative events");
            options.addOption(MUTE_MUSIC, false, "mute music");
            options.addOption(SHOW_CONSENT, false, "show consent form");
            return options;
        }

        public void process(String[] args) {
            CommandLine line;
            try {
                line = parse(options, args);
            } catch (ParseException e) {
                System.err.println("Parsing failed. Reason: " + e.getMessage());
                return;
            }
            if (line.hasOption(SIZE_OPTION_NAME)) {
                processSizeOption(line);
            }
            if (line.hasOption(SKIP_INTRO_OPTION)) {
                gameConfig.skipIntro.update(true);
            }
            if (line.hasOption(SKIP_WELCOME_OPTION)) {
                gameConfig.skipWelcome.update(true);
            }
            if (line.hasOption(SKIP_NARRATIVE)) {
                gameConfig.useNarrativeEvents.update(false);
            }
            if (line.hasOption(MUTE_MUSIC)) {
                gameConfig.muteMusic.update(true);
            }
            if (line.hasOption(SHOW_CONSENT)) {
                gameConfig.showConsent.update(true);
            }
        }

        private static void processSizeOption(CommandLine line) {
            String sizeString = line.getOptionValue(SIZE_OPTION_NAME);
            String[] separated = sizeString.split("x");
            if (separated.length != 2) {
                System.err.println("Illegal size expression, must be in form WxH: " + sizeString);
            } else {
                int w = Integer.valueOf(separated[0]);
                int h = Integer.valueOf(separated[1]);
                size.width = w;
                size.height = h;
            }
        }
    }
}
