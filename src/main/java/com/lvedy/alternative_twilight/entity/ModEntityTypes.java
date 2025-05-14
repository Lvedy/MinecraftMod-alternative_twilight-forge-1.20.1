package com.lvedy.alternative_twilight.entity;

import com.lvedy.alternative_twilight.ATMod;
import com.lvedy.alternative_twilight.entity.custom.SnowTrapEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, ATMod.MODID);

    public static final RegistryObject<EntityType<SnowTrapEntity>> SNOW_TRAP =
            ENTITY_TYPES.register("snow_trap",
                    () -> EntityType.Builder.of(SnowTrapEntity::new, MobCategory.CREATURE)
                            .sized(0.8f, 0.1f)
                            .build("snow_trap"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}