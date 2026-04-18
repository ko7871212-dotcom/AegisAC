package dev.aegis.core;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerData {

    private final UUID uuid;
    private final String name;

    private Location lastLocation;
    private Location currentLocation;
    private double lastDeltaY;
    private double lastSpeed;
    private int airTicks;
    private int groundTicks;
    private boolean onGround;
    private boolean lastOnGround;
    private double deltaX, deltaY, deltaZ;
    private Location predictedLocation;

    private long lastAttackTime;
    private List<Long> clickTimes = new ArrayList<>();
    private double cps;
    private float lastYaw, lastPitch;
    private float currentYaw, currentPitch;
    private List<float[]> rotationHistory = new ArrayList<>();

    private final Map<String, Integer> violations = new ConcurrentHashMap<>();
    private int totalViolations;
    private int trustScore = 50;
    private int ping;
    private long joinTime;
    private boolean flying;
    private boolean inVehicle;
    private boolean inLiquid;
    private boolean onIce;
    private boolean onSlime;
    private boolean hasVelocity;
    private long lastVelocityTime;

    private final LinkedList<Location> positionBuffer = new LinkedList<>();
    private double avgCPS;
    private double avgSpeed;
    private int sessionTicks;

    public PlayerData(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.joinTime = System.currentTimeMillis();
        this.lastLocation = player.getLocation().clone();
        this.currentLocation = player.getLocation().clone();
    }

    public void updateLocation(Location newLocation, boolean isOnGround) {
        this.lastLocation = this.currentLocation.clone();
        this.currentLocation = newLocation.clone();
        this.lastOnGround = this.onGround;
        this.onGround = isOnGround;
        this.deltaX = newLocation.getX() - lastLocation.getX();
        this.deltaY = newLocation.getY() - lastLocation.getY();
        this.deltaZ = newLocation.getZ() - lastLocation.getZ();
        this.lastSpeed = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        if (!isOnGround) { airTicks++; groundTicks = 0; }
        else { groundTicks++; airTicks = 0; }
        this.lastDeltaY = deltaY;
        positionBuffer.add(newLocation.clone());
        if (positionBuffer.size() > 400) positionBuffer.removeFirst();
    }

    public void recordRotation(float yaw, float pitch) {
        this.lastYaw = this.currentYaw;
        this.lastPitch = this.currentPitch;
        this.currentYaw = yaw;
        this.currentPitch = pitch;
        rotationHistory.add(new float[]{yaw, pitch});
        if (rotationHistory.size() > 40) rotationHistory.remove(0);
    }

    public void recordClick() {
        long now = System.currentTimeMillis();
        clickTimes.add(now);
        clickTimes.removeIf(t -> now - t > 1000);
        this.cps = clickTimes.size();
    }

    public int getViolation(String check) { return violations.getOrDefault(check, 0); }

    public int addViolation(String check, int amount) {
        int current = violations.getOrDefault(check, 0) + amount;
        violations.put(check, current);
        totalViolations += amount;
        return current;
    }

    public void decayViolation(String check, int amount) {
        violations.put(check, Math.max(0, violations.getOrDefault(check, 0) - amount));
    }

    public void decayAll() {
        violations.replaceAll((k, v) -> Math.max(0, v - 1));
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public Location getLastLocation() { return lastLocation; }
    public Location getCurrentLocation() { return currentLocation; }
    public double getDeltaX() { return deltaX; }
    public double getDeltaY() { return deltaY; }
    public double getDeltaZ() { return deltaZ; }
    public double getLastSpeed() { return lastSpeed; }
    public double getLastDeltaY() { return lastDeltaY; }
    public int getAirTicks() { return airTicks; }
    public int getGroundTicks() { return groundTicks; }
    public boolean isOnGround() { return onGround; }
    public boolean wasOnGround() { return lastOnGround; }
    public long getLastAttackTime() { return lastAttackTime; }
    public void setLastAttackTime(long t) { this.lastAttackTime = t; }
    public double getCps() { return cps; }
    public float getLastYaw() { return lastYaw; }
    public float getLastPitch() { return lastPitch; }
    public float getCurrentYaw() { return currentYaw; }
    public float getCurrentPitch() { return currentPitch; }
    public List<float[]> getRotationHistory() { return rotationHistory; }
    public Map<String, Integer> getViolations() { return violations; }
    public int getTotalViolations() { return totalViolations; }
    public int getTrustScore() { return trustScore; }
    public void setTrustScore(int score) { this.trustScore = Math.max(0, Math.min(100, score)); }
    public int getPing() { return ping; }
    public void setPing(int ping) { this.ping = ping; }
    public boolean isFlying() { return flying; }
    public void setFlying(boolean flying) { this.flying = flying; }
    public boolean isInVehicle() { return inVehicle; }
    public void setInVehicle(boolean v) { this.inVehicle = v; }
    public boolean isInLiquid() { return inLiquid; }
    public void setInLiquid(boolean v) { this.inLiquid = v; }
    public boolean isOnIce() { return onIce; }
    public void setOnIce(boolean v) { this.onIce = v; }
    public boolean isOnSlime() { return onSlime; }
    public void setOnSlime(boolean v) { this.onSlime = v; }
    public boolean isHasVelocity() { return hasVelocity; }
    public void setHasVelocity(boolean v) { this.hasVelocity = v; }
    public long getLastVelocityTime() { return lastVelocityTime; }
    public void setLastVelocityTime(long t) { this.lastVelocityTime = t; }
    public LinkedList<Location> getPositionBuffer() { return positionBuffer; }
    public Location getPredictedLocation() { return predictedLocation; }
    public void setPredictedLocation(Location loc) { this.predictedLocation = loc; }
    public long getJoinTime() { return joinTime; }
    public double getAvgCPS() { return avgCPS; }
    public void setAvgCPS(double v) { this.avgCPS = v; }
    public double getAvgSpeed() { return avgSpeed; }
    public void setAvgSpeed(double v) { this.avgSpeed = v; }
    public int getSessionTicks() { return sessionTicks; }
    public void incrementSessionTicks() { this.sessionTicks++; }
}
