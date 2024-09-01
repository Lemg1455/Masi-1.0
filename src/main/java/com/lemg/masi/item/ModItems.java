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
    public static final Item MAGE_CERTIFICATE = registerItems("mage_certificate",new MageCertificate(new FabricItemSettings().maxCount(1).fireproof().rarity(Rarity.COMMON)));
    public static final Item STAFF = registerItems("staff",new Staff(new FabricItemSettings().maxCount(1).maxDamage(64).rarity(Rarity.COMMON)));
    public static final Item MAGIC_SCROLL = registerItems("magic_scroll",new MagicScroll(new FabricItemSettings().maxCount(1).rarity(Rarity.RARE)));
    public static final Item ENERGY_BOTTLE = registerItems("energy_bottle",new EnergyBottle(new FabricItemSettings().maxCount(64).rarity(Rarity.EPIC)));
    public static final Item MAX_ENERGY_BOTTLE = registerItems("max_energy_bottle",new MaxEnergyBottle(new FabricItemSettings().maxCount(64).rarity(Rarity.EPIC)));
    public static final Item TRIAL_CARD = registerItems("trial_card",new TrialCard(new FabricItemSettings().maxCount(64).rarity(Rarity.UNCOMMON)));


    //Magics
    public static final Item FIRE_BALL_MAGIC = registerItems("fire_ball_magic",new FireBallMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)));
    public static final Item CREATING_WATER_MAGIC = registerItems("creating_water_magic",new CreatingWaterMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)));
    public static final Item HEAL_MAGIC = registerItems("heal_magic",new HealMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)));
    public static final Item FLICKER_MAGIC = registerItems("flicker_magic",new FlickerMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)));
    public static final Item ARCANE_SHIELD_MAGIC = registerItems("arcane_shield",new ArcaneShieldMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.COMMON)));
    public static final Item RANDOM_MAGIC = registerItems("random_magic",new RandomMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item LIGHTNING_MAGIC = registerItems("lightning_magic",new LightningMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ARCANE_MISSILE_MAGIC = registerItems("arcane_missile_magic",new ArcaneMissileMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item GAIN_CIRCLE_MAGIC = registerItems("gain_circle_magic",new GainCircleMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item FLY_MAGIC = registerItems("fly_magic",new FlyMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item DIMENSION_EXILE_MAGIC = registerItems("dimension_exile_magic",new DimensionExileMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item IMPRISON_MAGIC = registerItems("imprison_magic",new ImprisonMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item INGESTION_MAGIC = registerItems("ingestion_magic",new IngestionMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item TAUNT_MAGIC = registerItems("taunt_magic",new TauntMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item LOSE_CIRCLE_MAGIC = registerItems("lose_circle_magic",new LoseCircleMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item SHADOW_MANE_MAGIC = registerItems("shadow_mane_magic",new ShadowManeMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item STEAL_MAGIC = registerItems("steal_magic",new StealMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item STEALTH_MAGIC = registerItems("stealth_magic",new StealthMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ARCANE_TORRENT_MAGIC = registerItems("arcane_torrent_magic",new ArcaneTorrentMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item COLD_DISASTER_MAGIC = registerItems("cold_disaster_magic",new ColdDisasterMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item PURIFICATION_MAGIC = registerItems("purification_magic",new PurificationMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item DEATH_DECLARATION_MAGIC = registerItems("death_declaration_magic",new DeathDeclarationMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ARCANE_MINION_MAGIC = registerItems("arcane_minion_magic",new ArcaneMinionMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ELEMENT_AGGREGATION_MAGIC = registerItems("element_aggregation_magic",new ElementAggregationMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ELEMENTAL_BLESSING_MAGIC = registerItems("elemental_blessing_magic",new ElementalBlessingMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));
    public static final Item ARCANE_TENTACLES_MAGIC = registerItems("arcane_tentacles_magic",new ArcaneTentaclesMagic(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC)));




    //Bullets
    public static final Item LIGHTNING_BULLET = registerItems("lightning_bullet",new Item(new FabricItemSettings().maxDamage(64)));
    public static final Item ARCANE_BULLET = registerItems("arcane_bullet",new Item(new FabricItemSettings().maxDamage(64)));

    private static Item registerItems(String name,Item item){
        return Registry.register(Registries.ITEM,new Identifier(Masi.MOD_ID,name),item);
    }
    public static void registerModItems(){
        Masi.LOGGER.info("Registering Mod Items for " + Masi.MOD_ID);
    }
}
