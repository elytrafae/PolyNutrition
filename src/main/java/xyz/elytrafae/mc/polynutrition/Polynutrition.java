package xyz.elytrafae.mc.polynutrition;

import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.context.PacketContext;
import net.fabricmc.fabric.impl.item.DefaultItemComponentImpl;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.List;

public class Polynutrition implements ModInitializer {

    public static final String MODID = "polynutrition";

    public static final FontDescription DEFAULT_FONT = new FontDescription.Resource(Identifier.parse("default"));
    public static final FontDescription CUSTOM_HUNGER_FONT = new FontDescription.Resource(Identifier.fromNamespaceAndPath(MODID, "food"));
    public static final String DEFAULT_HUNGER_SYMBOL = "\uD83C\uDF56";
    public static final char HUNGER_SYMBOL = 'a';
    public static final char SATURATION_SYMBOL = 'b';
    public static final char HUNGER_HALF_SYMBOL = 'c';
    public static final char SATURATION_HALF_SYMBOL = 'd';
    public static final Style HUNGER_STYLE = Style.EMPTY.withColor(0x773502).withItalic(false);
    public static final Style COMPLETE_HUNGER_STYLE = HUNGER_STYLE.withFont(CUSTOM_HUNGER_FONT);
    public static final Style SATURATION_STYLE = Style.EMPTY.withColor(0xfcf400).withItalic(false);
    public static final Style COMPLETE_SATURATION_STYLE = SATURATION_STYLE.withFont(CUSTOM_HUNGER_FONT);
    public static final Style WHITE_STYLE = Style.EMPTY.withColor(ChatFormatting.WHITE).withFont(DEFAULT_FONT);

    @Override
    public void onInitialize() {
        PolymerItemUtils.CONTEXT_ITEM_CHECK.register((stack, packetContext) -> stack.get(DataComponents.FOOD) != null);
        PolymerItemUtils.ITEM_MODIFICATION_EVENT.register(this::modifyClientItem);

        PolymerResourcePackUtils.addModAssets(MODID);

        ServerPlayerEvents.JOIN.register(HUDManagerInstanceHolder::onJoin);
        ServerPlayerEvents.LEAVE.register(HUDManagerInstanceHolder::onLeave);
        ServerPlayerEvents.COPY_FROM.register(HUDManagerInstanceHolder::onCopyData);
        ServerTickEvents.START_SERVER_TICK.register(HUDManagerInstanceHolder::tickAll);
    }

    public ItemStack modifyClientItem(ItemStack original, ItemStack client, PacketContext context) {
        if (!original.has(DataComponents.FOOD)) {
            return client;
        }
        FoodProperties FOOD = original.get(DataComponents.FOOD);
        ItemLore LORE;
        if (client.has(DataComponents.LORE)) {
            LORE = client.get(DataComponents.LORE);
        } else {
            LORE = new ItemLore(List.of());
        }

        assert FOOD != null;

        Component hungerText = getHungerText(FOOD.nutrition(), context);
        Component saturationText = getSaturationText(FOOD.saturation(), context);

        assert LORE != null;
        LORE = LORE.withLineAdded(hungerText).withLineAdded(saturationText);
        client.set(DataComponents.LORE, LORE);
        return client;
    }

    private static Component getHungerText(int hunger, PacketContext context) {
        return getHungerRelatedText(hunger/2f, context, DEFAULT_HUNGER_SYMBOL, HUNGER_SYMBOL, HUNGER_HALF_SYMBOL, COMPLETE_HUNGER_STYLE, HUNGER_STYLE);
    }

    private static Component getSaturationText(float saturation, PacketContext context) {
        return getHungerRelatedText(saturation/2f, context, DEFAULT_HUNGER_SYMBOL, SATURATION_SYMBOL, SATURATION_HALF_SYMBOL, COMPLETE_SATURATION_STYLE, SATURATION_STYLE);
    }

    private static Component getHungerRelatedText(float number, PacketContext context, String defaultSymbol, char symbol, char half_symbol, Style completeStyle, Style colorOnlyStyle) {
        if (!PolymerResourcePackUtils.hasMainPack(context)) {
            return Component.literal(defaultSymbol).setStyle(colorOnlyStyle).append(getNumberText(number));
        }
        if (number > 6 || number <= 0) {
            return Component.literal(String.valueOf(symbol)).setStyle(completeStyle).append(getNumberText(number));
        }
        StringBuilder builder = new StringBuilder();
        int intNumber = (int)(number*2);
        int i = 0;
        for (; i < (intNumber/2*2); i+=2) {
            builder.append(symbol);
        }
        if (i < number*2) {
            builder.append(half_symbol);
        }
        if (builder.isEmpty()) {
            return Component.empty();
        }
        return Component.literal(builder.toString()).setStyle(completeStyle);
    }


    private static Component getNumberText(float number) {
        return Component.literal(String.format(" x %.2f", number)).setStyle(WHITE_STYLE);
    }



}
