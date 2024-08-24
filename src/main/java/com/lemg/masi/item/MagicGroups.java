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
        List<Item> items = Arrays.asList(ModItems.FIRE_BALL_MAGIC,ModItems.CREATING_WATER_MAGIC,ModItems.HEAL_MAGIC,ModItems.LIGHTNING_MAGIC);
        addMagicGroup(items,Text.translatable("magicGroup.elemental"),0,0,ModItems.FIRE_BALL_MAGIC.getDefaultStack());

        items = Arrays.asList(ModItems.ARCANE_SHIELD_MAGIC);
        addMagicGroup(items,Text.translatable("magicGroup.arcane"),0,1,ModItems.ARCANE_SHIELD_MAGIC.getDefaultStack());

        items = Arrays.asList(ModItems.FLICKER_MAGIC);
        addMagicGroup(items,Text.translatable("magicGroup.space"),0,2,ModItems.FLICKER_MAGIC.getDefaultStack());

        items = Arrays.asList(ModItems.RANDOM_MAGIC);
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
