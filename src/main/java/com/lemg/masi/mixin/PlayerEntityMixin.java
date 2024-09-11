package com.lemg.masi.mixin;

import com.lemg.masi.Masi;
import com.lemg.masi.item.*;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

	@Shadow public abstract void sendMessage(Text message, boolean overlay);

	@Inject(at = @At("TAIL"), method = "tick")
	public void tick(CallbackInfo ci) {
		PlayerEntity player = ((PlayerEntity)(Object)this);
		//如果魔法在释放后持续攻击，减少持续时间
		if(MagicUtil.TIME_REQUIRED.get(this)!=null){
			List<Object> list = MagicUtil.TIME_REQUIRED.get(this);
			Magic magic = (Magic)list.get(0);
			int time_required = (int)list.get(1);
			if(time_required>=0){
				//持续攻击
				if(magic.releaseContinueTime()>0 && player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof Staff){
					magic.release(player.getStackInHand(Hand.MAIN_HAND), player.getWorld(), player, time_required);
					//多重释放
				}else if(magic.Multiple()){
					if(time_required%10==0){
						magic.release(player.getStackInHand(Hand.MAIN_HAND), player.getWorld(), player, magic.singFinishTick());
					}
				}
			}
			if(!player.getWorld().isClient()){
				if(time_required>=0){
					time_required--;
				}

				if(!(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof Staff)){
					time_required=-1;
				}
				list.set(1,time_required);
				MagicUtil.TIME_REQUIRED.put(((PlayerEntity)(Object)this),list);

				PacketByteBuf buf = PacketByteBufs.create();
				buf.writeInt(time_required);
				ServerPlayNetworking.send((ServerPlayerEntity) player, ModMessage.TIME_REQUIRED_ID, buf);
			}
		}

		Item magic = Staff.UsersMagic.get(player);
		if(magic!=null){
			if(player.isUsingItem() && player.getStackInHand(player.getActiveHand()).getItem() instanceof Staff) {
				if(magic instanceof Magic magic1){
					magic1.onSinging(player.getStackInHand(player.getActiveHand()),player.getWorld(),player, player.getItemUseTime());//咏唱开始之后，释放之前期间的效果
				}
			}
		}

		//每秒恢复魔力
		if(!player.getWorld().isClient()){
			if(player.age%20==0){
				if(MagicUtil.ENERGY.get(player)!=null){
					if(player.isUsingItem()){
						Item item = player.getStackInHand(player.getActiveHand()).getItem();
						if(item instanceof Staff || item instanceof EnergyBottle){
							return;
						}
					}
					if(MagicUtil.ENERGY.get(player)<MagicUtil.MAX_ENERGY.get(player)){
						int energy = MagicUtil.ENERGY.get(player)+MagicUtil.ENERGY_RESTORED.get(player);
						if(energy>=MagicUtil.MAX_ENERGY.get(player)){
							energy=MagicUtil.MAX_ENERGY.get(player);
						}

						MagicUtil.energyUpdate(player,energy,false);

					}
				}
			}
		}

		if(MagicUtil.EQUIP_MAGICS.get(player)!=null){
			List<Item> items = MagicUtil.EQUIP_MAGICS.get(player);
			if(!items.isEmpty()){
				for(Item item : items){
					if(item instanceof Magic magic1){
						if(magic1.passive()){
							magic1.release(player.getStackInHand(player.getActiveHand()),player.getWorld(),player, player.age);
						}
					}
				}
			}
		}
		/*if(MagicUtil.ENERGY.get(player)!=null){
			if(MagicUtil.ENERGY.get(player)<10){
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, 0,false,false,false));
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60, 1,false,false,false));
			}
		}*/
	}
}