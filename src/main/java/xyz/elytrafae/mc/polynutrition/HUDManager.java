package xyz.elytrafae.mc.polynutrition;

import eu.pb4.polymer.virtualentity.api.ElementHolder;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import eu.pb4.polymer.virtualentity.api.elements.MarkerElement;
import eu.pb4.polymer.virtualentity.api.elements.TextDisplayElement;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.joml.Vector3f;

public class HUDManager {

    private ServerPlayerEntity player;
    private TextDisplayElement text;
    private ElementHolder holder;
    private EntityAttachment attachment;

    private MarkerElement testArmorStand;

    public HUDManager(ServerPlayerEntity entity) {
        text = new TextDisplayElement();
        text.setText(Text.literal("Hello!"));
        text.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
        text.setSeeThrough(true);
        text.setTranslation(new Vector3f(1f, -0.5f, 1f));
        text.setScale(new Vector3f(2f, 2f, 2f));

        testArmorStand = new MarkerElement();
        testArmorStand.setCustomName(Text.literal("Test Armor Stand!"));
        testArmorStand.setCustomNameVisible(true);

        holder = new ElementHolder();
        holder.addPassengerElement(text);
        holder.addPassengerElement(testArmorStand);
        setPlayer(entity);
    }

    public void setPlayer(ServerPlayerEntity player) {
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
        player.sendMessage(Text.literal("You should see text!"));
    }

    public void updateText() {
        text.setText(Text.literal(player.getHungerManager().getFoodLevel() + "  " + player.getHungerManager().getSaturationLevel()));
    }

}
