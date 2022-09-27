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

package dev.kazai.fabricmc.tlol.loottables;

import dev.kazai.fabricmc.tlol.configs.TLoLConfigs;
import dev.kazai.fabricmc.tlol.item.TLoLItems;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;

import java.util.HashMap;

public class TLoLLootTables {
    public static void registerLootTables(){
        HashMap<String, Float> laudanumLootTables =  TLoLConfigs.DEFAULT.getLaudanumLootTables();
        LootFunction nbtLootFunction = SetNbtLootFunction.builder(TLoLConfigs.DEFAULT.getLaudanumNbt()).build();

        LootTableEvents.MODIFY.register((resourceManager, lootManager, id, tableBuilder, source) -> {
            if(!laudanumLootTables.containsKey(id.toString())) return;

            float chance = laudanumLootTables.get(id.toString());

            LootPool.Builder poolBuilder = LootPool.builder()
                    .with(ItemEntry.builder(TLoLItems.LAUDANUM).build())
                    .apply(nbtLootFunction)
                    .conditionally(RandomChanceLootCondition.builder(chance));

            tableBuilder.pool(poolBuilder);
        });
    }
}
