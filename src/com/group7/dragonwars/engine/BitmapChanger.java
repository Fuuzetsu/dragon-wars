package com.group7.dragonwars.engine;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BitmapChanger {

    private BitmapChanger() {}

    public static Bitmap changeColour(final Bitmap templateBitmap,
                                      final Integer originalColour,
                                      final Integer replacementColour) {
        if (replacementColour == 0) {
            return templateBitmap;
        }

        Bitmap replacement = templateBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Integer allPxs = replacement.getHeight() * replacement.getWidth();

        int[] allpixels = new int[allPxs];

        replacement.getPixels(allpixels, 0, replacement.getWidth(), 0, 0,
                              replacement.getWidth(),replacement.getHeight());

        for (Integer i = 0; i < allPxs; i++) {
            if (allpixels[i] == originalColour) {
                allpixels[i] = replacementColour;
            }
        }

        replacement.setPixels(allpixels, 0, replacement.getWidth(), 0, 0,
                              replacement.getWidth(), replacement.getHeight());

        return replacement;
    }

    public static Bitmap combineMap(final GameMap map, final Integer tilesize,
                                    final Map<String, Map<String, Bitmap>> graphics) {
        Bitmap result = null;
        Integer width = map.getWidth() * tilesize;
        Integer height = map.getHeight() * tilesize;

        result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas combined = new Canvas(result);

        for (int i = 0; i < map.getWidth(); ++i) {
            for (int j = 0; j < map.getHeight(); j++) {
                Position pos = new Position(i, j);
                GameField gf = map.getField(i, j);
                String gfn = gf.getName();
                RectF dest = new RectF(tilesize * i, tilesize * j, tilesize * i + tilesize,
                                       tilesize * j + tilesize);
                combined.drawBitmap(graphics.get("Fields").get(gfn),
                                    null, dest, null);

                BitmapChanger.drawBorder(map, combined, pos, dest, graphics);

                if (gf.hostsBuilding()) {
                    Building b = gf.getBuilding();
                    String n = b.getName();
                    combined.drawBitmap(graphics.get("Buildings").get(n),
                                        null, dest, null);
                }
            }
        }

        return result;
    }

    private static void drawBorder(final GameMap map, final Canvas canvas, final Position currentField,
                                   final RectF dest, final Map<String, Map<String, Bitmap>> graphics) {
        /* TODO not hard-code this */
        Integer i = currentField.getX();
        Integer j = currentField.getY();

        final String FAKE = "NoTaReAlTiL3";

        String gfn = map.getField(currentField).getSpriteLocation();

        Position nep, np, nwp, ep, cp, wp, sep, sp, swp;
        String ne, n, nw, e, c, w, se, s, sw;

        nep = new Position(i + 1, j - 1);
        np = new Position(i, j - 1);
        nwp = new Position(i - 1, j - 1);

        ep = new Position(i + 1, j);
        cp = new Position(i, j);
        wp = new Position(i - 1, j);

        sep = new Position(i + 1, j + 1);
        sp = new Position(i, j + 1);
        swp = new Position(i - 1, j + 1);

        ne = map.isValidField(nep) ? map.getField(nep).getSpriteLocation() : FAKE;
        n = map.isValidField(np) ? map.getField(np).getSpriteLocation() : FAKE;
        nw = map.isValidField(nwp) ? map.getField(nwp).getSpriteLocation() : FAKE;

        e = map.isValidField(ep) ? map.getField(ep).getSpriteLocation() : FAKE;
        c = map.isValidField(cp) ? map.getField(cp).getSpriteLocation() : FAKE;
        w = map.isValidField(wp) ? map.getField(wp).getSpriteLocation() : FAKE;

        se = map.isValidField(sep) ? map.getField(sep).getSpriteLocation() : FAKE;
        s = map.isValidField(sp) ? map.getField(sp).getSpriteLocation() : FAKE;
        sw = map.isValidField(swp) ? map.getField(swp).getSpriteLocation() : FAKE;

        Func<String, Void> drawer = new Func<String, Void>() {
            public Void apply(String sprite) {
                canvas.drawBitmap(graphics.get("Fields").get(sprite),
                                  null, dest, null);
                return null; /* Java strikes again */
            }
        };
        List<String> land = new ArrayList<String>();
        land.add("grass");
        land.add("sand");
        List<String> liquid = new ArrayList<String>();
        liquid.add("water");
        liquid.add("lava");
        liquid.add("bridge_horizontal");
        liquid.add("bridge_vertical");

        if (liquid.contains(gfn)) {

            if (land.contains(s)) {
                drawer.apply(String.format("border %s %d", s, 1));
            }

            if (land.contains(e)) {
                drawer.apply(String.format("border %s %d", e, 2));
            }

            if (land.contains(n)) {
                drawer.apply(String.format("border %s %d", n, 3));
            }

            if (land.contains(w)) {
                drawer.apply(String.format("border %s %d", w, 4));
            }

            if (liquid.contains(s) && liquid.contains(e) && land.contains(se)) {
                drawer.apply(String.format("corner %s %d", se, 1));
            }

            if (liquid.contains(n) && liquid.contains(e) && land.contains(ne)) {
                drawer.apply(String.format("corner %s %d", ne, 2));
            }

            if (liquid.contains(n) && liquid.contains(w) && land.contains(nw)) {
                drawer.apply(String.format("corner %s %d", nw, 3));
            }

            if (liquid.contains(s) && liquid.contains(w) && land.contains(sw)) {
                drawer.apply(String.format("corner %s %d", sw, 4));
            }

            if (land.contains(s) && land.contains(e) && land.contains(se)) {
                if (s.equals(e) && e.equals(se)) {
                    drawer.apply(String.format("fullcorner %s %d", s, 1));
                }
            }

            if (land.contains(n) && land.contains(e) && land.contains(ne)) {
                if (n.equals(e) && e.equals(ne)) {
                    drawer.apply(String.format("fullcorner %s %d", n, 2));
                }
            }

            if (land.contains(n) && land.contains(w) && land.contains(nw)) {
                if (n.equals(w) && w.equals(nw)) {
                    drawer.apply(String.format("fullcorner %s %d", n, 3));
                }
            }

            if (land.contains(s) && land.contains(w) && land.contains(sw)) {
                if (s.equals(w) && w.equals(sw)) {
                    drawer.apply(String.format("fullcorner %s %d", s, 4));
                }
            }

        }
    }

}
