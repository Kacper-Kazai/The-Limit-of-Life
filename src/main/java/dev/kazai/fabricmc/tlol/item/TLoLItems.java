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

package dev.kazai.fabricmc.tlol.item;

import dev.kazai.fabricmc.tlol.item.custom.LaudanumItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static dev.kazai.fabricmc.tlol.TLoL.LOGGER;
import static dev.kazai.fabricmc.tlol.TLoL.MOD_ID;

public class TLoLItems {
    public static final Item LAUDANUM = registerItem("laudanum", new LaudanumItem(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC).group(ItemGroup.BREWING)));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registry.ITEM, new Identifier(MOD_ID, name), item);
    }

    public static void registerItems(){
        LOGGER.debug("Registering Mod Items for " + MOD_ID);
    }
}
