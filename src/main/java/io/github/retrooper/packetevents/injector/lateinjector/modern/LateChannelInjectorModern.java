/*
 * MIT License
 *
 * Copyright (c) 2020 retrooper
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

package io.github.retrooper.packetevents.injector.lateinjector.modern;

import io.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.injector.handler.modern.PlayerChannelHandlerModern;
import io.github.retrooper.packetevents.injector.lateinjector.LateInjector;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.bukkit.entity.Player;

import java.util.NoSuchElementException;

public class LateChannelInjectorModern implements LateInjector {
    @Override
    public void inject() {

    }

    @Override
    public void eject() {

    }

    @Override
    public void injectPlayer(Player player) {
        PlayerChannelHandlerModern playerChannelHandlerModern = new PlayerChannelHandlerModern();
        Channel channel = (Channel) PacketEvents.get().packetProcessorInternal.getChannel(player);
        channel.pipeline().addBefore("packet_handler", PacketEvents.handlerName, playerChannelHandlerModern);
    }

    @Override
    public void ejectPlayer(Player player) {
        Object channel = PacketEvents.get().packetProcessorInternal.getChannel(player);
        if (channel != null) {
            Channel chnl = (Channel) channel;
            try {
                chnl.pipeline().remove(PacketEvents.handlerName);
            }
            catch (Exception ex) {

            }
        }
    }

    @Override
    public boolean hasInjected(Player player) {
        Channel channel = (Channel) PacketEvents.get().packetProcessorInternal.getChannel(player);
        return channel.pipeline().get(PacketEvents.handlerName) != null;
    }

    @Override
    public void sendPacket(Object rawChannel, Object packet) {
        Channel channel = (Channel) rawChannel;
        channel.pipeline().writeAndFlush(packet);
    }
}