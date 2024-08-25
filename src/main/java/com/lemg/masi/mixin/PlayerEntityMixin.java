package com.lemg.masi.mixin;

import com.lemg.masi.Masi;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.item.Staff;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
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

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

	@Shadow public abstract void sendMessage(Text message, boolean overlay);


	@Inject(at = @At("TAIL"), method = "tick")
	public void tick(CallbackInfo ci) {
		this.sendMessage(Text.literal(String.valueOf(MagicUtil.TIME_REQUIRED.get(this))),true);
		PlayerEntity player = ((PlayerEntity)(Object)this);
		if(!player.getWorld().isClient()){
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
						System.out.println(time_required);
						if(time_required%10==0){

							magic.release(player.getStackInHand(Hand.MAIN_HAND), player.getWorld(), player, magic.singFinishTick());
						}
					}
				}

				if(time_required>=0){
					time_required--;
				}

				if(!(player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof Staff)){
					time_required=0;
				}
				list.set(1,time_required);
				MagicUtil.TIME_REQUIRED.put(((PlayerEntity)(Object)this),list);

				PacketByteBuf buf = PacketByteBufs.create();
				buf.writeInt(time_required);
				ServerPlayNetworking.send((ServerPlayerEntity) player, ModMessage.TIME_REQUIRED_ID, buf);
			}

			//玩家赋予给目标的魔法效果
			if(MagicUtil.EFFECT.get(this)!=null){
				Map<Object, Map<Magic,Integer>> map2 = MagicUtil.EFFECT.get(this);
				//目标以及它身上的效果
				for(Object object : map2.keySet()){
					Map<Magic,Integer> map1 = map2.get(object);
					//每种效果以及它对应的时间
					for(Magic magic : map1.keySet()){
						int time = map1.get(magic);
						//触发对应魔法中的效果
						magic.magicEffect(player.getStackInHand(Hand.MAIN_HAND),player.getWorld(),player,object,time);
						//时长减少
						if(time>=0){
							map1.put(magic,time-1);
						}
						//时长耗尽就移除该效果
						if(map1.get(magic)<0){
							map1.remove(magic);
						}
					}
					//目标身上没有效果了，就移除该目标
					if(map1.isEmpty()){
						map2.remove(object);
					}
				}
				MagicUtil.EFFECT.put(player,map2);
				//玩家没有目标了，移除该玩家
				if(MagicUtil.EFFECT.get(this).isEmpty()){
					MagicUtil.EFFECT.remove(this);
				}
			}
		}
	}
}