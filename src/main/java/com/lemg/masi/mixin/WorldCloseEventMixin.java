package com.lemg.masi.mixin;

import com.lemg.masi.item.Magics.SpacePackMagic;
import com.lemg.masi.util.MagicUtil;
import com.lemg.masi.util.MapPersistence;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(MinecraftServer.class)
public abstract class WorldCloseEventMixin {
    @Shadow public abstract Path getSavePath(WorldSavePath worldSavePath);

    @Shadow @Nullable public abstract ServerWorld getWorld(RegistryKey<World> key);

    @Shadow protected abstract void prepareStartRegion(WorldGenerationProgressListener worldGenerationProgressListener);

    @Shadow public abstract boolean save(boolean suppressLogs, boolean flush, boolean force);

    @Shadow public abstract PlayerManager getPlayerManager();

    @Shadow public abstract Set<RegistryKey<World>> getWorldRegistryKeys();

    @Inject(method = "stop", at = @At("RETURN"))
    private void onStop(boolean waitForShutdown, CallbackInfo ci) throws IOException {
        //this.stop();
    }

    /*private void stop() throws IOException {
        if(!SpacePackMagic.packs.isEmpty()){
            Path path = this.getSavePath(WorldSavePath.PLAYERS);
            path = path.getParent().resolve("masi_packs.dat");
            ConcurrentHashMap<String,List<Integer>> uuidPos = new ConcurrentHashMap<>();
            for(String uuid : SpacePackMagic.packs.keySet()){
                if(!this.getWorld(World.OVERWORLD).getPlayerByUuid().getPlayer(UUID.fromString(uuid)).getWorld().isClient()){
                    Random random = new Random();
                    int sx;
                    int sy;
                    int sz;
                    while (true){
                        boolean allAir = true;
                        sx = random.nextInt(player.getBlockPos().getX()-200,player.getBlockPos().getX()+200);
                        sy = random.nextInt(50,200);
                        sz = random.nextInt(player.getBlockPos().getZ()-200,player.getBlockPos().getZ()+200);
                        Box box = new Box(sx-5,sy-1,sz-5,sx+5,sy+10,sz+5);
                        int x = (int) box.minX;
                        int y = (int) box.minY;
                        int z = (int) box.minZ;
                        for(;x<box.maxX;x++){
                            for(;z<box.maxZ;z++){
                                for (;y<box.maxY;y++){
                                    BlockPos blockPos = new BlockPos(x,y,z);
                                    BlockState blockState = player.getWorld().getBlockState(blockPos);
                                    if(blockState.getBlock()!=Blocks.AIR){
                                        allAir=false;
                                    }
                                }
                                y = (int) box.minY;
                            }
                            z = (int) box.minZ;
                        }
                        if(allAir){
                            break;
                        }
                    }
                    ConcurrentHashMap<BlockPos, List<Object>> blocksAndpos = SpacePackMagic.packs.get(player);
                    if(blocksAndpos!=null && !blocksAndpos.isEmpty()){
                        for(BlockPos blockPos : blocksAndpos.keySet()){
                            BlockPos blockPos1 = new BlockPos(sx-5+blockPos.getX(),sy-1+blockPos.getY(),sz-5+blockPos.getZ());
                            player.getWorld().setBlockState(blockPos1, (BlockState) blocksAndpos.get(blockPos).get(0));
                            if(blocksAndpos.get(blockPos).size()==2){
                                BlockEntity blockEntity = (BlockEntity) blocksAndpos.get(blockPos).get(1);
                                NbtCompound nbt = blockEntity.createNbt();
                                BlockEntity blockEntity1 = blockEntity.getType().instantiate(blockPos1,(BlockState) blocksAndpos.get(blockPos).get(0));
                                if(blockEntity1!=null){
                                    blockEntity1.readNbt(nbt);
                                }
                                player.getWorld().addBlockEntity(blockEntity1);
                            }
                        }
                        SpacePackMagic.packs.remove(player);
                        BlockPos savePos = new BlockPos(sx,sy,sz);
                        System.out.println(savePos);
                        uuidPos.put(player.getUuidAsString(),List.of(savePos.getX(),savePos.getY(),savePos.getZ()));
                    }
                }
            }
            MapPersistence.savePacksToFile(uuidPos,path);
        }
    }*/
}