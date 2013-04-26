package com.group7.dragonwars.engine;

public final class BasicMapInfo {
    private String name;
    private String desc;
    private String path;
    private int players;

    public BasicMapInfo(final String name, final String desc,
                        final String path, final int players) {
        this.name = name;
        this.setDesc(desc);
        this.path = path;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(final int players) {
        this.players = players;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(final String desc) {
        this.desc = desc;
    }
}
