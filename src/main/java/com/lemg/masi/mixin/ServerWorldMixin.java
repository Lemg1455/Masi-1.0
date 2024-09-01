package com.lemg.masi.mixin;

import com.lemg.masi.item.EnergyBottle;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.item.Staff;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.level.ServerWorldProperties;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)


public abstract class ServerWorldMixin{
	@Shadow @Final private ServerWorldProperties worldProperties;
	@Shadow private int idleTimeout;

	@Shadow public abstract List<ServerPlayerEntity> getPlayers();

	@Unique
	int count = 0;
	@Inject(at = @At("TAIL"), method = "tick")
	public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {

		Profiler profiler = ((ServerWorld)(Object)this).getProfiler();
		profiler.push("magicEffectTicks");
		boolean bl = !((ServerWorld)(Object)this).getPlayers().isEmpty() || !((ServerWorld)(Object)this).getForcedChunks().isEmpty();
		if (bl || this.idleTimeout++ < 300) {
			//所有进行中的魔法效果
			if (!MagicUtil.EFFECT.isEmpty()) {
				//所有受魔法效果的目标
				for(Object object : MagicUtil.EFFECT.keySet()){
					ConcurrentHashMap<LivingEntity, ConcurrentHashMap<Magic, Integer>> map2 = MagicUtil.EFFECT.get(object);
					//施加者和它施加的魔法效果
					for (LivingEntity livingEntity : map2.keySet()) {
						ConcurrentHashMap<Magic, Integer> map1 = map2.get(livingEntity);
						//每种效果以及它对应的时间
						for (Magic magic : map1.keySet()) {
							int time = map1.get(magic);
							//触发对应魔法中的效果
							magic.magicEffect(livingEntity.getStackInHand(Hand.MAIN_HAND), ((ServerWorld)(Object)this), livingEntity, object, time);

							PacketByteBuf buf = PacketByteBufs.create();
							if(object instanceof Entity entity){
								buf.writeInt(1);
								buf.writeInt(entity.getId());
							} else if (object instanceof BlockPos blockPos) {
								buf.writeInt(2);
								buf.writeBlockPos(blockPos);
							} else if (object instanceof BlockHitResult blockHitResult) {
								buf.writeInt(3);
								buf.writeBlockHitResult(blockHitResult);
							} else if (object instanceof Vector3f vector3f) {
								buf.writeInt(4);
								buf.writeVector3f(vector3f);
							} else if (object instanceof ItemStack itemStack) {
								buf.writeInt(5);
								buf.writeItemStack(itemStack);
							}else {
								buf.writeInt(0);
							}
							buf.writeItemStack(magic.getDefaultStack());
							//buf.writeItemStack(livingEntity.getStackInHand(Hand.MAIN_HAND));


							buf.writeInt(livingEntity.getId());
							buf.writeInt(time);
							for(ServerPlayerEntity serverPlayerEntity : this.getPlayers()){
								ServerPlayNetworking.send(serverPlayerEntity, ModMessage.MAGIC_EFFECT_ID, buf);
							}

							//时长减少
							if (time >= 0) {
								map1.put(magic, time - 1);
							}
							//时长耗尽就移除该效果
							if (map1.get(magic) < 0) {
								map1.remove(magic);
							}
						}
						//该施加者的效果都结束了
						if (map1.isEmpty()) {
							map2.remove(livingEntity);
						}
					}
					MagicUtil.EFFECT.put(object, map2);
					//目标没有效果了
					if (MagicUtil.EFFECT.get(object).isEmpty()) {
						MagicUtil.EFFECT.remove(object);
					}
				}
			}
			profiler.pop();
		}
	}
}