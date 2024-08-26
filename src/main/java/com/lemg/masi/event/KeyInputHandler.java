package com.lemg.masi.event;

import com.lemg.masi.network.ModMessage;
import com.lemg.masi.screen.MagicPanelScreen;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public final class KeyInputHandler {
    public static final String KEY_CATEGORY_MASI = "key.category.masi.masi";

    public static final String KEY_CHANGE_LEFT = "key.masi.change_left";
    public static final String KEY_CHANGE = "key.masi.change";
    public static KeyBinding change_left;
    public static KeyBinding change;
    public static int solt = 0;


    public static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if(client.player!=null){
                if(change.wasPressed()) {
                    if(MagicUtil.MAGIC_CHOOSE.get(client.player)!=null){
                        solt = MagicUtil.MAGIC_CHOOSE.get(client.player);
                        solt++;
                        List<ItemStack> equip_magics = MagicUtil.EQUIP_MAGICS.get(client.player);

                        List<ItemStack> stacks = new ArrayList<>();
                        if(equip_magics!=null){
                            for(ItemStack itemStack : equip_magics){
                                if(!itemStack.isEmpty()){
                                    stacks.add(itemStack);
                                }
                            }
                        }

                        if(solt >= stacks.size()){solt=0;}
                        MagicUtil.MAGIC_CHOOSE.put(client.player,solt);

                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeInt(solt);
                        ClientPlayNetworking.send(ModMessage.MAGIC_CHOOSE_ID, buf);
                    }
                }else if(change_left.wasPressed()) {
                    if(MagicUtil.MAGIC_CHOOSE.get(client.player)!=null){
                        solt = MagicUtil.MAGIC_CHOOSE.get(client.player);
                        solt--;
                        List<ItemStack> equip_magics = MagicUtil.EQUIP_MAGICS.get(client.player);

                        List<ItemStack> stacks = new ArrayList<>();
                        if(equip_magics!=null){
                            for(ItemStack itemStack : equip_magics){
                                if(!itemStack.isEmpty()){
                                    stacks.add(itemStack);
                                }
                            }
                        }

                        if(solt<0){solt=stacks.size()-1;}
                        MagicUtil.MAGIC_CHOOSE.put(client.player,solt);

                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeInt(solt);
                        ClientPlayNetworking.send(ModMessage.MAGIC_CHOOSE_ID, buf);
                    }
                }
            }
        });
    }

    public static void register(){
        change = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_CHANGE,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY_MASI
        ));
        change_left = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_CHANGE_LEFT,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                KEY_CATEGORY_MASI
        ));
        registerKeyInputs();
    }
}