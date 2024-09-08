package com.lemg.masi.mixin;

import com.lemg.masi.item.EnergyBottle;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.item.Magics.SpacePackMagic;
import com.lemg.masi.item.Staff;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import com.lemg.masi.util.MapPersistence;
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
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BooleanSupplier;

@Mixin(ServerWorld.class)


public abstract class ServerWorldMixin{
	@Shadow @Final private ServerWorldProperties worldProperties;
	@Shadow private int idleTimeout;

	@Shadow public abstract List<ServerPlayerEntity> getPlayers();

	@Shadow
	public abstract @NotNull MinecraftServer getServer();

	@Unique
	int count = 0;
	@Inject(at = @At("TAIL"), method = "tick")
	public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) throws IOException, ClassNotFoundException {

		Profiler profiler = ((ServerWorld)(Object)this).getProfiler();
		profiler.push("magicEffectTicks");
		File file = this.getServer().getSavePath(WorldSavePath.PLAYERS).getParent().resolve("masi_packs.dat").toFile();
		Identifier l = ((ServerWorld)(Object)this).getRegistryKey().getValue();
		Identifier k = new Identifier("overworld");
		System.out.println(l);
		/*if(l == World.OVERWORLD.getValue()){
			System.out.println(l);
			System.out.println(Objects.equals(l, k));
		}*/
		if (file.exists()) {
			ConcurrentHashMap<String,List<Integer>> uuidPos = MapPersistence.loadPacksFromFile(this.getServer().getSavePath(WorldSavePath.PLAYERS).getParent().resolve("masi_packs.dat"));
			file.delete();
		}
		boolean bl = !((ServerWorld)(Object)this).getPlayers().isEmpty() || !((ServerWorld)(Object)this).getForcedChunks().isEmpty();
		if (bl || this.idleTimeout++ < 300) {
			//所有进行中的魔法效果
			if (!MagicUtil.EFFECT.isEmpty()) {
				//所有受魔法效果的目标
				if(MagicUtil.EFFECT.get(((ServerWorld)(Object)this))!=null){
					for(Object object : MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).keySet()){
						ConcurrentHashMap<LivingEntity, ConcurrentHashMap<Magic, Integer>> map2 = MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).get(object);
						//施加者和它施加的魔法效果
						for (LivingEntity livingEntity : map2.keySet()) {
							ConcurrentHashMap<Magic, Integer> map1 = map2.get(livingEntity);
							//每种效果以及它对应的时间
							for (Magic magic : map1.keySet()) {
								//触发对应魔法中的效果
								int time = map1.get(magic);
								magic.magicEffect(livingEntity.getStackInHand(Hand.MAIN_HAND), ((ServerWorld)(Object)this), livingEntity, object, time);

								time = MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).get(object).get(livingEntity).get(magic);

								//时长耗尽就移除该效果
								if (time <= 0) {
									map1.remove(magic);
								}

								//时长减少
								if (time > 0) {
									map1.put(magic, time - 1);
								}


							}
							//该施加者的效果都结束了
							if (map1.isEmpty()) {
								map2.remove(livingEntity);
							}
						}
						MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).put(object, map2);
						//目标没有效果了
						if (MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).get(object).isEmpty()) {
							MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).remove(object);
						}
				    }
				}
			}
			profiler.pop();
		}
	}
}