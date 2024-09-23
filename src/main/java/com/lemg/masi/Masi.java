package com.lemg.masi;

import com.lemg.masi.enchantment.EnergyConservationEnchantment;
import com.lemg.masi.enchantment.MultipleReleaseEnchantment;
import com.lemg.masi.entity.entities.minions.ArcaneMinionEntity;
import com.lemg.masi.entity.ModEntities;
import com.lemg.masi.entity.entities.SwordEnergyEntity;
import com.lemg.masi.entity.entities.minions.LightningCloudEntity;
import com.lemg.masi.entity.entities.minions.SwordManEntity;
import com.lemg.masi.item.MagicGroups;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.item.ModitemGroup;
import com.lemg.masi.network.ModMessage;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.DrownedEntity;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.mob.WitherSkeletonEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Masi implements ModInitializer {

	public static final String MOD_ID = "masi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final DefaultParticleType CIRCLE_FORWARD_BLUE = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_FORWARD_BLUE = FabricParticleTypes.simple(true);
	public static final DefaultParticleType CIRCLE_GROUND_BLUE = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_GROUND_BLUE = FabricParticleTypes.simple(true);

	public static final DefaultParticleType CIRCLE_FORWARD_BLACK = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_FORWARD_BLACK = FabricParticleTypes.simple(true);
	public static final DefaultParticleType CIRCLE_GROUND_BLACK = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_GROUND_BLACK = FabricParticleTypes.simple(true);

	public static final DefaultParticleType CIRCLE_FORWARD_GREEN = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_FORWARD_GREEN = FabricParticleTypes.simple(true);
	public static final DefaultParticleType CIRCLE_GROUND_GREEN = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_GROUND_GREEN = FabricParticleTypes.simple(true);

	public static final DefaultParticleType CIRCLE_FORWARD_PURPLE = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_FORWARD_PURPLE = FabricParticleTypes.simple(true);
	public static final DefaultParticleType CIRCLE_GROUND_PURPLE = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_GROUND_PURPLE = FabricParticleTypes.simple(true);

	public static final DefaultParticleType CIRCLE_FORWARD_RED = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_FORWARD_RED = FabricParticleTypes.simple(true);
	public static final DefaultParticleType CIRCLE_GROUND_RED = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_GROUND_RED = FabricParticleTypes.simple(true);

	public static final DefaultParticleType CIRCLE_FORWARD_WHITE = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_FORWARD_WHITE = FabricParticleTypes.simple(true);
	public static final DefaultParticleType CIRCLE_GROUND_WHITE = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_GROUND_WHITE = FabricParticleTypes.simple(true);

	public static final DefaultParticleType CIRCLE_FORWARD_YELLOW = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_FORWARD_YELLOW = FabricParticleTypes.simple(true);
	public static final DefaultParticleType CIRCLE_GROUND_YELLOW = FabricParticleTypes.simple(true);
	public static final DefaultParticleType LARGE_CIRCLE_GROUND_YELLOW = FabricParticleTypes.simple(true);
	public static final DefaultParticleType MAGIC_SWORD_SWEEP = FabricParticleTypes.simple(true);

	public static Enchantment MULTIPLE_RELEASE = new MultipleReleaseEnchantment(Enchantment.Rarity.RARE, EquipmentSlot.MAINHAND);
	public static Enchantment ENERGY_CONSERVATION = new EnergyConservationEnchantment(Enchantment.Rarity.UNCOMMON, EquipmentSlot.MAINHAND);

	@Override
	public void onInitialize() {
		LOGGER.info("Hello Fabric world!");
		ModitemGroup.registerModItemGroup();
		//MagicGroups.registerAndGetDefault(Registries.ITEM_GROUP);
		ModItems.registerModItems();
		ModMessage.registerC2SPackets();

		MagicGroups.registryMagicGroups();

		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_forward_blue"), CIRCLE_FORWARD_BLUE);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_forward_blue"), LARGE_CIRCLE_FORWARD_BLUE);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_ground_blue"), CIRCLE_GROUND_BLUE);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_ground_blue"), LARGE_CIRCLE_GROUND_BLUE);

		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_forward_black"), CIRCLE_FORWARD_BLACK);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_forward_black"), LARGE_CIRCLE_FORWARD_BLACK);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_ground_black"), CIRCLE_GROUND_BLACK);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_ground_black"), LARGE_CIRCLE_GROUND_BLACK);

		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_forward_white"), CIRCLE_FORWARD_WHITE);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_forward_white"), LARGE_CIRCLE_FORWARD_WHITE);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_ground_white"), CIRCLE_GROUND_WHITE);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_ground_white"), LARGE_CIRCLE_GROUND_WHITE);

		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_forward_red"), CIRCLE_FORWARD_RED);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_forward_red"), LARGE_CIRCLE_FORWARD_RED);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_ground_red"), CIRCLE_GROUND_RED);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_ground_red"), LARGE_CIRCLE_GROUND_RED);

		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_forward_green"), CIRCLE_FORWARD_GREEN);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_forward_green"), LARGE_CIRCLE_FORWARD_GREEN);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_ground_green"), CIRCLE_GROUND_GREEN);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_ground_green"), LARGE_CIRCLE_GROUND_GREEN);

		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_forward_yellow"), CIRCLE_FORWARD_YELLOW);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_forward_yellow"), LARGE_CIRCLE_FORWARD_YELLOW);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_ground_yellow"), CIRCLE_GROUND_YELLOW);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_ground_yellow"), LARGE_CIRCLE_GROUND_YELLOW);

		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_forward_purple"), CIRCLE_FORWARD_PURPLE);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_forward_purple"), LARGE_CIRCLE_FORWARD_PURPLE);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_ground_purple"), CIRCLE_GROUND_PURPLE);
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "large_circle_ground_purple"), LARGE_CIRCLE_GROUND_PURPLE);

		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "magic_sword_sweep"), MAGIC_SWORD_SWEEP);

		Registry.register(Registries.ENCHANTMENT, new Identifier("masi", "multiple_release"),MULTIPLE_RELEASE);
		Registry.register(Registries.ENCHANTMENT, new Identifier("masi", "energy_conservation"),ENERGY_CONSERVATION);

		FabricDefaultAttributeRegistry.register(ModEntities.ARCANE_MINION, ArcaneMinionEntity.createArcaneMinionAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SWORD_ENERGY, SwordEnergyEntity.createSwordEnergyAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.METEORITE, SwordEnergyEntity.createSwordEnergyAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.SWORD_MAN, SwordManEntity.createSwordManAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.LIGHTNING_CLOUD, LightningCloudEntity.createLightningCloudAttributes());


		FabricDefaultAttributeRegistry.register(ModEntities.MASI_ZOMBIE, ZombieEntity.createZombieAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MASI_DROWNED, DrownedEntity.createZombieAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MASI_SKELETON, SkeletonEntity.createAbstractSkeletonAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MASI_ZOMBIE_PIGLIN, ZombieEntity.createZombieAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MASI_ZOMBIE_VILLAGER, ZombieEntity.createZombieAttributes());
		FabricDefaultAttributeRegistry.register(ModEntities.MASI_WITHER_SKELETON, WitherSkeletonEntity.createAbstractSkeletonAttributes());


	}
}