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

package dev.kazai.fabricmc.tlol.networking;

import dev.kazai.fabricmc.tlol.networking.packet.ConfigSyncS2CPacket;
import dev.kazai.fabricmc.tlol.networking.packet.LaudanumCooldownSyncS2CPacket;
import dev.kazai.fabricmc.tlol.networking.packet.LivesSyncS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class TLoLMessages {
    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(ConfigSyncS2CPacket.IDENTIFIER, ConfigSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LivesSyncS2CPacket.IDENTIFIER, LivesSyncS2CPacket::receive);
        ClientPlayNetworking.registerGlobalReceiver(LaudanumCooldownSyncS2CPacket.IDENTIFIER, LaudanumCooldownSyncS2CPacket::receive);
    }
}
