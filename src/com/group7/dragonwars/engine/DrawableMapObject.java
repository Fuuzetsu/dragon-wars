/* This file is part of Dragon Wars.
 *
 * Dragon Wars is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dragon Wars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Dragon Wars.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.group7.dragonwars.engine;

public abstract class DrawableMapObject {

    private String name;
    private String spriteLocation;
    private String spriteDir;
    private String spritePack;
    protected String info;

    public DrawableMapObject(final String name, final String spriteLocation,
                             final String spriteDir, final String spritePack) {
        this.name = name;
        this.spriteLocation = spriteLocation;
        this.spriteDir = spriteDir;
        this.spritePack = spritePack;
    }

    public final String getName() {
        return name;
    }

    public final String getSpriteLocation() {
        return spriteLocation;
    }

    public final String getSpriteDir() {
        return spriteDir;
    }

    public final String getSpritePack() {
        return spritePack;
    }

    protected final void setInfo(final String newInfo) {
        info = newInfo;
    }

    public String getInfo() {
        return this.info;
    }

    /* Used to enforce info creation from extending classes */
    public abstract void generateInfo();

}
