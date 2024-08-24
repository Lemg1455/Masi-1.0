package com.lemg.masi;


import com.lemg.masi.event.KeyInputHandler;
import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.item.Staff;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.particles.Circle_Forward_Particle;
import com.lemg.masi.particles.Circle_Ground_Particle;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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

        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_FORWARD_BLUE, Circle_Forward_Particle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(Masi.CIRCLE_GROUND_BLUE, Circle_Ground_Particle.Factory::new);



    }
    private void renderBar(DrawContext context, float tickDelta) {

        PlayerEntity player = MC.player;

        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);

        int singFinishTick = 0;
        if(player.isUsingItem() && stack.getItem() instanceof Staff staff){
            if(staff.magic.getItem() instanceof Magic magic){
                singFinishTick = magic.singFinishTick();
            }

            int width;
            if(player.getItemUseTime()>=singFinishTick){
                width = 92;
            }else {
                width = (int)((float)(player.getItemUseTime()) / singFinishTick * 92);
            }
            context.drawTexture(MASI_BAR_TEXTURE, 170, 150, 0, 0, 92, 5);
            context.drawTexture(MASI_BAR_TEXTURE, 170, 150, 0, 5, width, 5);
            if(width==92){
                context.drawTexture(MASI_BAR_TEXTURE, 170, 150, 0, 10, width, 5);
            }

        }

        if(MagicUtil.MAX_ENERGY.get(player)!=null){
            int width;
            if(MagicUtil.ENERGY.get(player)>=MagicUtil.MAX_ENERGY.get(player)){
                width = 92;
            }else {
                width = (int)((float)(MagicUtil.ENERGY.get(player)) / MagicUtil.MAX_ENERGY.get(player) * 92);
            }
            context.drawTexture(MASI_BAR_TEXTURE, 220, 180, 0, 15, 92, 7);
            context.drawTexture(MASI_BAR_TEXTURE, 220, 180, 0, 22, width, 7);
            context.drawText(MC.textRenderer, Text.of(MagicUtil.ENERGY.get(player) + " / " +MagicUtil.MAX_ENERGY.get(player)),240,180,0xFFFFFF,false);
        }

    }
}