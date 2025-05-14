package com.lvedy.alternative_twilight.networking;

import com.lvedy.alternative_twilight.entity.ModEntityTypes;
import com.lvedy.alternative_twilight.entity.custom.SnowTrapEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import twilightforest.TwilightForestMod;
import twilightforest.entity.boss.SnowQueen;
import twilightforest.init.TFEntities;

import java.util.UUID;
import java.util.function.Supplier;

public class SnowQueenS2CPacket {
    private final float invincible;
    private final float frosty;
    private final float ice_armor;
    private final float explosion;
    private final float snow_trap;
    private final boolean big;
    private final String uuid;

    public SnowQueenS2CPacket(float invincible, float frosty, float ice_armor, float explosion,float snow_trap, boolean big,UUID uuid){
        this.invincible = invincible;
        this.frosty = frosty;
        this.ice_armor = ice_armor;
        this.explosion = explosion;
        this.snow_trap = snow_trap;
        this.big = big;
        this.uuid = uuid.toString();
    }

    public SnowQueenS2CPacket(FriendlyByteBuf buf){
        this.invincible = buf.readFloat();
        this.frosty = buf.readFloat();
        this.ice_armor = buf.readFloat();
        this.explosion = buf.readFloat();
        this.snow_trap = buf.readFloat();
        this.big = buf.readBoolean();
        this.uuid = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeFloat(invincible);
        buf.writeFloat(frosty);
        buf.writeFloat(ice_armor);
        buf.writeFloat(explosion);
        buf.writeFloat(snow_trap);
        buf.writeBoolean(big);
        buf.writeUtf(uuid);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(()->{
            // 获取客户端世界实例
            net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
            if (minecraft.level != null) {
                // 将字符串UUID转换回UUID对象
                UUID entityUUID = UUID.fromString(uuid);
                // 遍历客户端世界中的所有实体，查找匹配UUID的实体
                minecraft.level.entitiesForRendering().forEach(entity -> {
                    if (entity.getUUID().equals(entityUUID)) {
                        // 找到匹配的实体后，更新其持久化数据
                        net.minecraft.nbt.CompoundTag data = entity.getPersistentData();
                        // 更新无敌状态和冰冻状态
                        if(entity instanceof SnowQueen) {
                            data.putInt("Invincible", (int) invincible);
                            data.putInt("Frosty", (int) frosty);
                            data.putInt("IceArmor", (int) ice_armor);
                            data.putInt("Explosion", (int) explosion);
                        }
                        if(entity instanceof SnowTrapEntity) {
                            data.putInt("LifeTime", (int) snow_trap);
                            data.putBoolean("Big", big);
                        }
                    }
                });
            }
        });
        context.setPacketHandled(true);
    }
}
