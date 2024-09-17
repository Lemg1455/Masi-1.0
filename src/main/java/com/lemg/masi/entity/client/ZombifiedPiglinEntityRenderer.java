package com.lemg.masi.entity.client;

import com.google.common.collect.ImmutableMap;
import com.lemg.masi.Masi;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.ArmorEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLoader;
import net.minecraft.client.render.entity.model.PiglinEntityModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AbstractPiglinEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.Identifier;

import java.util.Map;

@Environment(value= EnvType.CLIENT)
public class ZombifiedPiglinEntityRenderer
        extends BipedEntityRenderer<MobEntity, PiglinEntityModel<MobEntity>> {
    public static final Identifier TEXTURE = new Identifier("textures/entity/piglin/zombified_piglin.png");

    private static final float HORIZONTAL_SCALE = 1.0019531f;

    public ZombifiedPiglinEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer mainLayer, EntityModelLayer innerArmorLayer, EntityModelLayer outerArmorLayer, boolean zombie) {
        super(ctx, getPiglinModel(ctx.getModelLoader(), mainLayer, zombie), 0.5f, 1.0019531f, 1.0f, 1.0019531f);
        this.addFeature(new ArmorFeatureRenderer(this, new ArmorEntityModel(ctx.getPart(innerArmorLayer)), new ArmorEntityModel(ctx.getPart(outerArmorLayer)), ctx.getModelManager()));
    }

    private static PiglinEntityModel<MobEntity> getPiglinModel(EntityModelLoader modelLoader, EntityModelLayer layer, boolean zombie) {
        PiglinEntityModel<MobEntity> piglinEntityModel = new PiglinEntityModel<MobEntity>(modelLoader.getModelPart(layer));
        if (zombie) {
            piglinEntityModel.rightEar.visible = false;
        }
        return piglinEntityModel;
    }

    @Override
    public Identifier getTexture(MobEntity mobEntity) {
        return TEXTURE;
    }

    @Override
    protected boolean isShaking(MobEntity mobEntity) {
        return super.isShaking(mobEntity) || mobEntity instanceof AbstractPiglinEntity && ((AbstractPiglinEntity)mobEntity).shouldZombify();
    }

}


