package xyz.elytrafae.mc.polynutrition;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.UUID;

public class HUDManagerInstanceHolder {

    private static HashMap<UUID, HUDManager> playerManagerInstances = new HashMap<>();

    public static void onJoin(ServerPlayerEntity player) {
        if (!playerManagerInstances.containsKey(player.getUuid())) {
            playerManagerInstances.put(player.getUuid(), new HUDManager(player));
        }
    }

    public static void onLeave(ServerPlayerEntity player) {
        playerManagerInstances.remove(player.getUuid());
    }

    public static void onCopyData(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        assert(oldPlayer.getUuid().equals(newPlayer.getUuid()));
        playerManagerInstances.get(oldPlayer.getUuid()).setPlayer(newPlayer);
    }

    public static void tickAll(MinecraftServer server) {
        playerManagerInstances.values().forEach(HUDManager::updateText);
    }

}
