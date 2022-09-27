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

package dev.kazai.fabricmc.tlol.configs;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Objects;

import static dev.kazai.fabricmc.tlol.TLoL.LOGGER;
import static dev.kazai.fabricmc.tlol.TLoL.MOD_ID;

public class Default extends Config{
    @Override
    String getName() {
        return "config";
    }

    private static int maxLives = 3;
    public static int getMaxLives(){ return maxLives; }
    private static int defaultLives = 3;
    public static int getDefaultLives(){ return defaultLives; }
    private static String commandAfterFinalDeath = "gamemode spectator @s";
    public static String getCommandAfterFinalDeath() { return commandAfterFinalDeath; }
    private static boolean livesSystemDisplayOnScreen = true;
    public static boolean getLivesSystemDisplayOnScreen() { return livesSystemDisplayOnScreen; }

    private static float livesSystemDisplayOnScreenXWindow = 0.5F;
    public static float getLivesSystemDisplayOnScreenXWindow() { return livesSystemDisplayOnScreenXWindow; }
    private static int livesSystemDisplayOnScreenXOffset = 105;
    public static int getLivesSystemDisplayOnScreenXOffset() { return livesSystemDisplayOnScreenXOffset; }
    private static float livesSystemDisplayOnScreenYWindow = 1F;
    public static float getLivesSystemDisplayOnScreenYWindow() { return livesSystemDisplayOnScreenYWindow; }
    private static int livesSystemDisplayOnScreenYOffset = 44;
    public static int getLivesSystemDisplayOnScreenYOffset() { return livesSystemDisplayOnScreenYOffset; }


    private static int laudanumUsageCooldown = 0;
    public static int getLaudanumUsageCooldown() {return laudanumUsageCooldown;}
    private static NbtCompound laudanumNbt = new NbtCompound();
    public static NbtCompound getLaudanumNbt() { return laudanumNbt; }
    private static HashMap<String, Float> laudanumLootTables = new HashMap<>();
    public static HashMap<String, Float> getLaudanumLootTables(){ return laudanumLootTables;}

    @Override
    void load() {
        JsonObject configObject = getJsonObjectFromFile(getName());
        if(Objects.isNull(configObject)) return;

        if(configObject.has("lives_system")){
            try{
                JsonObject livesSystemObject = configObject.get("lives_system").getAsJsonObject();

                try{
                    defaultLives = livesSystemObject.get("default_lives").getAsInt();
                }catch (Exception e) {}

                try{
                    maxLives = livesSystemObject.get("max_lives").getAsInt();
                }catch (Exception e) {}

                try{
                    commandAfterFinalDeath = livesSystemObject.get("command_after_final_death").getAsString();
                }catch (Exception e) {}

                try{
                    JsonObject displayOnScreenObject = livesSystemObject.get("display_on_screen").getAsJsonObject();

                    try{
                        livesSystemDisplayOnScreen = displayOnScreenObject.get("enabled").getAsBoolean();
                    }catch (Exception e) {}

                    try{
                        JsonObject livesSystemDisplayOnScreenX = displayOnScreenObject.get("x").getAsJsonObject();

                        try{
                            livesSystemDisplayOnScreenXWindow = livesSystemDisplayOnScreenX.get("window").getAsFloat();
                        }catch (Exception e) {}
                        try{
                            livesSystemDisplayOnScreenXOffset = livesSystemDisplayOnScreenX.get("offset").getAsInt();
                        }catch (Exception e) {}
                    }catch (Exception e) {}

                    try{
                        JsonObject livesSystemDisplayOnScreenY = displayOnScreenObject.get("y").getAsJsonObject();

                        try{
                            livesSystemDisplayOnScreenYWindow = livesSystemDisplayOnScreenY.get("window").getAsFloat();
                        }catch (Exception e) {}
                        try{
                            livesSystemDisplayOnScreenYOffset = livesSystemDisplayOnScreenY.get("offset").getAsInt();
                        }catch (Exception e) {}
                    }catch (Exception e) {}

                }catch (Exception e) {}

            }catch (Exception e) {}
        }
        if(configObject.has("laudanum")) {
            try{
                JsonObject laudanumObject = configObject.get("laudanum").getAsJsonObject();

                try {
                    String nbt = laudanumObject.get("nbt").getAsString();
                    laudanumNbt = StringNbtReader.parse(nbt);
                }catch (Exception e) { LOGGER.warn(e.toString()); }

                try{
                    laudanumUsageCooldown = laudanumObject.get("usage_cooldown").getAsInt();
                }catch (Exception e) {}

                try {
                    HashMap<String, Float> lootTables = new HashMap<>();
                    for (JsonElement loot_table : laudanumObject.get("loot_tables").getAsJsonArray()) {
                        try {
                            String key = loot_table.getAsJsonObject().get("entry").getAsString();

                            float value = 1;
                            try {
                                value = loot_table.getAsJsonObject().get("chance").getAsFloat();
                            } catch (Exception e) {}

                            lootTables.put(key, value);
                        }catch (Exception e){}
                    }
                    laudanumLootTables = lootTables;
                }catch (Exception e) {}

            }catch (Exception e) {}
        }
    }

    @Nullable
    private JsonObject getJsonObjectFromFile(String configName) {
        File configFile = getFile();
        try {
            if(!configFile.exists()){
                if(!(configFile.getParentFile().exists() || configFile.getParentFile().mkdirs())) return null;
                if(!saveDefault()) return null;
            }
            String data = Files.readString(configFile.toPath());
            JsonElement element = JsonParser.parseString(data);
            return element.getAsJsonObject();
        } catch (Exception e) {
            LOGGER.warn(e.toString());
        }
        return null;
    }
}
