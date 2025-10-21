package com.tiomadre.foragersinsight.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiomadre.foragersinsight.common.block.entity.DiffuserBlockEntity;
import com.tiomadre.foragersinsight.common.diffuser.DiffuserScent;
import com.tiomadre.foragersinsight.common.gui.DiffuserMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DiffuserScreen extends AbstractContainerScreen<DiffuserMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("foragersinsight", "textures/gui/diffuser.png");

    private static final int ARROW_U = 179;
    private static final int ARROW_V = 18;
    private static final int ARROW_HEIGHT = 6;
    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_X = 129;
    private static final int ARROW_Y = 20;

    private static final int EXTINGUISH_BUTTON_SIZE = 12;
    private static final int EXTINGUISH_BUTTON_X = 129;
    private static final int EXTINGUISH_BUTTON_Y = 58;

    private static final int ICON_SIZE = 16;
    private Button extinguishButton;

    public DiffuserScreen(DiffuserMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;

        this.titleLabelX = 8;
        this.titleLabelY = 6;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = this.imageHeight - 96 + 2;
    }
    @Override
    protected void init() {
        super.init();
        int buttonX = this.leftPos + EXTINGUISH_BUTTON_X;
        int buttonY = this.topPos + EXTINGUISH_BUTTON_Y;

        this.extinguishButton = Button.builder(Component.empty(), this::onExtinguishPressed)
                .bounds(buttonX, buttonY, EXTINGUISH_BUTTON_SIZE, EXTINGUISH_BUTTON_SIZE)
                .build();
        this.extinguishButton.setAlpha(0.0F);
        this.extinguishButton.setTooltip(Tooltip.create(
                Component.translatable("gui.foragersinsight.diffuser.extinguish")));
        this.addRenderableWidget(this.extinguishButton);
        updateButtonState();
    }
    private void updateButtonState() {
        if (this.extinguishButton != null) {
            this.extinguishButton.active = this.menu.getActiveScent().isPresent();
        }
    }

    private void onExtinguishPressed(Button button) {
        if (this.minecraft == null || this.minecraft.gameMode == null) {
            return;
        }
        if (this.menu.getActiveScent().isEmpty()) {
            return;
        }
        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, DiffuserMenu.BUTTON_EXTINGUISH);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtonState();
    }


    @Override
    protected void renderBg(@NotNull GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        gui.blit(TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight);
    int progress = Math.min(this.menu.getCraftProgress(), ARROW_WIDTH);
        if (progress > 0) {
            gui.blit(TEXTURE,
                    left + ARROW_X, top + ARROW_Y,
                    ARROW_U, ARROW_V,
                    progress, ARROW_HEIGHT);
        }

        renderScentIcon(gui, left, top);
    }

    @Override
    public void render(@NotNull GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTicks);
        this.renderTooltip(gui, mouseX, mouseY);
        this.renderScentTooltip(gui, mouseX, mouseY);
    }

    private void renderScentIcon(GuiGraphics gui, int left, int top) {
        Optional<DiffuserScent> scent = this.menu.getActiveScent();
        if (scent.isEmpty()) {
            return;
        }
        Slot slot = this.menu.getSlot(DiffuserBlockEntity.RESULT_SLOT_INDEX);
        int iconX = left + slot.x;
        int iconY = top + slot.y;
        gui.blit(scent.get().icon(), iconX, iconY, 0, 0, ICON_SIZE, ICON_SIZE, ICON_SIZE, ICON_SIZE);
    }

    private void renderScentTooltip(GuiGraphics gui, int mouseX, int mouseY) {
        Optional<DiffuserScent> scent = this.menu.getActiveScent();
        if (scent.isEmpty()) {
            return;
        }
        Slot slot = this.menu.getSlot(DiffuserBlockEntity.RESULT_SLOT_INDEX);
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        int iconX = left + slot.x;
        int iconY = top + slot.y;
        if (mouseX >= iconX && mouseX < iconX + ICON_SIZE && mouseY >= iconY && mouseY < iconY + ICON_SIZE) {
            gui.renderComponentTooltip(this.font, scent.get().tooltip(), mouseX, mouseY);
        }
    }
}