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

package dev.kazai.fabricmc.tlol.mixin;

import dev.kazai.fabricmc.tlol.networking.packet.LaudanumCooldownSyncS2CPacket;
import dev.kazai.fabricmc.tlol.util.IEntityData;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static dev.kazai.fabricmc.tlol.TLoL.MOD_ID;

@Mixin(Entity.class)
public class TLoLEntityDataMixin implements IEntityData {
    private NbtCompound persistentData;
    private final static Identifier IDENTIFIER = new Identifier(MOD_ID, "data");
    private final static String LAUDANUM_COOLDOWN = "laudanum_cooldown";

    @Override
    public NbtCompound getPersistentData() {
        if(this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return this.persistentData;
    }

    @Inject(at = @At("HEAD"), method = "writeNbt")
    public void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> info){
        if(persistentData != null){
            nbt.put(IDENTIFIER.toString(), persistentData);
        }
    }
    @Inject(at = @At("HEAD"), method = "readNbt")
    public void injectReadMethod(NbtCompound nbt, CallbackInfo info){
        if(nbt.contains(IDENTIFIER.toString(), NbtElement.COMPOUND_TYPE)){
            persistentData = nbt.getCompound(IDENTIFIER.toString());
        }
    }

    @Override
    public void setLaudanumCooldown(int ticks) {
        this.getPersistentData().putInt(LAUDANUM_COOLDOWN, ticks);
        LaudanumCooldownSyncS2CPacket.send((ServerPlayerEntity)(Object)this, ticks);
    }
    @Override
    public int getLaudanumCooldown() {
        if(!this.getPersistentData().contains(LAUDANUM_COOLDOWN)) return 0;
        return this.persistentData.getInt(LAUDANUM_COOLDOWN);
    }
}
