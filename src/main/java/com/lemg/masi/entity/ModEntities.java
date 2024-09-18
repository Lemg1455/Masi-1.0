package com.lemg.masi.entity;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.entities.ArcaneArrowEntity;
import com.lemg.masi.entity.entities.MeteoriteEntity;
import com.lemg.masi.entity.entities.minions.*;
import com.lemg.masi.entity.entities.SwordEnergyEntity;
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
            FabricEntityTypeBuilder.create(SpawnGroup.MISC, SwordEnergyEntity::new)
                    .trackRangeChunks(15)
                    .dimensions(EntityDimensions.fixed(5.0f,0.5f)).build());

    public static final EntityType<ArcaneArrowEntity> ARCANE_ARROW = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"arcane_arrow"),
            FabricEntityTypeBuilder.<ArcaneArrowEntity>create(SpawnGroup.MISC, ArcaneArrowEntity::new)
                    .trackRangeChunks(4)
                    .trackedUpdateRate(10)
                    .dimensions(EntityDimensions.fixed(0.5f,0.5f)).build());

    public static final EntityType<MeteoriteEntity> METEORITE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"meteorite"),
            FabricEntityTypeBuilder.<MeteoriteEntity>create(SpawnGroup.MISC, MeteoriteEntity::new)
                    .trackRangeChunks(4)
                    .trackedUpdateRate(10)
                    .dimensions(EntityDimensions.fixed(4.0f,4.0f)).build());

    public static final EntityType<SwordManEntity> SWORD_MAN = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"sword_man"),
            FabricEntityTypeBuilder.create(SpawnGroup.MISC,SwordManEntity::new)
                    .trackRangeChunks(10)
                    .dimensions(EntityDimensions.fixed(0.6f,1.95f)).build());

    public static final EntityType<MasiZombieVillagerEntity> MASI_ZOMBIE_VILLAGER = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"masi_zombie_villager"),
            FabricEntityTypeBuilder.<MasiZombieVillagerEntity>create(SpawnGroup.MISC, MasiZombieVillagerEntity::new)
                    .trackRangeChunks(8)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build());
    public static final EntityType<MasiZombifiedPiglinEntity> MASI_ZOMBIE_PIGLIN = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"masi_zombified_piglin"),
            FabricEntityTypeBuilder.<MasiZombifiedPiglinEntity>create(SpawnGroup.MISC, MasiZombifiedPiglinEntity::new)
                    .trackRangeChunks(8)
                    .fireImmune()
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build());
    public static final EntityType<MasiZombieEntity> MASI_ZOMBIE = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"masi_zombie"),
            FabricEntityTypeBuilder.<MasiZombieEntity>create(SpawnGroup.MISC, MasiZombieEntity::new)
                    .trackRangeChunks(8)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build());
    public static final EntityType<MasiDrownedEntity> MASI_DROWNED = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"masi_drowned"),
            FabricEntityTypeBuilder.<MasiDrownedEntity>create(SpawnGroup.MISC, MasiDrownedEntity::new)
                    .trackRangeChunks(8)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.95f)).build());

    public static final EntityType<MasiSkeletonEntity> MASI_SKELETON = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"masi_skeleton"),
            FabricEntityTypeBuilder.<MasiSkeletonEntity>create(SpawnGroup.MISC, MasiSkeletonEntity::new)
                    .trackRangeChunks(8)
                    .dimensions(EntityDimensions.fixed(0.6f, 1.99f)).build());

    public static final EntityType<MasiWitherSkeletonEntity> MASI_WITHER_SKELETON = Registry.register(Registries.ENTITY_TYPE,
            new Identifier(Masi.MOD_ID,"masi_wither_skeleton"),
            FabricEntityTypeBuilder.<MasiWitherSkeletonEntity>create(SpawnGroup.MISC, MasiWitherSkeletonEntity::new)
                    .trackRangeChunks(8)
                    .fireImmune()
                    .dimensions(EntityDimensions.fixed(0.7f, 2.4f)).build());
}