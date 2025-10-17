package com.tiomadre.foragersinsight.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiomadre.foragersinsight.common.block.entity.DiffuserBlockEntity;
import com.tiomadre.foragersinsight.common.diffuser.DiffuserScent;
import com.tiomadre.foragersinsight.common.gui.DiffuserMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class DiffuserScreen extends AbstractContainerScreen<DiffuserMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("foragersinsight", "textures/gui/diffuser.png");

    private static final int ARROW_U = 176;
    private static final int ARROW_V = 14;
    private static final int ARROW_H = 17;

    private static final int ICON_SIZE = 16;

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
    protected void renderBg(@NotNull GuiGraphics gui, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;

        gui.blit(TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight);

        int arrowX = left + 97;
        int arrowY = top + 34;
        int progress = this.menu.getCraftProgress();

        if (progress > 0) {
            gui.blit(TEXTURE,
                    arrowX, arrowY,
                    ARROW_U, ARROW_V,
                    progress, ARROW_H);
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