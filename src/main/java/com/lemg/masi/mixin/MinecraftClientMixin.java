package com.lemg.masi.mixin;

import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;

@Mixin(MinecraftClient.class)


public abstract class MinecraftClientMixin {
	@Shadow @Nullable public ClientWorld world;

	@Inject(at = @At("TAIL"), method = "tick")
	public void tick(CallbackInfo ci) {
		if(world==null){
			return;
		}
		Profiler profiler = this.world.getProfiler();
		profiler.push("magicEffectTicks");
		//所有进行中的魔法效果
		if (!MagicUtil.EFFECT.isEmpty()) {
			//所有受魔法效果的目标
			if(MagicUtil.EFFECT.get(this.world)!=null){
				for(Object object : MagicUtil.EFFECT.get(this.world).keySet()){
					ConcurrentHashMap<LivingEntity, ConcurrentHashMap<Magic, Integer>> map2 = MagicUtil.EFFECT.get(this.world).get(object);
					//施加者和它施加的魔法效果
					for (LivingEntity livingEntity : map2.keySet()) {
						ConcurrentHashMap<Magic, Integer> map1 = map2.get(livingEntity);
						//每种效果以及它对应的时间
						for (Magic magic : map1.keySet()) {
							int time = map1.get(magic);
							//触发对应魔法中的效果
							magic.magicEffect(livingEntity.getStackInHand(Hand.MAIN_HAND), (this.world), livingEntity, object, time);

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
					MagicUtil.EFFECT.get(this.world).put(object, map2);
					//目标没有效果了
					if (MagicUtil.EFFECT.get(this.world).get(object).isEmpty()) {
						MagicUtil.EFFECT.get(this.world).remove(object);
					}
			    }
			}
		}
		profiler.pop();
	}
}