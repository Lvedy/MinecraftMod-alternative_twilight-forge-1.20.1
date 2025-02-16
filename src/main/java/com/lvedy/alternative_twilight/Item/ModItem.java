package com.lvedy.alternative_twilight.Item;

import com.lvedy.alternative_twilight.ATMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItem {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ATMod.MODID);
    public static final RegistryObject<Item> HONEY_FISH = ITEMS.register("honey_fish", ()->new Item(new Item.Properties().rarity(Rarity.RARE).food(ModFood.HONEY_FISH)));
    public static final RegistryObject<Item> LUXURY_FEAST = ITEMS.register("luxury_feast", ()->new Item(new Item.Properties().rarity(Rarity.EPIC).food(ModFood.LUXURY_FEAST).stacksTo(1)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
