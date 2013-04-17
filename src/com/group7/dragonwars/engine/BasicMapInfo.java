package com.group7.dragonwars.engine;

public class BasicMapInfo {
    private String name;
    private String path;
    private int players;
    
    public BasicMapInfo(String name, String path, int players) {
        this.name = name;
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
}
