package com.lvedy.alternative_twilight.networking;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import twilightforest.entity.boss.Minoshroom;

import java.util.UUID;
import java.util.function.Supplier;

public class MinoshroomS2CPacket {
    private final int trackingTime;
    private final int targetSwitchCooldown;
    private final String uuid;

    public MinoshroomS2CPacket(int trackingTime, int targetSwitchCooldown, UUID uuid) {
        this.trackingTime = trackingTime;
        this.targetSwitchCooldown = targetSwitchCooldown;
        this.uuid = uuid.toString();
    }

    public MinoshroomS2CPacket(FriendlyByteBuf buf) {
        this.trackingTime = buf.readInt();
        this.targetSwitchCooldown = buf.readInt();
        this.uuid = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(trackingTime);
        buf.writeInt(targetSwitchCooldown);
        buf.writeUtf(uuid);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            // 获取客户端世界实例
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            if (minecraft.level != null) {
                // 将字符串UUID转换回UUID对象
                UUID entityUUID = UUID.fromString(uuid);
                // 遍历客户端世界中的所有实体，查找匹配UUID的实体
                minecraft.level.entitiesForRendering().forEach(entity -> {
                    if (entity.getUUID().equals(entityUUID) && entity instanceof Minoshroom) {
                        // 找到匹配的实体后，更新其持久化数据
                        CompoundTag data = entity.getPersistentData();
                        // 更新追踪时间
                        data.putInt("TrackingTime", trackingTime);
                        // 更新目标转移冷却时间
                        data.putInt("TargetSwitchCooldown", targetSwitchCooldown);
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }
}