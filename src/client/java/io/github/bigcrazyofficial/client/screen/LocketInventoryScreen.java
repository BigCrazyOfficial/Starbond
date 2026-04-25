package io.github.bigcrazyofficial.client.screen;

import io.github.bigcrazyofficial.Starbond;
import io.github.bigcrazyofficial.menu.LocketInventoryMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class LocketInventoryScreen extends AbstractContainerScreen<LocketInventoryMenu> {
    private static final Identifier CONTAINER_TEXTURE = Identifier.fromNamespaceAndPath(Starbond.MOD_ID, "textures/gui/container/locket_inv_temp.png");
    public LocketInventoryScreen(LocketInventoryMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractBackground(graphics, mouseX, mouseY, delta);
        int xo = (this.width - this.imageWidth) / 2;
        int yo = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, CONTAINER_TEXTURE, xo, yo, 0.0F, 0.0F, this.imageWidth, this.imageHeight, BACKGROUND_TEXTURE_WIDTH, BACKGROUND_TEXTURE_HEIGHT);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        // Render item tooltips
        this.extractTooltip(graphics, mouseX, mouseY);
    }
}
