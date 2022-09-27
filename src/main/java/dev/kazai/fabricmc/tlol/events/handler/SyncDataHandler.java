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

package dev.kazai.fabricmc.tlol.events.handler;

import dev.kazai.fabricmc.tlol.configs.TLoLConfigs;
import dev.kazai.fabricmc.tlol.systems.LivesSystem;
import dev.kazai.fabricmc.tlol.networking.packet.ConfigSyncS2CPacket;
import dev.kazai.fabricmc.tlol.networking.packet.LivesSyncS2CPacket;
import dev.kazai.fabricmc.tlol.util.IEntityData;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class SyncDataHandler {
    public static void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        ConfigSyncS2CPacket.send(handler.player, TLoLConfigs.DEFAULT.getMaxLives(), TLoLConfigs.DEFAULT.getDefaultLives(), TLoLConfigs.DEFAULT.getLaudanumUsageCooldown());

        int lives = LivesSystem.getLives(handler.player.getServer(), handler.player.getUuid());
        LivesSyncS2CPacket.send(handler.player, lives);

        IEntityData dataSaver = (IEntityData)handler.player;
        dataSaver.setLaudanumCooldown(dataSaver.getLaudanumCooldown());
    }
}
