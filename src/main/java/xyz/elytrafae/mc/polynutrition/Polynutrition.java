package xyz.elytrafae.mc.polynutrition;

import eu.pb4.polymer.core.api.item.PolymerItemUtils;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.font.Font;
import net.minecraft.client.font.FontManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public class Polynutrition implements ModInitializer {

    public static final String MODID = "polynutrition";

    public static final StyleSpriteSource DEFAULT_FONT = new StyleSpriteSource.Font(Identifier.ofVanilla("default"));
    public static final StyleSpriteSource CUSTOM_HUNGER_FONT = new StyleSpriteSource.Font(Identifier.of(MODID, "food"));
    public static final String DEFAULT_HUNGER_SYMBOL = "\uD83C\uDF56";
    public static final char HUNGER_SYMBOL = 'a';
    public static final char SATURATION_SYMBOL = 'b';
    public static final char HUNGER_HALF_SYMBOL = 'c';
    public static final char SATURATION_HALF_SYMBOL = 'd';
    public static final Style HUNGER_STYLE = Style.EMPTY.withColor(0x773502).withItalic(false);
    public static final Style COMPLETE_HUNGER_STYLE = HUNGER_STYLE.withFont(CUSTOM_HUNGER_FONT);
    public static final Style SATURATION_STYLE = Style.EMPTY.withColor(0xfcf400).withItalic(false);
    public static final Style COMPLETE_SATURATION_STYLE = SATURATION_STYLE.withFont(CUSTOM_HUNGER_FONT);
    public static final Style WHITE_STYLE = Style.EMPTY.withColor(Formatting.WHITE).withFont(DEFAULT_FONT);

    @Override
    public void onInitialize() {
        PolymerItemUtils.CONTEXT_ITEM_CHECK.register((stack, packetContext) -> stack.contains(DataComponentTypes.FOOD));
        PolymerItemUtils.ITEM_MODIFICATION_EVENT.register(this::modifyClientItem);

        PolymerResourcePackUtils.addModAssets(MODID);
    }

    public ItemStack modifyClientItem(ItemStack original, ItemStack client, PacketContext context) {
        if (!original.contains(DataComponentTypes.FOOD)) {
            return client;
        }
        FoodComponent FOOD = original.get(DataComponentTypes.FOOD);
        LoreComponent LORE;
        if (client.contains(DataComponentTypes.LORE)) {
            LORE = client.get(DataComponentTypes.LORE);
        } else {
            LORE = new LoreComponent(List.of());
        }

        assert FOOD != null;

        Text hungerText = getHungerText(FOOD.nutrition(), context);
        Text saturationText = getSaturationText(FOOD.saturation(), context);

        LORE = LORE.with(hungerText).with(saturationText);
        client.set(DataComponentTypes.LORE, LORE);
        return client;
    }

    private static Text getHungerText(int hunger, PacketContext context) {
        return getHungerRelatedText(hunger/2f, context, DEFAULT_HUNGER_SYMBOL, HUNGER_SYMBOL, HUNGER_HALF_SYMBOL, COMPLETE_HUNGER_STYLE, HUNGER_STYLE);
    }

    private static Text getSaturationText(float saturation, PacketContext context) {
        return getHungerRelatedText(saturation/2f, context, DEFAULT_HUNGER_SYMBOL, SATURATION_SYMBOL, SATURATION_HALF_SYMBOL, COMPLETE_SATURATION_STYLE, SATURATION_STYLE);
    }

    private static Text getHungerRelatedText(float number, PacketContext context, String defaultSymbol, char symbol, char half_symbol, Style completeStyle, Style colorOnlyStyle) {
        if (!PolymerResourcePackUtils.hasMainPack(context)) {
            return ((MutableText)Text.of(defaultSymbol)).setStyle(colorOnlyStyle).append(getNumberText(number));
        }
        if (number > 6 || number <= 0) {
            return ((MutableText)Text.of(String.valueOf(symbol))).setStyle(completeStyle).append(getNumberText(number));
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
            return Text.empty();
        }
        return Text.of(builder.toString()).getWithStyle(completeStyle).getFirst();
    }


    private static Text getNumberText(float number) {
        return Text.of(String.format(" x %.2f", number)).getWithStyle(WHITE_STYLE).getFirst();
    }



}
