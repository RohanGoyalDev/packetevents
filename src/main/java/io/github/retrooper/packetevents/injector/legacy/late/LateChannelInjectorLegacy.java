/*
 * This file is part of packetevents - https://github.com/retrooper/packetevents
 * Copyright (C) 2021 retrooper and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.retrooper.packetevents.injector.legacy.late;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.injector.LateInjector;
import io.github.retrooper.packetevents.injector.legacy.PacketDecoderLegacy;
import io.github.retrooper.packetevents.injector.legacy.early.PEChannelInitializerLegacy;
import io.github.retrooper.packetevents.protocol.PacketState;
import net.minecraft.util.io.netty.channel.Channel;
import net.minecraft.util.io.netty.channel.ChannelHandler;
import org.bukkit.entity.Player;

public class LateChannelInjectorLegacy implements LateInjector {
    @Override
    public void inject() {

    }

    @Override
    public void eject() {

    }

    @Override
    public void injectPlayer(Player player) {
        Channel channel = (Channel) PacketEvents.get().getPlayerManager().getChannel(player);
        PEChannelInitializerLegacy.postInitChannel(channel);
    }

    private PacketDecoderLegacy getDecoder(Object rawChannel) {
        Channel channel = (Channel) rawChannel;
        ChannelHandler decoder = channel.pipeline().get("decoder");
        if (decoder instanceof PacketDecoderLegacy) {
            return (PacketDecoderLegacy) decoder;
        }
        else {
            return null;
        }
    }


    @Override
    public void ejectPlayer(Player player) {
        Object channel = PacketEvents.get().getPlayerManager().getChannel(player);
        if (channel != null) {
            try {
                PEChannelInitializerLegacy.postDestroyChannel((Channel) channel);
            } catch (Exception ex) {

            }
        }
    }

    @Override
    public boolean hasInjected(Player player) {
        Object channel = PacketEvents.get().getPlayerManager().getChannel(player);
        if (channel == null) {
            return false;
        }
        PacketDecoderLegacy decoder = getDecoder(channel);
        return decoder != null && decoder.player != null;
    }

    @Override
    public void writePacket(Object ch, Object rawNMSPacket) {
        Channel channel = (Channel) ch;
        channel.write(rawNMSPacket);
    }

    @Override
    public void flushPackets(Object ch) {
        Channel channel = (Channel) ch;
        channel.flush();
    }

    @Override
    public void sendPacket(Object rawChannel, Object packet) {
        Channel channel = (Channel) rawChannel;
        channel.pipeline().writeAndFlush(packet);
    }

    @Override
    public PacketState getPacketState(Object channel) {
        return PacketState.PLAY;
    }

    @Override
    public void changePacketState(Object channel, PacketState packetState) {
        PacketDecoderLegacy decoder = getDecoder(channel);
        if (decoder != null) {
            decoder.packetState = packetState;
        }
    }
}