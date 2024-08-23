package com.lemg.masi.mixin;

import com.lemg.masi.Masi;
import com.lemg.masi.event.KeyInputHandler;
import com.lemg.masi.util.MagicUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(InGameHud.class)
public class InGameHudMixin {

	@Shadow @Final private MinecraftClient client;
	@Unique
	private static final Identifier MAGIC_WIDGETS_TEXTURE = new Identifier(Masi.MOD_ID,"textures/gui/magic_widgets.png");

	@Inject(at = @At("TAIL"), method = "renderHotbar")
	public void renderHotbar(float tickDelta, DrawContext context, CallbackInfo ci) {

		context.getMatrices().push();
		context.getMatrices().translate(0.0f, 0.0f, -90.0f);
		context.drawTexture(MAGIC_WIDGETS_TEXTURE, 40, 200, 0, 0, 68, 24);
		context.getMatrices().pop();

		PlayerEntity playerEntity = this.getCameraPlayer();
		List<ItemStack> equip_magics = MagicUtil.EQUIP_MAGICS.get(playerEntity);
		List<ItemStack> stacks = new ArrayList<>();
		if(equip_magics!=null){
			for(ItemStack itemStack : equip_magics){
				if(!itemStack.isEmpty()){
					stacks.add(itemStack);
				}
			}
		}


		if(!stacks.isEmpty() && MagicUtil.MAGIC_CHOOSE.get(playerEntity)!=null){
			int i = 0;
			int j = 0;

			int solt = MagicUtil.MAGIC_CHOOSE.get(playerEntity);
			if(solt != stacks.size()-1){j = solt+1;}
			if(solt == 0){i = stacks.size()-1;}else {i=solt-1;}

			this.renderHotbarItem(context, 43, 204, tickDelta, playerEntity, stacks.get(i), 12);
			this.renderHotbarItem(context, 66, 204, tickDelta, playerEntity, stacks.get(solt), 11);
			this.renderHotbarItem(context, 89, 204, tickDelta, playerEntity, stacks.get(j), 13);
		}
	}
	@Unique
	private void renderHotbarItem(DrawContext context, int x, int y, float f, PlayerEntity player, ItemStack stack, int seed) {
		if (stack.isEmpty()) {
			return;
		}

		float g = (float)stack.getBobbingAnimationTime() - f;
		if (g > 0.0f) {
			float h = 1.0f + g / 5.0f;
			context.getMatrices().push();
			context.getMatrices().translate(x + 8, y + 12, 0.0f);
			context.getMatrices().scale(1.0f / h+3, (h + 1.0f) / 2.0f+3, 1.0f+3);
			context.getMatrices().translate(-(x + 8), -(y + 12), 0.0f);
		}
		context.drawItem(player, stack, x, y, seed);
		if (g > 0.0f) {
			context.getMatrices().pop();
		}

//		context.getMatrices().push();
//		context.getMatrices().scale(2.0f,2.0f,2.0f);
//
//		context.drawItemInSlot(this.client.textRenderer, stack, x, y);
//		context.getMatrices().pop();
	}

	@Unique
	private PlayerEntity getCameraPlayer() {
		if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
			return null;
		}
		return (PlayerEntity)this.client.getCameraEntity();
	}
}