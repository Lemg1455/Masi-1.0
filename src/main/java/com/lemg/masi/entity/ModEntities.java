package com.lemg.masi.entity;

import com.lemg.masi.Masi;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static final EntityType<ArcaneMinionEntity> ARCANE_MINION = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"arcane_minion"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC,ArcaneMinionEntity::new)
                    .trackRangeChunks(15)
                    .dimensions(EntityDimensions.fixed(2.0f,4.0f)).build());

    public static final EntityType<SwordEnergyEntity> SWORD_ENERGY = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"sword_energy"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC,SwordEnergyEntity::new)
                    .trackRangeChunks(15)
                    .dimensions(EntityDimensions.fixed(5.0f,0.5f)).build());
}