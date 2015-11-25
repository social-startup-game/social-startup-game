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

import static com.google.common.base.Preconditions.checkArgument;

public final class SizeConstrainedSmoothingList {
    public static SizeConstrainedSmoothingList withCapacity(int capacity) {
        return new SizeConstrainedSmoothingList(capacity);
    }

    private final int capacity;
    private final int[] list;
    private int size = 0;

    private SizeConstrainedSmoothingList(int capacity) {
        checkArgument(capacity > 0);
        this.capacity = capacity;
        list = new int[capacity];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void add(int i) {
        if (size == capacity) {
            shrinkAndSmooth();
        }
        list[size++] = i;
    }

    private void shrinkAndSmooth() {
        int[] replacement = new int[capacity / 2];
        for (int i = 0, limit = size / 2; i < limit; i++) {
            replacement[i] = (list[i * 2] + list[i * 2 + 1]) / 2;
        }
        System.arraycopy(replacement, 0, list, 0, replacement.length);
        size = replacement.length;
    }

    public int get(int index) {
        return list[index];
    }

    public int size() {
        return size;
    }
}
