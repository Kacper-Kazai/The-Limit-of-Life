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

package dev.kazai.fabricmc.tlol.systems;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import dev.kazai.fabricmc.tlol.configs.TLoLConfigs;
import dev.kazai.fabricmc.tlol.networking.packet.ConfigSyncS2CPacket;
import dev.kazai.fabricmc.tlol.networking.packet.LivesSyncS2CPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

import static dev.kazai.fabricmc.tlol.TLoL.LOGGER;
import static dev.kazai.fabricmc.tlol.TLoL.MOD_ID;

public class LivesSystem {
    private static File getPlayerFile(MinecraftServer server, UUID playerUuid){
        File file = server.getSavePath(WorldSavePath.PLAYERDATA)
                .toAbsolutePath()
                .resolve(MOD_ID)
                .resolve(playerUuid.toString() + "." + MOD_ID).toFile();
        return file;
    }
    private static int readPlayerLivesFromFile(MinecraftServer server, UUID playerUUid){
        File playerFile = getPlayerFile(server, playerUUid);
        if(!playerFile.exists()) return TLoLConfigs.DEFAULT.getDefaultLives();
        String data;
        try {
            data = Files.readString(playerFile.toPath());
        } catch (IOException e) {
            return TLoLConfigs.DEFAULT.getDefaultLives();
        }
        JsonElement element = JsonParser.parseString(data);
        if(!element.getAsJsonObject().has("lives")) return TLoLConfigs.DEFAULT.getDefaultLives();
        return element.getAsJsonObject().get("lives").getAsInt();
    }
    private static boolean savePlayerLivesToFile(MinecraftServer server, UUID playerUUid, int lives){
        File playerFile = getPlayerFile(server, playerUUid);
        if(!(playerFile.getParentFile().exists() || playerFile.getParentFile().mkdirs())) return false;
        try {
            JsonWriter writer = new JsonWriter(new FileWriter(playerFile));
            writer.beginObject();
            writer.name("lives").value(lives);
            writer.endObject();
            writer.close();
        } catch (IOException e) {
            LOGGER.warn(e.toString());
            return false;
        }
        return true;
    }

    public static int getLives(MinecraftServer server, UUID playerUUid){
        return readPlayerLivesFromFile(server, playerUUid);
    }
    public static boolean setLives(MinecraftServer server, UUID playerUUid, int lives){
        if(!isValid(lives, false)) return false;
        boolean result = savePlayerLivesToFile(server, playerUUid, lives);
        if(!result) return false;
        syncData(server, playerUUid, lives);
        return true;
    }
    public static boolean addLives(MinecraftServer server, UUID playerUUid, int lives){
        int playerLives = getLives(server, playerUUid);
        playerLives += lives;
        return setLives(server, playerUUid, playerLives);
    }
    public static boolean subLives(MinecraftServer server, UUID playerUUid, int lives){
        int playerLives = getLives(server, playerUUid);
        playerLives -= lives;
        return setLives(server, playerUUid, playerLives);
    }

    public static void syncData(MinecraftServer server, UUID playerUUid, int lives){
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerUUid);
        if(player == null) return;
        LivesSyncS2CPacket.send(player, lives);
    }

    public static boolean isValid(int value, boolean isClient){
        return value > 0 && value <= (isClient ? ConfigSyncS2CPacket.getMaxLives() : TLoLConfigs.DEFAULT.getMaxLives());
    }
    public static int getLives(PlayerEntity player, boolean isClient){
        return player.world.isClient ? LivesSyncS2CPacket.getLives() : LivesSystem.getLives(player.getServer(), player.getUuid());
    }
}
