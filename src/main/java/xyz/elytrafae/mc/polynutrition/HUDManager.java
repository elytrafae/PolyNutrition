package xyz.elytrafae.mc.polynutrition;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.MarkerElement;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import org.joml.Vector3f;

public class HUDManager {

    private ServerPlayer player;
    private TextDisplayElement text;
    private ElementHolder holder;
    private EntityAttachment attachment;

    private MarkerElement testArmorStand;

    public HUDManager(ServerPlayer entity) {
        text = new TextDisplayElement();
        text.setText(Component.literal("Hello!"));
        text.setBillboardMode(Display.BillboardConstraints.CENTER);
        text.setSeeThrough(true);
        text.setTranslation(new Vector3f(1f, -0.5f, 1f));
        text.setScale(new Vector3f(2f, 2f, 2f));

        testArmorStand = new MarkerElement();
        testArmorStand.setCustomName(Component.literal("Test Armor Stand!"));
        testArmorStand.setCustomNameVisible(true);

        holder = new ElementHolder();
        holder.addPassengerElement(text);
        holder.addPassengerElement(testArmorStand);
        setPlayer(entity);
    }

    public void setPlayer(ServerPlayer player) {
        if (this.player != null && attachment != null) {
            attachment.stopWatching(this.player);
        }
        this.player = player;

        updateText();
        //text.setInitialPosition(player.getEyePos());

        attachment = EntityAttachment.ofTicking(holder, player);
        attachment.startWatching(player);

        // EntityPassengersSetS2CPacket packet = new EntityPassengersSetS2CPacket()

        // TODO: Make the text ride the player...?
        player.sendSystemMessage(Component.literal("You should see text!"));
    }

    public void updateText() {

        text.setText(Component.literal(player.getFoodData().getFoodLevel() + "  " + player.getFoodData().getSaturationLevel()));
    }

}
