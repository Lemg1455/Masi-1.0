package com.lemg.masi.mixin;

import com.lemg.masi.item.EnergyBottle;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.item.Staff;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {
	@Inject(at = @At("HEAD"), method = "writeNbt")
	public void writeNbt(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
		if((Entity)(Object)this instanceof PlayerEntity player){
			nbt.putInt("mixin",111111);
		}
	}
}