package com.lemg.masi.mixin;

import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.item.Magics.SpacePackMagic;
import com.lemg.masi.util.MagicUtil;
import com.lemg.masi.util.MapPersistence;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.level.ServerWorldProperties;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
		if(((ServerWorld)(Object)this).getRegistryKey().getValue() == World.OVERWORLD.getValue()){
			if (file.exists()) {
				ConcurrentHashMap<String,List<Integer>> uuidPos = MapPersistence.loadPacksFromFile(this.getServer().getSavePath(WorldSavePath.PLAYERS).getParent().resolve("masi_packs.dat"));
				if (uuidPos!=null && !uuidPos.isEmpty()) {
					for(String uuid : uuidPos.keySet()){
						ConcurrentHashMap<BlockPos, List<Object>> blocksAndpos = new ConcurrentHashMap<>();
						List<Integer> pos = uuidPos.get(uuid);
						Box box = new Box(pos.get(0)-5,pos.get(1)-1,pos.get(2)-5,pos.get(0)+5,pos.get(1)+10,pos.get(2)+5);
						int x = (int) box.minX;
						int y = (int) box.minY;
						int z = (int) box.minZ;
						int rx = 0;
						int ry = 0;
						int rz = 0;
						for(;x<box.maxX;x++){
							for(;z<box.maxZ;z++){
								for (;y<box.maxY;y++){
									BlockPos blockPos = new BlockPos(x,y,z);
									BlockState blockState = ((ServerWorld)(Object)this).getBlockState(blockPos);
									List<Object> list = new ArrayList<>();
									list.add(blockState);
									if(blockState.hasBlockEntity()){
										BlockEntity blockEntity = ((ServerWorld)(Object)this).getBlockEntity(blockPos);
										list.add(blockEntity);
										((ServerWorld)(Object)this).removeBlockEntity(blockPos);
									}
									BlockPos blockPos2 = new BlockPos(rx,ry,rz);
									blocksAndpos.put(blockPos2, list);
									((ServerWorld)(Object)this).setBlockState(blockPos, Blocks.AIR.getDefaultState());
									ry++;
								}
								y = (int) box.minY;
								ry = 0;
								rz++;
							}
							z = (int) box.minZ;
							rz=0;
							rx++;
						}
						SpacePackMagic.packs.put(uuid,blocksAndpos);
					}
				}
				file.delete();
			}
		}
		boolean bl = !((ServerWorld)(Object)this).getPlayers().isEmpty() || !((ServerWorld)(Object)this).getForcedChunks().isEmpty();
		if (bl || this.idleTimeout++ < 300) {
			//所有进行中的魔法效果
			if (!MagicUtil.EFFECT.isEmpty()) {
				//所有受魔法效果的目标
				if(MagicUtil.EFFECT.get(((ServerWorld)(Object)this))!=null){
					for(Object object : MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).keySet()){
						ConcurrentHashMap<LivingEntity, ConcurrentHashMap<Magic, Integer>> map2 = MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).get(object);
						if(map2!=null){
							//施加者和它施加的魔法效果
							for (LivingEntity livingEntity : map2.keySet()) {
								ConcurrentHashMap<Magic, Integer> map1 = map2.get(livingEntity);
								if(map1!=null){
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
							}
							MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).put(object, map2);
							//目标没有效果了
							if (MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).get(object).isEmpty()) {
								MagicUtil.EFFECT.get(((ServerWorld)(Object)this)).remove(object);
							}
						}
				    }
				}
			}
			profiler.pop();
		}
	}
}