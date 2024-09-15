package com.lemg.masi.item.Magics;

import com.lemg.masi.Masi;
import com.lemg.masi.item.ModItems;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

public class TeleportMagic extends Magic{

    public TeleportMagic(Settings settings,int singFinishTick,int energyConsume,int studyNeed) {
        super(settings,singFinishTick,energyConsume,studyNeed);
    }
    @Override
    public void release(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(user.getOffHandStack().getItem() != ModItems.TELEPORT_BEACON){
            ItemStack beacon = new ItemStack(ModItems.TELEPORT_BEACON);
            NbtCompound nbt = new NbtCompound();
            nbt.putInt("x",user.getBlockX());
            nbt.putInt("y",user.getBlockY());
            nbt.putInt("z",user.getBlockZ());
            NbtString string = NbtString.of(NbtString.escape("记录坐标: " + user.getBlockPos().toString()));
            NbtList list = new NbtList();
            list.add(string);
            NbtCompound nbt1 = new NbtCompound();
            nbt1.put("Lore", list);
            nbt.put("display", nbt1);
            beacon.setNbt(nbt);

            if(user instanceof PlayerEntity playerEntity){
                if(playerEntity.getInventory().getEmptySlot()!=-1){
                    playerEntity.getInventory().insertStack(beacon);
                }else {
                    playerEntity.dropStack(beacon);
                }
            }
        }else {
            ItemStack beacon = user.getOffHandStack();
            if(beacon.getNbt()!=null){
                NbtCompound nbt = beacon.getNbt();
                if(nbt.contains("x")){
                    int x = nbt.getInt("x");
                    int y = nbt.getInt("y");
                    int z = nbt.getInt("z");

                    EnumSet<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
                    set.add(PositionFlag.X);
                    set.add(PositionFlag.Y);
                    set.add(PositionFlag.Z);
                    set.add(PositionFlag.X_ROT);
                    set.add(PositionFlag.Y_ROT);

                    if(!world.isClient()){
                        List<Entity> list = world.getOtherEntities(user,user.getBoundingBox().expand(1.5,3,1.5));
                        user.teleport((ServerWorld) world, x,y,z, set, user.getYaw(), user.getPitch());
                        Random random = new Random();
                        for(Entity entity : list){
                            entity.teleport((ServerWorld) world, x+random.nextDouble(2),y,z+random.nextDouble(2), set, entity.getYaw(), entity.getPitch());
                        }
                    }
                }
            }
        }
        super.release(stack,world,user,singingTicks);
    }
    @Override
    public void onSinging(ItemStack stack, World world, LivingEntity user, float singingTicks){
        if(!user.getWorld().isClient()){
            ((ServerWorld)user.getWorld()).spawnParticles(Masi.LARGE_CIRCLE_GROUND_WHITE, user.getX(),user.getY()+0.2,user.getZ(), 0, 0, 0.0, 0, 0.0);

            if(user.getItemUseTime() >= singFinishTick()){
                double yawRadians = Math.toRadians(user.getYaw()+90);
                double x = user.getX() + Math.cos(yawRadians) * 2;
                double z = user.getZ() + Math.sin(yawRadians) * 2;
                double y = user.getY()+4;
                ((ServerWorld)user.getWorld()).spawnParticles(Masi.CIRCLE_FORWARD_WHITE, x,y,z, 0, 0, 0.0, 0, 0.0);

            }
        }
    }


    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.masi.teleport_magic.tooltip"));
        super.appendTooltip(stack,world,tooltip,context);
    }
}
