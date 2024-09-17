package com.lemg.masi.mixin;

import com.lemg.masi.entity.entities.minions.MasiSkeletonEntity;
import com.lemg.masi.entity.entities.minions.Minion;
import com.lemg.masi.item.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
	/*@Inject(at = @At("HEAD"), method = "getTeamColorValue", cancellable = true)
	public void getTeamColorValue(CallbackInfoReturnable<Integer> cir) {
		Entity entity = ((Entity)(Object)this);
		if(entity instanceof MasiSkeletonEntity){
			cir.setReturnValue(0x000000);
		}
	}*/
}