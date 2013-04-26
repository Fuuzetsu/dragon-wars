package com.group7.dragonwars.engine;

public abstract class DrawableMapObject {

    private String name;
    private String spriteLocation;
    private String spriteDir;
    private String spritePack;
    private String info;

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
