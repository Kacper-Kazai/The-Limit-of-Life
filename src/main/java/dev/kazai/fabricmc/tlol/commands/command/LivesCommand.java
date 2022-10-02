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

package dev.kazai.fabricmc.tlol.commands.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.kazai.fabricmc.tlol.configs.TLoLConfigs;
import dev.kazai.fabricmc.tlol.systems.LivesSystem;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import static net.minecraft.command.argument.GameProfileArgumentType.getProfileArgument;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static net.minecraft.command.argument.EntityArgumentType.TOO_MANY_PLAYERS_EXCEPTION;
import static net.minecraft.command.argument.EntityArgumentType.PLAYER_NOT_FOUND_EXCEPTION;
import static net.minecraft.server.command.CommandManager.*;

public class LivesCommand implements CommandRegistrationCallback {
    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, RegistrationEnvironment environment) {
        dispatcher.register(
                literal("lives")
                        .executes(ctx -> executeGet(ctx.getSource(), Collections.singleton(ctx.getSource().getPlayer().getGameProfile())))
                        .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                        .then(literal("reset")
                                .then(argument("players", GameProfileArgumentType.gameProfile())
                                        .executes(ctx -> executeReset(ctx.getSource(), getProfileArgument(ctx, "players")))
                                )
                        ).then(literal("set")
                                .then(argument("players", GameProfileArgumentType.gameProfile())
                                        .then(argument("lives", IntegerArgumentType.integer(1, TLoLConfigs.DEFAULT.getMaxLives()))
                                                .executes(ctx -> executeSet(ctx.getSource(), getProfileArgument(ctx, "players"), getInteger(ctx, "lives")))
                                        )
                                )
                        ).then(literal("get")
                                .then(argument("player", GameProfileArgumentType.gameProfile())
                                        .executes(ctx -> executeGet(ctx.getSource(), getProfileArgument(ctx, "player")))
                                )
                        ).then(literal("add")
                                .then(argument("players", GameProfileArgumentType.gameProfile())
                                        .then(argument("lives", IntegerArgumentType.integer(1, TLoLConfigs.DEFAULT.getMaxLives()-1))
                                                .executes(ctx -> executeAdd(ctx.getSource(), getProfileArgument(ctx, "players"), getInteger(ctx, "lives")))
                                        )
                                )
                        ).then(literal("remove")
                                .then(argument("players", GameProfileArgumentType.gameProfile())
                                        .then(argument("lives", IntegerArgumentType.integer(1, TLoLConfigs.DEFAULT.getMaxLives()-1))
                                                .executes(ctx -> executeRemove(ctx.getSource(), getProfileArgument(ctx, "players"), getInteger(ctx, "lives")))
                                        )
                                )
                        )
        );
    }

    private static int executeGet(ServerCommandSource source, Collection<GameProfile> gameProfiles) throws CommandSyntaxException {
        if(gameProfiles.size() != 1) throw TOO_MANY_PLAYERS_EXCEPTION.create();
        GameProfile gameProfile = gameProfiles.stream().findFirst().get();
        if(gameProfile.getId() == null) throw PLAYER_NOT_FOUND_EXCEPTION.create();

        int lives = LivesSystem.getLives(source.getServer(), gameProfile.getId());
        String playerName = "target";
        if(gameProfile.getName() != null) playerName = gameProfile.getName();

        source.sendFeedback(Text.translatable("commands.scoreboard.players.get.success", playerName, lives, "lives"), false);
        return lives;
    }
    private static int executeReset(ServerCommandSource source, Collection<GameProfile> gameProfiles) {
        Iterator<GameProfile> gameProfilesIterator = gameProfiles.iterator();
        String playerName = null;
        int playersCount = 0;

        while(gameProfilesIterator.hasNext()) {
            GameProfile gameProfile = gameProfilesIterator.next();
            boolean result = LivesSystem.setLives(source.getServer(), gameProfile.getId(), TLoLConfigs.DEFAULT.getDefaultLives());
            if(result){
                if(playerName == null) playerName = gameProfile.getName();
                playersCount++;
            }
        }

        if (gameProfiles.size() == 1 && playerName != null) {
            source.sendFeedback(Text.translatable("commands.scoreboard.players.reset.specific.single", "lives", playerName), true);
        } else {
            source.sendFeedback(Text.translatable("commands.scoreboard.players.reset.specific.multiple", "lives", playersCount), true);
        }

        return playersCount;
    }
    private static int executeSet(ServerCommandSource source, Collection<GameProfile> gameProfiles, int lives) {
        Iterator<GameProfile> gameProfilesIterator = gameProfiles.iterator();
        String playerName = null;
        int playersCount = 0;

        while(gameProfilesIterator.hasNext()) {
            GameProfile gameProfile = gameProfilesIterator.next();
            boolean result = LivesSystem.setLives(source.getServer(), gameProfile.getId(), lives);
            if(result){
                if(playerName == null) playerName = gameProfile.getName();
                playersCount++;
            }
        }

        if (gameProfiles.size() == 1 && playerName != null) {
            source.sendFeedback(Text.translatable("commands.scoreboard.players.set.success.single", "lives", playerName, lives), true);
        } else {
            source.sendFeedback(Text.translatable("commands.scoreboard.players.set.success.multiple", "lives", playersCount, lives), true);
        }
        return lives * playersCount;
    }
    private static int executeAdd(ServerCommandSource source, Collection<GameProfile> gameProfiles, int lives) {
        Iterator<GameProfile> gameProfilesIterator = gameProfiles.iterator();
        String playerName = null;
        int playersCount = 0;

        while(gameProfilesIterator.hasNext()) {
            GameProfile gameProfile = gameProfilesIterator.next();
            boolean result = LivesSystem.addLives(source.getServer(), gameProfile.getId(), lives);
            if(result){
                if(playerName == null) playerName = gameProfile.getName();
                playersCount++;
            }
        }

        int total = playersCount * lives;
        if (gameProfiles.size() == 1 && playerName != null) {
            source.sendFeedback(Text.translatable("commands.scoreboard.players.add.success.single", lives, "lives", playerName, total), true);
        } else {
            source.sendFeedback(Text.translatable("commands.scoreboard.players.add.success.multiple", lives, "lives", playersCount), true);
        }
        return total;
    }

    private static int executeRemove(ServerCommandSource source, Collection<GameProfile> gameProfiles, int lives) {
        Iterator<GameProfile> gameProfilesIterator = gameProfiles.iterator();
        String playerName = null;
        int playersCount = 0;

        while(gameProfilesIterator.hasNext()) {
            GameProfile gameProfile = gameProfilesIterator.next();
            boolean result = LivesSystem.subLives(source.getServer(), gameProfile.getId(), lives);
            if(result){
                if(playerName == null) playerName = gameProfile.getName();
                playersCount++;
            }
        }

        int total = playersCount * lives;
        if (gameProfiles.size() == 1 && playerName != null) {
            source.sendFeedback(Text.translatable("commands.scoreboard.players.add.success.single", lives, "lives", playerName, total), true);
        } else {
            source.sendFeedback(Text.translatable("commands.scoreboard.players.add.success.multiple", lives, "lives", playersCount), true);
        }
        return total;
    }
}
