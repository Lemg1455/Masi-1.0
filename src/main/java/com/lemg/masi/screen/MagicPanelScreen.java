package com.lemg.masi.screen;

import com.google.common.collect.Lists;
import com.lemg.masi.Masi;
import com.lemg.masi.item.MagicGroups;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.lemg.masi.item.Magics.Magic;
import com.lemg.masi.network.ModMessage;
import com.lemg.masi.util.MagicUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.HotbarStorage;
import net.minecraft.client.option.HotbarStorageEntry;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;
/**
 *魔法学习与配置的界面
 */
@Environment(value=EnvType.CLIENT)
public class MagicPanelScreen
        extends AbstractInventoryScreen<MagicPanelScreen.CreativeScreenHandler> {
    //顶部类别按钮背景
    private static final Identifier TEXTURE = new Identifier("textures/gui/container/creative_inventory/tabs.png");
    private static final Identifier MAGIC_TAB_TEXTURE = new Identifier(Masi.MOD_ID,"textures/gui/masi_tab.png");
    private static final String TAB_TEXTURE_PREFIX = "textures/gui/container/creative_inventory/tab_items.png";
    private static final String CUSTOM_CREATIVE_LOCK_KEY = "CustomCreativeLock";

    static final SimpleInventory INVENTORY = new SimpleInventory(45);//45格的简单背包

    private static List<Object> selectedTab = MagicGroups.getDefaultTab();//物品组的默认选项卡
    private float scrollPosition;//滚动条位置
    private boolean scrolling;//正在滚动

    private boolean ignoreTypedCharacter;
    private boolean lastClickOutsideBounds;
    private final Set<TagKey<Item>> searchResultTags = new HashSet<TagKey<Item>>();
    public final PlayerEntity player;

    public MagicPanelScreen(PlayerEntity player, FeatureSet enabledFeatures) {
        super(new MagicPanelScreen.CreativeScreenHandler(player), player.getInventory(), ScreenTexts.EMPTY);
        player.currentScreenHandler = this.handler;//当前屏幕处理器
        this.backgroundHeight = 136;//背景宽高
        this.backgroundWidth = 195;
        this.player = player;

    }

    //tick
    @Override
    public void handledScreenTick() {
        super.handledScreenTick();
        if (this.client == null) {
            return;
        }
    }

    @Override
    protected void onMouseClick(@Nullable Slot slot, int slotId, int button, SlotActionType actionType) {
        boolean bl = actionType == SlotActionType.QUICK_MOVE;
        SlotActionType slotActionType = actionType = slotId == -999 && actionType == SlotActionType.PICKUP ? SlotActionType.THROW : actionType;
        //鼠标点击的槽位不为空，或者点击类别栏，或者进行快速合成
        if (slot != null) {
            if (!slot.canTakeItems(this.client.player)) {
                return;//检查槽位是否有效
            }
            if (slot.inventory == INVENTORY) {
                //获取光标物品
                ItemStack itemStack = this.handler.getCursorStack();
                //获取槽位物品
                ItemStack itemStack2 = slot.getStack();

                if(!itemStack2.isEmpty()){
                    if(itemStack2.getItem() instanceof Magic magic){
                        if(client.player==null){
                            return;
                        }
                        List<ItemStack> Learned_magics = MagicUtil.LEARNED_MAGICS.get(client.player);

                        Boolean b1;
                        if(Learned_magics==null){
                            Learned_magics = new ArrayList<>();
                            b1 = true;
                        }else {
                            b1 = !MagicUtil.getStacksItems(Learned_magics).contains(itemStack2.getItem());
                        }
                        if(b1){
                            if(client.player.getAbilities().creativeMode || client.player.experienceLevel>=magic.studyNeed()){
                                if(!client.player.getAbilities().creativeMode){
                                    client.player.addExperienceLevels(-magic.studyNeed());
                                }
                                Learned_magics.add(itemStack2);
                                MagicUtil.LEARNED_MAGICS.put(client.player,Learned_magics);

                                PacketByteBuf buf = PacketByteBufs.create();
                                buf.writeItemStack(itemStack2);
                                buf.writeInt(magic.studyNeed());
                                ClientPlayNetworking.send(ModMessage.LEARNED_MAGICS_ID, buf);

                                client.player.sendMessage(Text.translatable("masi.panel.learned.message"),true);
                            }else {
                                client.player.sendMessage(Text.translatable("masi.panel.exp.message"),true);
                            }
                            return;
                        }
                    }
                }

                //如果动作是交换
                if (actionType == SlotActionType.SWAP) {
                    //槽位不等于空
                    if (!itemStack2.isEmpty()) {
                        //this.client.player.getInventory().setStack(button, itemStack2.copyWithCount(itemStack2.getMaxCount()));
                        this.client.player.playerScreenHandler.sendContentUpdates();
                    }
                    return;
                }
                //操作为克隆
                if (actionType == SlotActionType.CLONE) {
                    //光标为空，槽位不为空
                    if ((this.handler).getCursorStack().isEmpty() && slot.hasStack()) {
                        ItemStack itemStack3 = slot.getStack();
                        //光标的物品等于槽位的最大数量
                        (this.handler).setCursorStack(itemStack3.copyWithCount(itemStack3.getMaxCount()));
                    }
                    return;
                }
                //如果操作为丢弃，槽位不为空，根据shift丢弃物品
                if (actionType == SlotActionType.THROW) {
                    if (!itemStack2.isEmpty()) {

                    }
                    return;
                }
                if (itemStack2.isEmpty() || !itemStack.isEmpty()) {
                    //一次性放下光标的东西，或一次放一个，不过并不会影响槽位
                    if (button == 0) {
                        (this.handler).setCursorStack(ItemStack.EMPTY);
                    } else if (!(this.handler).getCursorStack().isEmpty()) {
                        (this.handler).getCursorStack().decrement(1);
                    }
                } else {
                    //快速移动到光标上的数量
                    int j = bl ? itemStack2.getMaxCount() : itemStack2.getCount();
                    (this.handler).setCursorStack(itemStack2.copyWithCount(j));
                }
                ///和制作还有快捷栏有关？
            } else if (this.handler != null) {
                this.handler.onSlotClick(slot.id, button, actionType, this.client.player);
                this.client.player.playerScreenHandler.sendContentUpdates();
            }
            //光标上不为空，然后点击了背包以外的区域，丢出光标上的物品
        } else if (!(this.handler).getCursorStack().isEmpty() && this.lastClickOutsideBounds) {
           (this.handler).setCursorStack(ItemStack.EMPTY);
        }
    }

    //添加组件
    @Override
    protected void init() {
        super.init();
        //类别栏
        List<Object> group = selectedTab;
        selectedTab = MagicGroups.getDefaultTab();
        this.setSelectedTab(group);
    }

    //关于搜索和滚动条
    @Override
    public void resize(MinecraftClient client, int width, int height) {
        int i = this.handler.getRow(this.scrollPosition);
        this.init(client, width, height);
        this.scrollPosition = this.handler.getScrollPosition(i);
        this.handler.scrollItems(this.scrollPosition);
    }

    @Override
    public void removed() {
        super.removed();
        if (this.client.player != null && this.client.player.getInventory() != null) {
            //this.client.player.playerScreenHandler.removeListener(this.listener);
        }
        MagicUtil.EQUIP_MAGICS.put(player,this.handler.simpleInventory.stacks);
        PacketByteBuf buf = PacketByteBufs.create();
        for(ItemStack itemStack : this.handler.simpleInventory.stacks){
            buf.writeItemStack(itemStack);
        }
        ClientPlayNetworking.send(ModMessage.EQUIP_MAGICS_ID, buf);
    }

    //输入方式，搜索或者聊天
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        this.ignoreTypedCharacter = false;

        if (this.client.options.chatKey.matchesKey(keyCode, scanCode)) {
            this.ignoreTypedCharacter = true;
            this.setSelectedTab(MagicGroups.getSearchGroup());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        this.ignoreTypedCharacter = false;
        return super.keyReleased(keyCode, scanCode, modifiers);
    }


    //应该是往类别栏的背景上画图标
    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, (Text)(selectedTab.get(1)), 8, 6, 0x404040, false);
    }

    //鼠标点击
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double d = mouseX - (double)this.x;
            double e = mouseY - (double)this.y;
            for (List<Object> group : MagicGroups.magicGroups) {
                if (!this.isClickInTab(group, d, e)) continue;
                return true;
            }
            if (this.isClickInScrollbar(mouseX, mouseY)) {
                this.scrolling = this.hasScrollbar();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    //鼠标释放
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            double d = mouseX - (double)this.x;
            double e = mouseY - (double)this.y;
            this.scrolling = false;
            for (List<Object> group : MagicGroups.magicGroups) {
                if (!this.isClickInTab(group, d, e)) continue;
                this.setSelectedTab(group);
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    //是否有滚动条
    private boolean hasScrollbar() {
        return (this.handler).shouldShowScrollbar();
    }

    //根据选中的选项卡设置内容
    private void setSelectedTab(List<Object> group) {
        selectedTab = group;
        this.cursorDragSlots.clear();
        (this.handler).itemList.clear();
        this.endTouchDrag();
        if (group!=null) {
            //点开类别栏后里面显示的东西
            (this.handler).itemList.addAll(MagicUtil.getMagicStacks(selectedTab));
        }
        this.scrollPosition = 0.0f;
        this.handler.scrollItems(0.0f);
    }

    //滚动条
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (!this.hasScrollbar()) {
            return false;
        }
        this.scrollPosition = this.handler.getScrollPosition(this.scrollPosition, amount);
        this.handler.scrollItems(this.scrollPosition);
        return true;
    }

    //点击背包之外
    @Override
    protected boolean isClickOutsideBounds(double mouseX, double mouseY, int left, int top, int button) {
        boolean bl = mouseX < (double)left || mouseY < (double)top || mouseX >= (double)(left + this.backgroundWidth) || mouseY >= (double)(top + this.backgroundHeight);
        this.lastClickOutsideBounds = bl && !this.isClickInTab(selectedTab, mouseX, mouseY);
        return this.lastClickOutsideBounds;
    }

    protected boolean isClickInScrollbar(double mouseX, double mouseY) {
        int i = this.x;
        int j = this.y;
        int k = i + 175;
        int l = j + 18;
        int m = k + 14;
        int n = l + 112;
        return mouseX >= (double)k && mouseY >= (double)l && mouseX < (double)m && mouseY < (double)n;
    }

    //鼠标拖动
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.scrolling) {
            int i = this.y + 18;
            int j = i + 112;
            this.scrollPosition = ((float)mouseY - (float)i - 7.5f) / ((float)(j - i) - 15.0f);
            this.scrollPosition = MathHelper.clamp(this.scrollPosition, 0.0f, 1.0f);
            this.handler.scrollItems(this.scrollPosition);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        context.getMatrices().push();
        context.getMatrices().translate(this.x, this.y, 0.0f);
        this.renderMagicTab(context);
        context.getMatrices().pop();
        for (List<Object> group : MagicGroups.magicGroups) {
            if (this.renderTabTooltipIfHovered(context, group, mouseX, mouseY)) break;
        }
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    //提示信息
    @Override
    public List<Text> getTooltipFromItem(ItemStack stack) {
        boolean bl = this.focusedSlot != null && this.focusedSlot instanceof MagicPanelScreen.LockableSlot;
        boolean bl2 = true;
        boolean bl3 = false;
        TooltipContext.Default default_ = this.client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.BASIC;
        TooltipContext.Default tooltipContext = bl ? default_.withCreative() : default_;
        List<Text> list = stack.getTooltip(this.client.player, tooltipContext);
        if (!bl2 || !bl) {
            ArrayList<Text> list2 = Lists.newArrayList(list);
            if (bl3 && bl) {
                this.searchResultTags.forEach(tagKey -> {
                    if (stack.isIn((TagKey<Item>)tagKey)) {
                        list2.add(1, Text.literal("#" + tagKey.id()).formatted(Formatting.DARK_PURPLE));
                    }
                });
            }
            int i = 1;
            for (List<Object> group : MagicGroups.magicGroups) {
                if (!MagicUtil.getMagicStacks(group).contains(stack)) continue;
                list2.add(i++, ((Text) group.get(1)).copy().formatted(Formatting.BLUE));
            }
            return list2;
        }
        return list;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        for (List<Object> group : MagicGroups.magicGroups) {
            if (group == selectedTab) continue;
            this.renderTabIcon(context, group);
        }
        context.drawTexture(new Identifier(TAB_TEXTURE_PREFIX), this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
        int i = this.x + 175;
        int j = this.y + 18;
        int k = j + 112;
        context.drawTexture(TEXTURE, i, j + (int)((float)(k - j - 17) * this.scrollPosition), 232 + (this.hasScrollbar() ? 0 : 12), 0, 12, 15);
        this.renderTabIcon(context, selectedTab);
        //this.renderMagicTab(context);
    }

    private int getTabX(List<Object> group) {
        int i = (int) group.get(3);
        int j = 27;
        int k = 27 * i;
        return k;
    }

    private int getTabY(List<Object> group) {
        int i = 0;
        i = (int)group.get(2) == 1 ? (i += this.backgroundHeight) : (i -= 32);
        return i;
    }

    protected boolean isClickInTab(List<Object> group, double mouseX, double mouseY) {
        int i = this.getTabX(group);
        int j = this.getTabY(group);
        return mouseX >= (double)i && mouseX <= (double)(i + 26) && mouseY >= (double)j && mouseY <= (double)(j + 32);
    }

    protected boolean renderTabTooltipIfHovered(DrawContext context, List<Object> group, int mouseX, int mouseY) {
        int j;
        int i = this.getTabX(group);
        if (this.isPointWithinBounds(i + 3, (j = this.getTabY(group)) + 3, 21, 27, mouseX, mouseY)) {
            context.drawTooltip(this.textRenderer, (Text) group.get(1), mouseX, mouseY);
            return true;
        }
        return false;
    }

    //画图标
    protected void renderTabIcon(DrawContext context, List<Object> group){
        boolean bl = group == selectedTab;
        boolean bl2 = (int)group.get(2) == 0;
        //int i = group.getColumn();
        int i = (int)group.get(3);
        int j = i * 26;
        int k = 0;
        int l = this.x + this.getTabX(group);
        int m = this.y;
        int n = 32;
        if (bl) {
            k += 32;
        }
        if (bl2) {
            m -= 28;
        } else {
            k += 64;
            m += this.backgroundHeight - 4;
        }
        context.drawTexture(TEXTURE, l, m, j, k, 26, 32);
        context.getMatrices().push();
        context.getMatrices().translate(0.0f, 0.0f, 100.0f);
        int n2 = bl2 ? 1 : -1;
        ItemStack itemStack = (ItemStack) group.get(4);
        context.drawItem(itemStack, l += 5, m += 8 + n2);
        context.drawItemInSlot(this.textRenderer, itemStack, l, m);
        context.getMatrices().pop();
    }

    protected void renderMagicTab(DrawContext context){
        for (Slot slot : this.handler.slots){
            if(slot.hasStack() && slot.inventory == INVENTORY){
                if(slot.getStack().getItem() instanceof Magic magic){
                    List<ItemStack> Learned_magics = MagicUtil.LEARNED_MAGICS.get(client.player);
                    if(Learned_magics!=null){
                        if(!MagicUtil.getStacksItems(Learned_magics).contains(slot.getStack().getItem())){
                            context.drawTexture(MAGIC_TAB_TEXTURE, slot.x, slot.y, 0, 0, 17, 17);
                        }
                    }else {
                        context.drawTexture(MAGIC_TAB_TEXTURE, slot.x, slot.y, 0, 0, 17, 17);
                    }
                }
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class CreativeScreenHandler
            extends ScreenHandler {
        public final DefaultedList<ItemStack> itemList = DefaultedList.of();
        private final ScreenHandler parent;
        public SimpleInventory simpleInventory = new SimpleInventory(9);

        public CreativeScreenHandler(PlayerEntity player) {
            super(null, 0);
            int i;
            this.parent = player.playerScreenHandler;
            PlayerInventory playerInventory = player.getInventory();

            List<ItemStack> equip_magics = MagicUtil.EQUIP_MAGICS.get(player);
            DefaultedList<ItemStack> inventory = DefaultedList.ofSize(9,ItemStack.EMPTY);
            if(equip_magics!=null){
                for(i = 0; i < equip_magics.size(); ++i){
                    inventory.set(i,equip_magics.get(i));
                }
            }

            for (i = 0; i < 5; ++i) {
                for (int j = 0; j < 9; ++j) {
                    this.addSlot(new MagicPanelScreen.LockableSlot(INVENTORY, i * 9 + j, 9 + j * 18, 18 + i * 18));
                }
            }
            for (i = 0; i < 9; ++i) {
                this.addSlot(new Slot(simpleInventory, i, 9 + i * 18, 112));
                simpleInventory.setStack(i,inventory.get(i));
            }
            this.scrollItems(0.0f);
        }

        @Override
        public boolean canUse(PlayerEntity player) {
            return true;
        }

        protected int getOverflowRows() {
            return MathHelper.ceilDiv(this.itemList.size(), 9) - 5;
        }

        protected int getRow(float scroll) {
            return Math.max((int)((double)(scroll * (float)this.getOverflowRows()) + 0.5), 0);
        }

        protected float getScrollPosition(int row) {
            return MathHelper.clamp((float)row / (float)this.getOverflowRows(), 0.0f, 1.0f);
        }

        protected float getScrollPosition(float current, double amount) {
            return MathHelper.clamp(current - (float)(amount / (double)this.getOverflowRows()), 0.0f, 1.0f);
        }

        public void scrollItems(float position) {
            int i = this.getRow(position);
            for (int j = 0; j < 5; ++j) {
                for (int k = 0; k < 9; ++k) {
                    int l = k + (j + i) * 9;
                    if (l >= 0 && l < this.itemList.size()) {
                        INVENTORY.setStack(k + j * 9, this.itemList.get(l));
                        continue;
                    }
                    INVENTORY.setStack(k + j * 9, ItemStack.EMPTY);
                }
            }
        }

        public boolean shouldShowScrollbar() {
            return this.itemList.size() > 45;
        }

        @Override
        public ItemStack quickMove(PlayerEntity player, int slot) {
            Slot slot2;
            if (slot >= this.slots.size() - 9 && slot < this.slots.size() && (slot2 = (Slot)this.slots.get(slot)) != null && slot2.hasStack()) {
                slot2.setStack(ItemStack.EMPTY);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public boolean canInsertIntoSlot(ItemStack stack, Slot slot) {
            return slot.inventory != INVENTORY;
        }

        @Override
        public boolean canInsertIntoSlot(Slot slot) {
            return slot.inventory != INVENTORY;
        }

        @Override
        public ItemStack getCursorStack() {
            return this.parent.getCursorStack();
        }

        @Override
        public void setCursorStack(ItemStack stack) {
            this.parent.setCursorStack(stack);
        }
    }
    @Environment(value=EnvType.CLIENT)
    static class LockableSlot
            extends Slot {
        public LockableSlot(Inventory inventory, int i, int j, int k) {
            super(inventory, i, j, k);
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            ItemStack itemStack = this.getStack();
            if (super.canTakeItems(playerEntity) && !itemStack.isEmpty()) {
                return itemStack.isItemEnabled(playerEntity.getWorld().getEnabledFeatures()) && itemStack.getSubNbt(MagicPanelScreen.CUSTOM_CREATIVE_LOCK_KEY) == null;
            }
            return itemStack.isEmpty();
        }
    }
}

