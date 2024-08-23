package com.lemg.masi.item;

import com.lemg.masi.Masi;
import com.lemg.masi.item.Magics.*;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;


public class ModItems {
    //Items
    public static final Item MAGE_CERTIFICATE = registerItems("mage_certificate",new MageCertificate(new FabricItemSettings().rarity(Rarity.COMMON)));
    public static final Item STAFF = registerItems("staff",new Staff(new FabricItemSettings().maxDamage(64).rarity(Rarity.COMMON)));
    public static final Item MAGIC_SCROLL = registerItems("magic_scroll",new MagicScroll(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item ENERGY_BOTTLE = registerItems("energy_bottle",new EnergyBottle(new FabricItemSettings().maxCount(64).rarity(Rarity.EPIC)));
    public static final Item MAX_ENERGY_BOTTLE = registerItems("max_energy_bottle",new MaxEnergyBottle(new FabricItemSettings().maxCount(64).rarity(Rarity.EPIC)));

    //Magics
    public static final Item FIRE_BALL_MAGIC = registerItems("fire_ball_magic",new FireBallMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)));
    public static final Item CREATING_WATER_MAGIC = registerItems("creating_water_magic",new CreatingWaterMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)));
    public static final Item HEAL_MAGIC = registerItems("heal_magic",new HealMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)));
    public static final Item FLICKER_MAGIC = registerItems("flicker_magic",new FlickerMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)));
    public static final Item ARCANE_SHIELD_MAGIC = registerItems("arcane_shield",new ArcaneShieldMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)));
    public static final Item RANDOM_MAGIC = registerItems("random_magic",new RandomMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));

    private static Item registerItems(String name,Item item){
        return Registry.register(Registries.ITEM,new Identifier(Masi.MOD_ID,name),item);
    }
    public static void registerModItems(){
        Masi.LOGGER.info("Registering Mod Items for " + Masi.MOD_ID);
    }
}
