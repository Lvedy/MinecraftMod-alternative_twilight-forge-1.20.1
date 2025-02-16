package com.lvedy.alternative_twilight.Item;

import com.lvedy.alternative_twilight.ATMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ATMod.MODID);
    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_MODE_TABS.register("tab", () -> CreativeModeTab.builder()
            .icon(() -> ModItem.HONEY_FISH.get().getDefaultInstance())
            .title(Component.translatable("itemGroup.alternative_twilight.tab"))
            .displayItems((parameters, output) -> {
                output.accept(ModItem.HONEY_FISH.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
                output.accept(ModItem.LUXURY_FEAST.get());
            }).build());
}
