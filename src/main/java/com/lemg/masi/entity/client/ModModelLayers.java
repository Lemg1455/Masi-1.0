package com.lemg.masi.entity.client;

import com.lemg.masi.Masi;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class ModModelLayers {
    public static final EntityModelLayer ARCANE_MINION =
            new EntityModelLayer(new Identifier(Masi.MOD_ID,"arcane_minion"),"main");

    public static final EntityModelLayer SWORD_ENERGY =
            new EntityModelLayer(new Identifier(Masi.MOD_ID,"sword_energy"),"main");
    public static final EntityModelLayer METEORITE =
            new EntityModelLayer(new Identifier(Masi.MOD_ID,"meteorite_entity"),"main");
    public static final EntityModelLayer SWORD_MAN =
            new EntityModelLayer(new Identifier(Masi.MOD_ID,"sword_man"),"main");

}