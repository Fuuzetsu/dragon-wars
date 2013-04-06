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
    public String getName() {
        return name;
    }

    public String getSpriteLocation() {
        return spriteLocation;
    }

    public String getSpriteDir() {
        return spriteDir;
    }

    public String getSpritePack() {
        return spritePack;
    }

    public String getInfo() {
        return info;
    }

    /* Used to enforce info creation from extending classes */
    public abstract void generateInfo();

}