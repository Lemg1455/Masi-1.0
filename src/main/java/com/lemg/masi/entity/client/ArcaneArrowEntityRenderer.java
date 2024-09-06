package com.lemg.masi.entity.client;

import com.lemg.masi.Masi;
import com.lemg.masi.entity.ArcaneArrowEntity;
import com.lemg.masi.entity.ArcaneMinionEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

@Environment(value = EnvType.CLIENT)
public class ArcaneArrowEntityRenderer extends ProjectileEntityRenderer<ArcaneArrowEntity> {
    public static final Identifier TEXTURE = new Identifier(Masi.MOD_ID, "textures/entity/arcane_arrow.png");


    public ArcaneArrowEntityRenderer(EntityRendererFactory.Context context) {
        super(context);

    }

    @Override
    public Identifier getTexture(ArcaneArrowEntity arcaneArrowEntity) {
        return TEXTURE;
    }
}
