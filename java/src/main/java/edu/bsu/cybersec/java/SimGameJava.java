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
import playn.java.LWJGLPlatform;

public class SimGameJava {

    private static CustomLWJGLPlatformConfig config = new CustomLWJGLPlatformConfig();

    public static void main(String[] args) {
        new Parser().process(args);
        LWJGLPlatform plat = new SimGameJavaPlatform(config);
        plat.graphics().registerFont("Lato-Regular", "fonts/Lato-Regular.ttf");
        new SimGame(plat);
        plat.start();
    }

    private static final class Parser extends BasicParser {
        private static final String SIZE_OPTION_NAME = "size";
        private static final String SKIP_INTRO_OPTION = "skipIntro";
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
            Options options = new Options();
            options.addOption(sizeOption);
            options.addOption(skipIntroOption);
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
                config.skipIntro = true;
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
                config.width = w;
                config.height = h;
            }
        }
    }
}
