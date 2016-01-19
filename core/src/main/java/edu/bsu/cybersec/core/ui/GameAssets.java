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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import playn.core.Assets;
import playn.core.Image;
import playn.core.Tile;
import react.RFuture;
import react.RPromise;
import react.Slot;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class GameAssets {

    public enum TileKey {
        BACKGROUND_1("employee_bg_1.png"),
        BACKGROUND_2("employee_bg_2.png"),
        BACKGROUND_4("employee_bg_4.png"),
        NARRATIVE_BACKGROUND_1("crop-1.png"),
        NARRATIVE_BACKGROUND_2("crop-2.png"),
        NARRATIVE_BACKGROUND_3("crop-3.png"),
        COMPANY_LOGO_WITH_ALPHA("company_logo.png");

        private final String path;

        TileKey(String name) {
            this.path = "images/" + checkNotNull(name);
        }
    }

    public enum ImageKey {
        ADMIN("admin.png"),
        STATUS("status.png"),
        NEWS("news.png"),
        NEWS_ATTENTION("news-attention.png"),
        DEVELOPMENT("development.png"),
        MAINTENANCE("maintenance.png"),
        LOGO("logo.png"),
        MUTE("mute.png"),
        UNMUTE("unmute.png"),
        DESK("desk.png"),
        ESTEBAN("Esteban.png"),
        MELISSA("Melissa.png"),
        NANCY("Nancy.png"),
        JERRY("Jerry.png"),
        VANI("Vani.png"),
        ABDULLAH("Abdullah.png"),
        JANINE("Janine.png"),
        IVAR("Ivar.png");

        private final String path;

        ImageKey(String name) {
            this.path = "images/" + checkNotNull(name);
        }
    }

    private final Assets assets;

    private Map<TileKey, Tile> tileCache = Maps.newEnumMap(TileKey.class);
    private Map<ImageKey, Image> imageCache = Maps.newEnumMap(ImageKey.class);

    public GameAssets(Assets assets) {
        this.assets = checkNotNull(assets);
    }

    public List<RFuture<Image>> cache(ImageKey... keys) {
        final List<RFuture<Image>> list = Lists.newArrayListWithCapacity(ImageKey.values().length);
        for (final ImageKey key : keys) {
            final RPromise<Image> promise = RPromise.create();
            Image image = assets.getImage(key.path);
            image.state.onSuccess(new Slot<Image>() {
                @Override
                public void onEmit(Image image) {
                    imageCache.put(key, image);
                    promise.succeed(image);
                }
            });
            image.state.onFailure(new Slot<Throwable>() {
                @Override
                public void onEmit(Throwable throwable) {
                    promise.fail(throwable);
                }
            });
            list.add(promise);
        }
        return list;
    }

    public List<RFuture<Tile>> cache(TileKey... keys) {
        final List<RFuture<Tile>> list = Lists.newArrayListWithCapacity(TileKey.values().length);
        for (final TileKey key : keys) {
            final RPromise<Tile> promise = RPromise.create();
            Image image = assets.getImage(key.path);
            image.state.onSuccess(new Slot<Image>() {
                @Override
                public void onEmit(Image image) {
                    RFuture<Tile> future = image.tileAsync();
                    future.onSuccess(new Slot<Tile>() {
                        @Override
                        public void onEmit(Tile tile) {
                            tileCache.put(key, tile);
                            promise.succeed(tile);
                        }
                    });
                    future.onFailure(new Slot<Throwable>() {
                        @Override
                        public void onEmit(Throwable throwable) {
                            promise.fail(throwable);
                        }
                    });
                }
            });
            image.state.onFailure(new Slot<Throwable>() {
                @Override
                public void onEmit(Throwable throwable) {
                    promise.fail(throwable);
                }
            });
            list.add(promise);
        }
        return list;
    }

    public Tile getTile(TileKey key) {
        Tile tile = tileCache.get(key);
        if (tile == null) {
            throw new UnsupportedOperationException("Cannot currently get tiles that are not cached (attempted to get " + key + ")");
        }
        return tile;
    }

    public Image getImage(ImageKey key) {
        Image image = imageCache.get(key);
        if (image == null) {
            throw new UnsupportedOperationException("Cannot currently get images that are not cached (attempted to get " + key + ")");
        }
        return image;
    }

}
