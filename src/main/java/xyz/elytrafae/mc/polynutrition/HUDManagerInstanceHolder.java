package xyz.elytrafae.mc.polynutrition;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.UUID;

public class HUDManagerInstanceHolder {

    private static HashMap<UUID, HUDManager> playerManagerInstances = new HashMap<>();

    public static void onJoin(ServerPlayer player) {
        if (!playerManagerInstances.containsKey(player.getUUID())) {
            playerManagerInstances.put(player.getUUID(), new HUDManager(player));
        }
    }

    public static void onLeave(ServerPlayer player) {
        playerManagerInstances.remove(player.getUUID());
    }

    public static void onCopyData(ServerPlayer oldPlayer, ServerPlayer newPlayer, boolean alive) {
        assert(oldPlayer.getUUID().equals(newPlayer.getUUID()));
        playerManagerInstances.get(oldPlayer.getUUID()).setPlayer(newPlayer);
    }

    public static void tickAll(MinecraftServer server) {
        playerManagerInstances.values().forEach(HUDManager::updateText);
    }

}
