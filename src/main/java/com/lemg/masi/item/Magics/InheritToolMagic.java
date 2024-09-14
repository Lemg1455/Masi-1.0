package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.ArcaneMinionEntity;
import com.lemg.masi.item.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterials;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InheritToolMagic extends Magic{
    public InheritToolMagic(Settings settings) {
        super(settings);
    }
    @Override
    public int singFinishTick(){
        return 20;
    }

    @Override
    public int energyConsume(){
        return 40;
    }
    @Override
    public int studyNeed(){
        return 5;
    }

    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(user.getOffHandStack().isEmpty()){
            return;
        }
        ItemStack material = user.getStackInHand(Hand.OFF_HAND);
        ItemStack tool = new ItemStack(ModItems.INHERIT_TOOL_ITEM);
        NbtCompound nbt = new NbtCompound();
        if(!material.isEmpty()){
            if(material.getItem()==Items.COBBLESTONE){
                nbt.putFloat("miningSpeed", ToolMaterials.STONE.getMiningSpeedMultiplier());
                nbt.putFloat("attackDamage",ToolMaterials.STONE.getAttackDamage());
            }else if(material.getItem()==Items.IRON_INGOT){
                nbt.putFloat("miningSpeed", ToolMaterials.IRON.getMiningSpeedMultiplier());
                nbt.putFloat("attackDamage",ToolMaterials.IRON.getAttackDamage());
            }else if(material.getItem()==Items.GOLD_INGOT){
                nbt.putFloat("miningSpeed", ToolMaterials.GOLD.getMiningSpeedMultiplier());
                nbt.putFloat("attackDamage",ToolMaterials.GOLD.getAttackDamage());
            }else if(material.getItem()==Items.DIAMOND){
                nbt.putFloat("miningSpeed", ToolMaterials.DIAMOND.getMiningSpeedMultiplier());
                nbt.putFloat("attackDamage",ToolMaterials.DIAMOND.getAttackDamage());
            }else if(material.getItem()==Items.NETHERITE_INGOT){
                nbt.putFloat("miningSpeed", ToolMaterials.NETHERITE.getMiningSpeedMultiplier());
                nbt.putFloat("attackDamage",ToolMaterials.NETHERITE.getAttackDamage());
            }else {
                nbt.putFloat("miningSpeed", ToolMaterials.WOOD.getMiningSpeedMultiplier());
                nbt.putFloat("attackDamage",ToolMaterials.WOOD.getAttackDamage());
            }
            tool.setNbt(nbt);
            tool.setCustomName(Text.literal(tool.getName().getString()+material.getItem().getName().getString()));
        }
        if(user instanceof PlayerEntity playerEntity){
            material.decrement(1);
            if (material.isEmpty()) {
                playerEntity.getInventory().removeOne(material);
            }
            if(playerEntity.getInventory().getEmptySlot()!=-1){
                playerEntity.getInventory().insertStack(tool);
            }else {
                playerEntity.dropStack(tool);
            }
        }
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_GROUND_BLACK, user.getX(),user.getY(),user.getZ(), 0, 0, 0.0, 0, 0.0);
            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 1;
                double z = user.getZ() + Math.sin(yawRadians) * 1;
                double y = user.getY()+2;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_BLACK, x,y,z, 0, 0, 0.0, 0, 0.0);
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.inherit_tool_magic.tooltip"));
        tooltip.add(Text.translatable("item.masi.inherit_tool_magic.tooltip2"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
