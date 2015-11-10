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

package edu.bsu.cybersec.java;

import edu.bsu.cybersec.core.SimGame;
import org.apache.commons.cli.*;
import playn.java.JavaPlatform;
import playn.java.LWJGLPlatform;
import pythagoras.i.Dimension;
import pythagoras.i.IDimension;

public class SimGameJava {

    private static final IDimension DEFAULT_SIZE = new Dimension(640, 960);
    private static boolean skipIntro = false;
    private static boolean skipWelcome = false;
    private static Dimension size = new Dimension(DEFAULT_SIZE.width(), DEFAULT_SIZE.height());

    public static void main(String[] args) {
        new Parser().process(args);
        LWJGLPlatform plat = new LWJGLPlatform(makeLWJGLConfig());
        plat.graphics().registerFont("Lato-Regular", "fonts/Lato-Regular.ttf");
        new SimGame(plat, new JavaGameConfig(skipIntro, skipWelcome));
        plat.start();
    }

    private static JavaPlatform.Config makeLWJGLConfig() {
        LWJGLPlatform.Config config = new LWJGLPlatform.Config();
        config.width = size.width();
        config.height = size.height();
        return config;
    }

    private static final class Parser extends BasicParser {
        private static final String SIZE_OPTION_NAME = "size";
        private static final String SKIP_INTRO_OPTION = "skipIntro";
        private static final String SKIP_WELCOME_OPTION = "skipWelcome";
        private CommandLine line;
        private Options options = createOptions();

        @SuppressWarnings("static-access") // Static access required through CLI API
        private static Options createOptions() {
            Option sizeOption = OptionBuilder.withArgName(SIZE_OPTION_NAME)
                    .hasArg()
                    .withDescription("specify game screen size")
                    .create(SIZE_OPTION_NAME);
            Option skipIntroOption = OptionBuilder.withArgName(SKIP_INTRO_OPTION)
                    .withDescription("skip the game introduction")
                    .create(SKIP_INTRO_OPTION);
            Option skipWelcomeOption = OptionBuilder.withArgName(SKIP_WELCOME_OPTION)
                    .withDescription("skip the administrative coordinator's welcome event")
                    .create(SKIP_WELCOME_OPTION);
            Options options = new Options();
            options.addOption(sizeOption);
            options.addOption(skipIntroOption);
            options.addOption(skipWelcomeOption);
            return options;
        }

        public void process(String[] args) {
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
                skipIntro = true;
            }
            if (line.hasOption(SKIP_WELCOME_OPTION)) {
                skipWelcome = true;
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
