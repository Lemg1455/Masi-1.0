package com.lemg.masi;

import com.lemg.masi.enchantment.EnergyConservationEnchantment;
import com.lemg.masi.enchantment.MultipleReleaseEnchantment;
import com.lemg.masi.item.MagicGroups;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.item.ModitemGroup;
import com.lemg.masi.network.ModMessage;
import com.mojang.serialization.Lifecycle;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.MultishotEnchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Masi implements ModInitializer {

	public static final String MOD_ID = "masi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final DefaultParticleType CIRCLE_FORWARD_BLUE = FabricParticleTypes.simple(true);
	public static final DefaultParticleType CIRCLE_GROUND_BLUE = FabricParticleTypes.simple(true);
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
		Registry.register(Registries.PARTICLE_TYPE, new Identifier("masi", "circle_ground_blue"), CIRCLE_GROUND_BLUE);

		Registry.register(Registries.ENCHANTMENT, new Identifier("masi", "multiple_release"),MULTIPLE_RELEASE);
		Registry.register(Registries.ENCHANTMENT, new Identifier("masi", "energy_conservation"),ENERGY_CONSERVATION);


	}
}