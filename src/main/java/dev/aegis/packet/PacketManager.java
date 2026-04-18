package dev.aegis.packet;

import com.comphenix.protocol.ProtocolLibrary;
import dev.aegis.AegisAC;

public class PacketManager {

    private final AegisAC plugin;
    private PacketListener listener;

    public PacketManager(AegisAC plugin) {
        this.plugin = plugin;
    }

    public void register() {
        listener = new PacketListener(plugin);
        ProtocolLibrary.getProtocolManager().addPacketListener(listener);
    }

    public void unregister() {
        if (listener != null)
            ProtocolLibrary.getProtocolManager().removePacketListener(listener);
    }
}
