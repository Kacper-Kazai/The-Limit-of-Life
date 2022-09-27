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
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class LivesSystemHandler {
    public static void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive){
        if(alive) return;
        boolean result = LivesSystem.subLives(newPlayer.getServer(), newPlayer.getUuid(), 1);
        if(result) return;

        ServerCommandSource source = newPlayer.getServer().getCommandSource()
                .withEntity(newPlayer)
                .withPosition(newPlayer.getPos())
                .withRotation(newPlayer.getRotationClient())
                .withSilent();

        String command = TLoLConfigs.DEFAULT.getCommandAfterFinalDeath();
        newPlayer.getServer().getCommandManager().execute(source, command);
    }
}
