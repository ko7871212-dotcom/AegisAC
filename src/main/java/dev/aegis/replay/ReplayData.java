package dev.aegis.replay;

import org.bukkit.Location;
import java.util.LinkedList;

public class ReplayData {

    private final String playerName;
    private final LinkedList<Location> positions;
    private final long recordedAt;

    public ReplayData(String playerName, LinkedList<Location> positions) {
        this.playerName = playerName;
        this.positions = new LinkedList<>(positions);
        this.recordedAt = System.currentTimeMillis();
    }

    public String getPlayerName() { return playerName; }
    public LinkedList<Location> getPositions() { return positions; }
    public long getRecordedAt() { return recordedAt; }
}
