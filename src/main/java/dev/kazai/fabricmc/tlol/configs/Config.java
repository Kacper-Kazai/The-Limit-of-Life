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

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static dev.kazai.fabricmc.tlol.TLoL.LOGGER;
import static dev.kazai.fabricmc.tlol.TLoL.MOD_ID;

public abstract class Config{
    abstract String getName();
    abstract void load();

    public File getFile(){
        return FabricLoader.getInstance().getConfigDir()
            .resolve(MOD_ID)
            .resolve(getName() + ".json").toFile();
    }
    public boolean saveDefault(){
        return saveDefault(getFile());
    }

    private static boolean saveDefault(File configFile){
        URL defaultConfig = getResource(configFile.getName());
        try {
            InputStream is = defaultConfig.openStream();
            if(is == null) return false;
            Files.copy(is, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            LOGGER.warn(e.toString());
            return false;
        }
    }

    private static URL getResource(String path){
        return Config.class.getResource("/" + path);
    }
}
