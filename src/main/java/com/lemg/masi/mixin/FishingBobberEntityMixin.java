package com.lemg.masi.mixin;

import com.lemg.masi.item.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin {

	@Inject(at = @At("HEAD"), method = "removeIfInvalid", cancellable = true)
	public void removeIfInvalid(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
		ItemStack itemStack = player.getMainHandStack();
		ItemStack itemStack2 = player.getOffHandStack();
		boolean bl = itemStack.isOf(ModItems.INHERIT_TOOL_ITEM) || itemStack.isOf(Items.FISHING_ROD);;
		boolean bl2 = itemStack2.isOf(ModItems.INHERIT_TOOL_ITEM) || itemStack2.isOf(Items.FISHING_ROD);;
		if (player.isRemoved() || !player.isAlive() || !bl && !bl2 || ((FishingBobberEntity)(Object)this).squaredDistanceTo(player) > 1024.0) {
			((FishingBobberEntity)(Object)this).discard();
			cir.setReturnValue(true);
		}
		cir.setReturnValue(false);
	}
}