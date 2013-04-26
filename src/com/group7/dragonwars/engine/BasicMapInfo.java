package com.group7.dragonwars.engine;

public class BasicMapInfo {
    private String name;
    private String desc;
    private String path;
    private int players;

    public BasicMapInfo(String name, String desc, String path, int players) {
        this.name = name;
        this.setDesc(desc);
        this.path = path;
        this.players = players;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getPlayers() {
        return players;
    }

    public void setPlayers(int players) {
        this.players = players;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
