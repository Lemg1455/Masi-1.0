package com.lemg.masi.item;


import com.lemg.masi.Masi;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModitemGroup {
    public static final ItemGroup MASI_GROUP = Registry.register(Registries.ITEM_GROUP,
            new Identifier(Masi.MOD_ID,"masi_group"),
            FabricItemGroup.builder().displayName(Text.translatable("itemGroup.masi_group"))
                    .icon(()-> new ItemStack(ModItems.MAGE_CERTIFICATE)).entries((displayContext, entries) -> {
                        entries.add(ModItems.STAFF);
                        entries.add(ModItems.MAGE_CERTIFICATE);
                        entries.add(ModItems.MAGIC_SCROLL);
                        entries.add(ModItems.ENERGY_BOTTLE);
                        entries.add(ModItems.MAX_ENERGY_BOTTLE);
                        entries.add(ModItems.TRIAL_CARD);
                        entries.add(ModItems.MAGIC_SWORD);
                        entries.add(ModItems.ARCANE_BOW);
                        entries.add(ModItems.INHERIT_TOOL_ITEM);

                    }).build());



    public static void registerModItemGroup(){

    }
}
