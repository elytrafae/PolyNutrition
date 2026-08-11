package xyz.elytrafae.mc.polynutrition;

import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;
import xyz.elytrafae.mc.polybars.api.LayeredHealthStylePolyBar;
import xyz.elytrafae.mc.polybars.api.PolyBarTexture;

import java.util.List;

public class HungerBarWithSaturation extends LayeredHealthStylePolyBar {
    public HungerBarWithSaturation(Identifier id, Identifier textureBaseId, int priority) {
        super(id, List.of(
                new PolyBarTexture(textureBaseId.withSuffix("food_empty"), 2),
                new PolyBarTexture(textureBaseId.withSuffix("food_half"), 2),
                new PolyBarTexture(textureBaseId.withSuffix("food_full"), 2),
                new PolyBarTexture(textureBaseId.withSuffix("saturation_half"), 2),
                new PolyBarTexture(textureBaseId.withSuffix("saturation_full"), 2)
        ), priority, 2);
    }

    @Override
    public double getValue(ServerPlayer player, int layer) {
        FoodData food = player.getFoodData();
        return layer == 0 ? food.getFoodLevel() : food.getSaturationLevel();
    }

    @Override
    public double getMaxValue(ServerPlayer player, int layer) {
        return 20;
    }

    @Override
    public int getFullTextureIndex(ServerPlayer player, int layer) {
        return layer == 0 ? 2 : 4;
    }

    @Override
    public int getHalfTextureIndex(ServerPlayer player, int layer) {
        return layer == 0 ? 1 : 3;
    }

    @Override
    public int getSliceIndex(ServerPlayer player) {
        return player.hasEffect(MobEffects.HUNGER) ? 1 : 0;
    }

    @Override
    public boolean isOrderReversed(ServerPlayer player) {
        return true;
    }
}
