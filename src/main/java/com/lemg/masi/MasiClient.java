package com.lemg.masi;


import com.lemg.masi.entity.ModEntities;
import com.lemg.masi.entity.client.*;
import com.lemg.masi.event.KeyInputHandler;
import com.lemg.masi.item.items.ArcaneBow;
import com.lemg.masi.item.items.MagicSword;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.item.ModItems;
import com.lemg.masi.item.items.Staff;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.particles.Circle_Forward_Particle;
import com.lemg.masi.particles.Circle_Ground_Particle;
import com.lemg.masi.particles.Magic_Sword_Sweep_Particle;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.mob.ZombieVillagerEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;


public class MasiClient implements ClientModInitializer  {
    public DefaultedList<ItemStack> itemStacks;

    public static final MinecraftClient MC = MinecraftClient.getInstance();
    private static final Identifier MASI_BAR_TEXTURE = new Identifier(Masi.MOD_ID,"textures/gui/masi_bar.png");

    @Override
    public void onInitializeClient() {
        KeyInputHandler.register();
        HudRenderCallback.EVENT.register(this::renderBar);
        ModMessage.registerS2CPackets();

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.ARCANE_MINION, ArcaneMinionEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.ARCANE_MINION, ArcaneMinionEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SWORD_ENERGY, SwordEnergyEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.SWORD_ENERGY, SwordEnergyEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.ARCANE_ARROW, ArcaneArrowEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.METEORITE, MeteoriteEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.METEORITE, MeteoriteEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.SWORD_MAN, SwordManEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.SWORD_MAN, SwordManEntityRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(ModModelLayers.LIGHTNING_CLOUD, LightningCloudEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(ModEntities.LIGHTNING_CLOUD, LightningCloudEntityRenderer::new);

        EntityRendererRegistry.register(ModEntities.MASI_ZOMBIE, ZombieEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MASI_DROWNED, DrownedEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MASI_SKELETON, SkeletonEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MASI_ZOMBIE_PIGLIN, context -> new ZombifiedPiglinEntityRenderer(context, EntityModelLayers.ZOMBIFIED_PIGLIN, EntityModelLayers.ZOMBIFIED_PIGLIN_INNER_ARMOR, EntityModelLayers.ZOMBIFIED_PIGLIN_OUTER_ARMOR, true));
        EntityRendererRegistry.register(ModEntities.MASI_ZOMBIE_VILLAGER, ZombieVillagerEntityRenderer::new);
        EntityRendererRegistry.register(ModEntities.MASI_WITHER_SKELETON, WitherSkeletonEntityRenderer::new);

        registerPullPredicate(ModItems.ARCANE_BOW);
        registerInheritToolPredicate(ModItems.INHERIT_TOOL_ITEM);
        ModelPredicateProviderRegistry.register(ModItems.INHERIT_TOOL_ITEM, new Identifier("cast"), (stack, world, entity, seed) -> {
            boolean bl2;
            if (entity == null) {
                return 0.0f;
            }
            boolean bl = entity.getMainHandStack() == stack;
            boolean bl3 = bl2 = entity.getOffHandStack() == stack;
            if (entity.getMainHandStack().isOf(ModItems.INHERIT_TOOL_ITEM)) {
                bl2 = false;
            }
            return (bl || bl2) && entity instanceof PlayerEntity && ((PlayerEntity)entity).fishHook != null ? 1.0f : 0.0f;
        });

        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_FORWARD_BLUE, Circle_Forward_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_FORWARD_BLUE, Circle_Forward_Particle.LargeFactory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_GROUND_BLUE, Circle_Ground_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_GROUND_BLUE, Circle_Ground_Particle.LargeFactory::new);

        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_FORWARD_BLACK, Circle_Forward_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_FORWARD_BLACK, Circle_Forward_Particle.LargeFactory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_GROUND_BLACK, Circle_Ground_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_GROUND_BLACK, Circle_Ground_Particle.LargeFactory::new);

        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_FORWARD_RED, Circle_Forward_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_FORWARD_RED, Circle_Forward_Particle.LargeFactory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_GROUND_RED, Circle_Ground_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_GROUND_RED, Circle_Ground_Particle.LargeFactory::new);

        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_FORWARD_GREEN, Circle_Forward_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_FORWARD_GREEN, Circle_Forward_Particle.LargeFactory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_GROUND_GREEN, Circle_Ground_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_GROUND_GREEN, Circle_Ground_Particle.LargeFactory::new);

        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_FORWARD_YELLOW, Circle_Forward_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_FORWARD_YELLOW, Circle_Forward_Particle.LargeFactory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_GROUND_YELLOW, Circle_Ground_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_GROUND_YELLOW, Circle_Ground_Particle.LargeFactory::new);

        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_FORWARD_PURPLE, Circle_Forward_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_FORWARD_PURPLE, Circle_Forward_Particle.LargeFactory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_GROUND_PURPLE, Circle_Ground_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_GROUND_PURPLE, Circle_Ground_Particle.LargeFactory::new);

        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_FORWARD_WHITE, Circle_Forward_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_FORWARD_WHITE, Circle_Forward_Particle.LargeFactory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_GROUND_WHITE, Circle_Ground_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.LARGE_CIRCLE_GROUND_WHITE, Circle_Ground_Particle.LargeFactory::new);

        ParticleFactoryRegistry.getInstance().register(Masi.MAGIC_SWORD_SWEEP, Magic_Sword_Sweep_Particle.Factory::new);

    }
    private void renderBar(DrawContext context, float tickDelta) {

        PlayerEntity player = MC.player;

        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

        int scaledWidth = context.getScaledWindowWidth();
        int scaledHeight = context.getScaledWindowHeight();

        int singFinishTick = -1;
        if(player.isUsingItem() && player.getActiveHand()==Hand.MAIN_HAND){
            if(stack.getItem() instanceof Staff staff){
                if(Staff.UsersMagic.get(player) instanceof Magic magic){
                    singFinishTick = magic.singFinishTick();
                }
            } else if (stack.getItem() instanceof MagicSword) {
                singFinishTick = 100;
            }else if(stack.getItem() instanceof ArcaneBow){
                singFinishTick = 100;
            }

            if(singFinishTick!=-1){
                int width;
                if(player.getItemUseTime()>=singFinishTick){
                    width = 92;
                }else {
                    width = (int)((float)(player.getItemUseTime()) / singFinishTick * 92);
                }

                int m = scaledWidth / 2 -45;
                int o = scaledHeight - 90;

                context.drawTexture(MASI_BAR_TEXTURE, m, o, 0, 0, 92, 5);
                context.drawTexture(MASI_BAR_TEXTURE, m, o, 0, 5, width, 5);
                if(width==92){
                    context.drawTexture(MASI_BAR_TEXTURE, m, o, 0, 10, width, 5);
                }
            }
        }

        if(MagicUtil.MAX_ENERGY.get(player)!=null){
            int width;
            if(MagicUtil.ENERGY.get(player)>=MagicUtil.MAX_ENERGY.get(player)){
                width = 92;
            }else {
                width = (int)((float)(MagicUtil.ENERGY.get(player)) / MagicUtil.MAX_ENERGY.get(player) * 92);
            }

            int m = scaledWidth / 2 + 10;
            int o = scaledHeight - 60;

            context.drawTexture(MASI_BAR_TEXTURE, m, o, 0, 15, 92, 7);
            context.drawTexture(MASI_BAR_TEXTURE, m, o, 0, 22, width, 7);
            context.drawText(MC.textRenderer, Text.of(MagicUtil.ENERGY.get(player) + " / " +MagicUtil.MAX_ENERGY.get(player)),m+20,o,0xFFFFFF,false);
        }
    }

    private void registerPullPredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("pull"), (stack, world, entity, seed) -> {
            if (entity == null) {
                return 0.0f;
            }
            if (entity.getActiveItem() != stack) {
                return 0.0f;
            }
            return (float) (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / 20.0f;
        });

        ModelPredicateProviderRegistry.register(item, new Identifier("pulling"), (stack, world, entity, seed) -> entity != null && entity.isUsingItem() && entity.getActiveItem() == stack ? 1.0f : 0.0f);
    }
    private void registerInheritToolPredicate(Item item) {
        ModelPredicateProviderRegistry.register(item, new Identifier("tool"), (stack, world, entity, seed) -> {
            if(stack.getNbt()!=null && stack.getNbt().contains("tool")){
                return stack.getNbt().getFloat("tool");
            }
            return 0.0f;
        });

    }
}