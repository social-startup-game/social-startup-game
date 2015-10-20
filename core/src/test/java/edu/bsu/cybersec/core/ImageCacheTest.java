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

package edu.bsu.cybersec.core;

import edu.bsu.cybersec.core.ui.ImageCache;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import playn.core.Assets;
import playn.core.Image;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageCacheTest {

    @Before
    public void setUp() {
        Assets assets = mock(Assets.class);
        when(assets.getImage(Matchers.anyString())).thenReturn(mock(Image.class));
        ImageCache.initialize(assets);
    }

    @After
    public void tearDown() {
        ImageCache.deinitialize();
    }


    @Test
    public void testValues_containsAllConstants() {
        Field[] fields = ImageCache.class.getFields();
        int count = 0;
        for (Field f : fields) {
            if (f.getType().getSimpleName().equals("Image")) {
                count++;
            }
        }
        assertEquals(count, ImageCache.instance().all().size());
    }
}
