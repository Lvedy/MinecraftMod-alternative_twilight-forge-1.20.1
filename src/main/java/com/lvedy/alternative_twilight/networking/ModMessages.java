package com.lvedy.alternative_twilight.networking;

import com.lvedy.alternative_twilight.ATMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModMessages {
    private static SimpleChannel INSTACNE;
    private static int packetId = 0;

    private static int id(){
        return packetId++;
    }

    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder.named(new ResourceLocation(ATMod.MODID,"messages"))
                .networkProtocolVersion(()->"1.0")
                .clientAcceptedVersions(s->true)
                .serverAcceptedVersions(s->true)
                .simpleChannel();
        INSTACNE = net;

        net.messageBuilder(SnowQueenS2CPacket.class,id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SnowQueenS2CPacket::new)
                .encoder(SnowQueenS2CPacket::toBytes)
                .consumerMainThread(SnowQueenS2CPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message){
        INSTACNE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player){
        INSTACNE.send(PacketDistributor.PLAYER.with(()->player),message);
    }

    public static <MSG> void sendToAllPlayers(MSG message){
        INSTACNE.send(PacketDistributor.ALL.noArg(), message);
    }
}
