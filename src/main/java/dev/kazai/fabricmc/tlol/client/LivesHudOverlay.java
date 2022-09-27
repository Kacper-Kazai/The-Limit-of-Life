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

package dev.kazai.fabricmc.tlol.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.kazai.fabricmc.tlol.TLoL;
import dev.kazai.fabricmc.tlol.configs.TLoLConfigs;
import dev.kazai.fabricmc.tlol.networking.packet.LivesSyncS2CPacket;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;

public class LivesHudOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(MatrixStack matrixStack, float tickDelta) {
        MinecraftClient client = MinecraftClient.getInstance();
        if(client == null) return;
        if(client.player.getAbilities().creativeMode || client.player.isSpectator()) return;

        int width = MinecraftClient.getInstance().getWindow().getScaledWidth();
        int height = MinecraftClient.getInstance().getWindow().getScaledHeight();

        int lives = LivesSyncS2CPacket.getLives();

        RenderSystem.setShader(GameRenderer::getPositionShader);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.setShaderTexture(0, DrawableHelper.GUI_ICONS_TEXTURE);

        int size = 14;

//        int offsetX = 105;
//        int offsetY = 44;

        int posX = (int)(width*TLoLConfigs.DEFAULT.getLivesSystemDisplayOnScreenXWindow()) - TLoLConfigs.DEFAULT.getLivesSystemDisplayOnScreenXOffset();
        int posY = (int)(height*TLoLConfigs.DEFAULT.getLivesSystemDisplayOnScreenYWindow()) - TLoLConfigs.DEFAULT.getLivesSystemDisplayOnScreenYOffset();

        DrawableHelper.drawTexture(matrixStack, posX, posY, size, size, 16, 0, 9, 9, 256, 256);
        DrawableHelper.drawTexture(matrixStack, posX, posY, size, size, 52, 0, 9, 9, 256, 256);
        DrawableHelper.drawCenteredText(matrixStack, MinecraftClient.getInstance().textRenderer, ""+lives, posX+size/2, posY+size/2-4, Color.WHITE.getRGB());
    }
}
