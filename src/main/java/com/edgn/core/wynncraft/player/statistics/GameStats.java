package com.edgn.core.wynncraft.player.statistics;

import java.util.Map;

public class GameStats {
    private int total;
    private Map<String, Integer> list;

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    public Map<String, Integer> getList() { return list; }
    public void setList(Map<String, Integer> list) { this.list = list; }
}