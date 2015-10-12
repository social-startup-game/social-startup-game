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

package edu.bsu.cybersec.robovm;

import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.apple.uikit.UIApplicationDelegateAdapter;
import org.robovm.apple.uikit.UIApplicationLaunchOptions;
import org.robovm.apple.uikit.UIInterfaceOrientationMask;
import org.robovm.apple.uikit.UIScreen;
import org.robovm.apple.uikit.UIWindow;

import playn.robovm.RoboPlatform;
import edu.bsu.cybersec.core.SimGame;

public class SimGameRoboVM extends UIApplicationDelegateAdapter {

    @Override
    public boolean didFinishLaunching(UIApplication app, UIApplicationLaunchOptions launchOpts) {
        // create a full-screen window
        CGRect bounds = UIScreen.getMainScreen().getBounds();
        UIWindow window = new UIWindow(bounds);

        // configure and create the PlayN platform
        RoboPlatform.Config config = new RoboPlatform.Config();
        config.orients = UIInterfaceOrientationMask.All;
        RoboPlatform plat = RoboPlatform.create(window, config);

        // create and initialize our game
        new SimGame(plat);

        // make our main window visible (this starts the platform)
        window.makeKeyAndVisible();
        addStrongRef(window);
        return true;
    }

    public static void main(String[] args) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(args, null, SimGameRoboVM.class);
        pool.close();
    }
}
