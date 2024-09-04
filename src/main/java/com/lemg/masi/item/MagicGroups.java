package com.lemg.masi.item;

import net.minecraft.item.*;
import net.minecraft.text.Text;
import java.util.*;


public class MagicGroups {
    public static ArrayList<List<Object>> magicGroups = new ArrayList<>();
    public static void addMagicGroup(List<Item> items,Text groupName,int Row,int column,ItemStack icon){
        List<Object> magicGroup = Arrays.asList(items,groupName,Row,column,icon);//添加一个魔法组(魔法列表，组名，显示行列，选项卡图标)
        magicGroups.add(magicGroup);
    }

    public static void registryMagicGroups(){
        List<Item> items = Arrays.asList(ModItems.FIRE_BALL_MAGIC,ModItems.CREATING_WATER_MAGIC,ModItems.HEAL_MAGIC,ModItems.LIGHTNING_MAGIC,ModItems.GAIN_CIRCLE_MAGIC,ModItems.LOSE_CIRCLE_MAGIC,ModItems.COLD_DISASTER_MAGIC,ModItems.PURIFICATION_MAGIC,ModItems.ELEMENT_AGGREGATION_MAGIC,ModItems.ELEMENTAL_BLESSING_MAGIC);
        addMagicGroup(items,Text.translatable("magicGroup.elemental"),0,0,ModItems.FIRE_BALL_MAGIC.getDefaultStack());

        items = Arrays.asList(ModItems.ARCANE_SHIELD_MAGIC, ModItems.ARCANE_MISSILE_MAGIC,ModItems.ARCANE_TORRENT_MAGIC,ModItems.ARCANE_MINION_MAGIC,ModItems.ARCANE_TENTACLES_MAGIC);
        addMagicGroup(items,Text.translatable("magicGroup.arcane"),0,1,ModItems.ARCANE_MISSILE_MAGIC.getDefaultStack());

        items = Arrays.asList(ModItems.FLICKER_MAGIC,ModItems.FLY_MAGIC,ModItems.DIMENSION_EXILE_MAGIC,ModItems.IMPRISON_MAGIC,ModItems.INGESTION_MAGIC,ModItems.SHADOW_MANE_MAGIC,ModItems.STEALTH_MAGIC);
        addMagicGroup(items,Text.translatable("magicGroup.space"),0,2,ModItems.FLICKER_MAGIC.getDefaultStack());

        items = Arrays.asList(ModItems.RANDOM_MAGIC,ModItems.TAUNT_MAGIC,ModItems.STEAL_MAGIC,ModItems.DEATH_DECLARATION_MAGIC,ModItems.REVENGE_MAGIC);
        addMagicGroup(items,Text.translatable("magicGroup.misc"),0,3,ModItems.RANDOM_MAGIC.getDefaultStack());
    }

    public static List<Object> getDefaultTab() {
        if(magicGroups.get(0)!=null){
            return magicGroups.get(0);
        }
        return null;
    }

    public static List<Object> getSearchGroup() {
        return magicGroups.get(0);
    }
}
