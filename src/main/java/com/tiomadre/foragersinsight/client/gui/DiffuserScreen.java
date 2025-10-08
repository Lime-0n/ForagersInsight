package com.tiomadre.foragersinsight.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.tiomadre.foragersinsight.common.gui.DiffuserMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class DiffuserScreen extends AbstractContainerScreen<DiffuserMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("foragersinsight", "textures/gui/diffuser.png");


    private static final int FLAME_U = 176;
    private static final int FLAME_V = 0;
    private static final int FLAME_W = 14;
    private static final int FLAME_H = 14;

    private static final int ARROW_U = 176;
    private static final int ARROW_V = 14;
    private static final int ARROW_W = 24;
    private static final int ARROW_H = 17;

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

        int flameX = left + 57;
        int flameY = top + 36;

        if (this.menu.isLit()) {
            int lit = this.menu.getLitProgress();
            gui.blit(TEXTURE,
                    flameX, flameY + (FLAME_H - lit),
                    FLAME_U, FLAME_V + (FLAME_H - lit),
                    FLAME_W, lit);
        }

        int arrowX = left + 97;
        int arrowY = top + 34;
        int progress = this.menu.getCraftProgress();

        if (progress > 0) {
            gui.blit(TEXTURE,
                    arrowX, arrowY,
                    ARROW_U, ARROW_V,
                    progress, ARROW_H);
        }
    }

    @Override
    public void render(@NotNull GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(gui);
        super.render(gui, mouseX, mouseY, partialTicks);
        this.renderTooltip(gui, mouseX, mouseY);
    }
}
