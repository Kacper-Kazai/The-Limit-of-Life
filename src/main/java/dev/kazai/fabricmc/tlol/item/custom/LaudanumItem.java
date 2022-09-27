/*
 * The Limit of Life
 * Copyright (c) 2022 Kacper Kazai
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package dev.kazai.fabricmc.tlol.item.custom;

import dev.kazai.fabricmc.tlol.configs.TLoLConfigs;
import dev.kazai.fabricmc.tlol.networking.packet.LaudanumCooldownSyncS2CPacket;
import dev.kazai.fabricmc.tlol.systems.LivesSystem;
import dev.kazai.fabricmc.tlol.networking.packet.ConfigSyncS2CPacket;
import dev.kazai.fabricmc.tlol.networking.packet.LivesSyncS2CPacket;
import dev.kazai.fabricmc.tlol.util.IEntityData;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class LaudanumItem extends Item {
    private static final int MAX_USE_TIME = 40;

    public LaudanumItem(Settings settings) {
        super(settings);
    }

    public int getMaxUseTime(ItemStack stack) {
        return MAX_USE_TIME;
    }
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        int laudanumCooldown = world.isClient ?
                LaudanumCooldownSyncS2CPacket.getCooldown() :
                ((IEntityData)user).getLaudanumCooldown();
        if(laudanumCooldown > 0) return TypedActionResult.pass(user.getStackInHand(hand));

        int lives = LivesSystem.getLives(user, world.isClient);
        boolean result = LivesSystem.isValid(lives+1, world.isClient);
        if(!result) return TypedActionResult.pass(user.getStackInHand(hand));

        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        ItemStack itemStack = super.finishUsing(stack, world, user);
        if(!(user instanceof PlayerEntity)) return itemStack;

        int laudanumCooldown = world.isClient ?
                LaudanumCooldownSyncS2CPacket.getCooldown() :
                ((IEntityData)user).getLaudanumCooldown();
        if(laudanumCooldown > 0) return itemStack;

        boolean result = world.isClient ?
            LivesSystem.isValid(LivesSystem.getLives((PlayerEntity) user, true), true) :
            LivesSystem.addLives(world.getServer(), user.getUuid(), 1);
        if (!result) return itemStack;

        if(!world.isClient) {
            int cooldown = TLoLConfigs.DEFAULT.getLaudanumUsageCooldown();
            ((IEntityData)user).setLaudanumCooldown(cooldown);
            if(user instanceof ServerPlayerEntity) LaudanumCooldownSyncS2CPacket.send((ServerPlayerEntity)user , cooldown);

            NbtCompound nbtEffects = stack.getNbt();
            if (nbtEffects != null && nbtEffects.contains("CustomPotionEffects", NbtElement.LIST_TYPE)) {
                NbtList nbtList = nbtEffects.getList("CustomPotionEffects", NbtElement.COMPOUND_TYPE);
                for (int i = 0; i < nbtList.size(); ++i) {
                    NbtCompound nbtCompound = nbtList.getCompound(i);
                    if (nbtCompound.contains("Id", NbtElement.STRING_TYPE)) {
                        Identifier identifier = new Identifier(nbtCompound.getString("Id"));
                        StatusEffect effect = Registry.STATUS_EFFECT.get(identifier);
                        byte effectId = (byte) Registry.STATUS_EFFECT.getRawId(effect);
                        if (!Objects.nonNull(effectId)) continue;
                        nbtCompound.putByte("Id", effectId);
                    }
                    StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
                    if (statusEffectInstance.getEffectType().isInstant()) {
                        statusEffectInstance.getEffectType().applyInstantEffect(user, user, user, statusEffectInstance.getAmplifier(), 1.0);
                    } else {
                        user.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
                    }
                }
            }
        }

        return ((PlayerEntity)user).getAbilities().creativeMode ? itemStack : new ItemStack(Items.GLASS_BOTTLE);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add((new TranslatableText("item.tlol.laudanum.tooltip")).formatted(Formatting.GRAY));
        if(LaudanumCooldownSyncS2CPacket.getCooldown() <= 0) return;
        if(Screen.hasShiftDown()) tooltip.add(new TranslatableText("item.tlol.laudanum.cooldown", LaudanumCooldownSyncS2CPacket.getCooldown()/20 + "s"));
        else tooltip.add(new TranslatableText("item.tlol.tooltip.more_information").formatted(Formatting.YELLOW));
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return LaudanumCooldownSyncS2CPacket.getCooldown() > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return Math.round(13F * (1 - (float)LaudanumCooldownSyncS2CPacket.getCooldown() / (float)ConfigSyncS2CPacket.getLaudanumUsageCooldown()));
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return Color.WHITE.getRGB();
    }
}