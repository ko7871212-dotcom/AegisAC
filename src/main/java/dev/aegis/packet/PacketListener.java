package dev.aegis.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.*;
import dev.aegis.AegisAC;
import dev.aegis.anticheat.checks.packet.BadPacketsCheck;
import dev.aegis.anticheat.checks.packet.TimerCheck;
import dev.aegis.core.PlayerData;
import org.bukkit.entity.Player;

public class PacketListener extends PacketAdapter {

    private final AegisAC plugin;
    private final BadPacketsCheck badPacketsCheck;
    private final TimerCheck timerCheck;

    public PacketListener(AegisAC plugin) {
        super(plugin, ListenerPriority.MONITOR,
                PacketType.Play.Client.POSITION,
                PacketType.Play.Client.POSITION_LOOK,
                PacketType.Play.Client.LOOK);
        this.plugin = plugin;
        this.badPacketsCheck = plugin.getCheckManager().getBadPacketsCheck();
        this.timerCheck = plugin.getCheckManager().getTimerCheck();
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        Player player = event.getPlayer();
        if (player == null || !player.isOnline()) return;
        if (player.hasPermission("aegis.bypass")) return;
        PlayerData data = plugin.getPlayerDataManager().getData(player);
        if (data == null) return;

        PacketContainer packet = event.getPacket();
        PacketType type = packet.getType();
        timerCheck.onPacket(player, data);

        if (type == PacketType.Play.Client.POSITION || type == PacketType.Play.Client.POSITION_LOOK) {
            double x = packet.getDoubles().read(0);
            double y = packet.getDoubles().read(1);
            double z = packet.getDoubles().read(2);
            badPacketsCheck.checkPosition(player, data, x, y, z);
        }
        if (type == PacketType.Play.Client.POSITION_LOOK || type == PacketType.Play.Client.LOOK) {
            float yaw = packet.getFloat().read(0);
            float pitch = packet.getFloat().read(1);
            badPacketsCheck.checkRotation(player, data, yaw, pitch);
            data.recordRotation(yaw, pitch);
        }
    }
}
